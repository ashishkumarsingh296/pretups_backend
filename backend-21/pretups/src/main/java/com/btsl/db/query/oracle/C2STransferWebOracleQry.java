package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebQry;

public class C2STransferWebOracleQry implements C2STransferWebQry{

    private Log LOG = LogFactory.getLog(C2STransferWebOracleQry.class.getName());

    @Override
    public PreparedStatement loadC2STransferVOListQry(Connection con, String networkCode, Date fromDate, Date toDate, ArrayList userList, String receiverMsisdn, String transferID, String serviceType, String senderCat,
                                                      ListValueVO user,OperatorUtilI operatorUtilI) throws BTSLBaseException, SQLException {
        //local_index_already_implemented
        String methodName = "loadC2STransferVOListQry";
        String tbl_name = "c2s_transfers";
        final StringBuilder selectQueryStafParent=new StringBuilder("Select parent_id from users where user_id=?");
        ResultSet rs = null;
        String ownerID=null;
        final StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT KV.value errcode,KV2.value txnstatus,U.user_name,ST.name, ");
        selectQueryBuff.append("PROD.short_name, CTRF.transfer_id,CTRF.transfer_date, CTRF.transfer_date_time, ");
        selectQueryBuff.append("CTRF.network_code, sender_id,CTRF.sender_category, CTRF.product_code, ");
        selectQueryBuff.append("CTRF.sender_msisdn, CTRF.receiver_msisdn,CTRF.receiver_network_code, CTRF.transfer_value, ");
        selectQueryBuff.append("CTRF.error_code, CTRF.request_gateway_type, ");
        selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
        selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
        selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
        selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
        selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
        selectQueryBuff.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
        selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type, ");
        selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
        selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
        selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
        selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.sub_service, CTRF.serial_number, CTRF.active_user_id ");

        selectQueryBuff.append(" ,CTRF.cell_id,CTRF.switch_id ");

        if (operatorUtilI.getNewDataAftrTbleMerging(fromDate, toDate)) {
            selectQueryBuff.append(", CTRF.bonus_details, CTRF.promo_previous_balance, CTRF.promo_post_balance, CTRF.promo_previous_expiry, CTRF.promo_new_expiry ");
        } else {
            tbl_name = "c2s_transfers_old";
        }

        selectQueryBuff.append("FROM key_values KV,key_values KV2, products PROD,service_type ST,users U, ");
        selectQueryBuff.append(tbl_name);
        selectQueryBuff.append(" CTRF ");
        selectQueryBuff.append("WHERE CTRF.transfer_date >=? AND CTRF.transfer_date < ?  ");
        if (!BTSLUtil.isNullString(transferID)) {
            selectQueryBuff.append("AND CTRF.transfer_id=? ");
        }
        if (!BTSLUtil.isNullString(receiverMsisdn)) {
            selectQueryBuff.append("AND CTRF.receiver_msisdn=? ");
        }
        if (userList != null && userList.size() == 1) {
            user = (ListValueVO) userList.get(0);
            if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                selectQueryBuff.append("AND CTRF.sender_id = ? AND CTRF.active_user_id= ? ");
            } else {
                if (PretupsI.BCU_USER.equalsIgnoreCase(senderCat) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(senderCat)) {
                    senderCat = user.getCodeName();
                }
                selectQueryBuff.append(" AND CTRF.sender_category = ? AND CTRF.active_user_id= ? ");
            }
        }
        if (!(PretupsI.BCU_USER.equalsIgnoreCase(senderCat) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(senderCat)) && (userList != null && userList.size() > 1)) {
            selectQueryBuff.append("AND CTRF.sender_id = ? ");
        }
        selectQueryBuff.append("AND CTRF.network_code= ? AND U.user_id = CTRF.sender_id ");
        selectQueryBuff.append("AND KV.key(+)=CTRF.error_code AND KV.type(+)= ? ");
        selectQueryBuff.append("AND KV2.key(+)=CTRF.transfer_status AND KV2.type(+)=? ");
        selectQueryBuff.append("AND CTRF.service_type= CASE ? WHEN 'ALL' THEN CTRF.service_type ELSE ? END ");
        selectQueryBuff.append("AND CTRF.product_code=PROD.product_code AND ST.service_type=CTRF.service_type ");
        selectQueryBuff.append("ORDER BY CTRF.service_type,CTRF.transfer_date_time DESC,CTRF.transfer_id ");

        LogFactory.printLog(methodName, selectQueryBuff.toString(), LOG);

        PreparedStatement pstmtSelect = con.prepareStatement(selectQueryBuff.toString());
        int i = 1;
        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
        i++;
        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(toDate, 1)));
        i++;
        if (!BTSLUtil.isNullString(transferID)) {
            pstmtSelect.setString(i, transferID);
            i++;
        }
        if (!BTSLUtil.isNullString(receiverMsisdn)) {
            pstmtSelect.setString(i, receiverMsisdn);
            i++;
        }
        if (userList != null && userList.size() == 1) {
            if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                pstmtSelect.setString(i, user.getValue());
                i++;
                pstmtSelect.setString(i, user.getValue());
                i++;
            } else if(user.getType().equals(PretupsI.STAFF_USER_TYPE)) {
                pstmtSelect.setString(i, senderCat);
                i++;
                final String selectQueryStafParentTemp = selectQueryStafParent.toString();
                PreparedStatement pstmtSelectOwner = con.prepareStatement(selectQueryStafParentTemp);
                pstmtSelectOwner.setString(1, user.getValue());
                rs = pstmtSelectOwner.executeQuery();

                /////////// Cast Fix... /////
                   /*
                   if(rs.next()){
                	 ownerID=rs.getString("parent_id");
                   }
                   try {
                       if (rs != null) {
                           rs.close();
                       }
                   } catch (Exception e) {
                       LOG.errorTrace(methodName, e);
                   }
                   try {
                       if (pstmtSelectOwner != null) {
                    	   pstmtSelectOwner.close();
                       }
                   } catch (Exception e) {
                       LOG.errorTrace(methodName, e);
                   }
                   
                   */

                ///////////////// Modified Part start here///////
                try {
                    if(rs.next()){
                        ownerID=rs.getString("parent_id");
                    }

                }catch (Exception e) {
                    // TODO: handle exception
                    LOG.errorTrace(methodName, e);

                }finally {


                    try {

                        if (rs != null) {

                            rs.close();

                        }

                        if (pstmtSelectOwner != null) {

                            pstmtSelectOwner.close();

                        }

                    } catch (Exception e) {

                        LOG.errorTrace(methodName, e);

                    }

                }

                //////Modification ends here

                pstmtSelect.setString(i, ownerID);
                i++;
            }
            else {
                pstmtSelect.setString(i, senderCat);
                i++;
                pstmtSelect.setString(i, user.getValue());
                i++;
            }
        } else if (userList != null && userList.size() > 1) {
            int userListSizes=userList.size();
            for (int k = 0; k <userListSizes ; k++) {
                user = (ListValueVO) userList.get(k);
                if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                    pstmtSelect.setString(i, user.getValue());
                    i++;
                    break;
                }
            }
        }

        pstmtSelect.setString(i, networkCode);
        i++;
        pstmtSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
        i++;
        pstmtSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
        i++;
        pstmtSelect.setString(i, serviceType);
        i++;
        pstmtSelect.setString(i, serviceType);

        return pstmtSelect;
    }

    @Override
    public String loadC2SReconciliationList() {
        String methodName = "loadC2SReconciliationList";
        //local_index_missing
        final StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name, CTRF.transfer_id, ");
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
        selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id,CTRF.subs_sid ,CTRF.transfer_profile_id,");
        selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.serial_number,U.owner_id ");
        selectQueryBuff.append(", UP.phone_language, UP.msisdn, UP.country phcountry,CTRF.ext_credit_intfce_type,CTRF.SUB_SERVICE,CTRF.reversal_id,CTRF.PENALTY,CTRF.OWNER_PENALTY, U.USER_ID, CTRF.PENALTY_DETAILS ");
        selectQueryBuff.append("FROM c2s_transfers CTRF, products PROD,service_type ST,users U,key_values KV,key_values KV1,user_phones UP ");
        selectQueryBuff.append("WHERE U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id AND KV.key(+)=CTRF.error_code AND KV.type(+)=? ");
        selectQueryBuff.append("AND KV1.key(+)=CTRF.transfer_status AND KV1.type(+)=? ");
        selectQueryBuff.append("AND CTRF.transfer_date >=? AND CTRF.transfer_date < ? ");
        selectQueryBuff.append("AND CTRF.service_type=? AND CTRF.product_code=PROD.product_code ");
        selectQueryBuff.append("AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND ST.service_type=CTRF.service_type ");

        selectQueryBuff.append("AND (CTRF.transfer_status=? OR CTRF.transfer_status=? ) ");
        selectQueryBuff.append("AND CTRF.network_code=? ");
        selectQueryBuff.append("ORDER BY CTRF.transfer_date_time DESC ,CTRF.transfer_id ");
        final String selectQuery = selectQueryBuff.toString();
        LogFactory.printLog(methodName, selectQuery.toString(), LOG);
        return selectQuery.toString();
    }


    @Override
    public PreparedStatement getReversalTransactionsQry(String msisdn,Connection con, String senderMsisdn,String txID,Date date,String time) throws SQLException{
        String methodName="getReversalTransactionsQry";
        final StringBuilder selectQueryBuff = new StringBuilder();
        //local_index_implemented
        final Calendar cal = BTSLDateUtil.getInstance();
        java.util.Date dt = cal.getTime(); // Current Date
        try{
            dt = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(dt, -1), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
        }catch(Exception e){
            LOG.error(methodName, e);
            LOG.errorTrace(methodName, e);
        }
        PreparedStatement pstmtSelect;
        selectQueryBuff.append("Select transfer_id,sender_msisdn,sender_category, receiver_msisdn, subscriber_type,service_class_code,transfer_value,subs_sid,TRANSFER_DATE_TIME,SERVICE_TYPE ");
        selectQueryBuff.append("FROM c2s_transfers ");
        if (BTSLUtil.isNullString(msisdn)) {
            selectQueryBuff
                    .append("WHERE transfer_date >= ? and sender_msisdn = ? and  transfer_id like ? and (transfer_date_time between (?-(?/24)) and ?) and service_type =? and reversal_id is null and transfer_status=? ");
            final String selectQuery = selectQueryBuff.toString();
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dt));
            pstmtSelect.setString(2, senderMsisdn);
            pstmtSelect.setString(3, "%" + txID);
            pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setInt(5, Integer.parseInt(time));
            pstmtSelect.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setString(7, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
            pstmtSelect.setString(8, PretupsI.TXN_STATUS_SUCCESS);
        } else {
            selectQueryBuff
                    .append("WHERE transfer_date >= ? and sender_msisdn = ? and ");
            selectQueryBuff.append(" (receiver_msisdn= ? or ");
            selectQueryBuff.append("transfer_id like ?) ");
            selectQueryBuff.append(" and (transfer_date_time between (?-(?/24)) and ?) and service_type=? and reversal_id is null and transfer_status=?");
            final String selectQuery = selectQueryBuff.toString();
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dt));
            pstmtSelect.setString(2, senderMsisdn);
            pstmtSelect.setString(3, msisdn);
            pstmtSelect.setString(4, txID);
            pstmtSelect.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setInt(6, Integer.parseInt(time));
            pstmtSelect.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setString(8, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
            pstmtSelect.setString(9, PretupsI.TXN_STATUS_SUCCESS);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "query:" + selectQueryBuff.toString());
        }
        return pstmtSelect;
    }

    @Override
    public String loadC2SReconciliationQry() {
        String methodName = "loadC2SReconciliationQry";
        final StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name, CTRF.transfer_id, ");
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
        selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.serial_number,U.owner_id ");
        selectQueryBuff.append(", UP.phone_language, UP.msisdn, UP.country phcountry,CTRF.ext_credit_intfce_type,CTRF.SUB_SERVICE,CTRF.reversal_id,CTRF.PENALTY,CTRF.OWNER_PENALTY, U.USER_ID, CTRF.PENALTY_DETAILS, CTRF.subs_sid ");
        selectQueryBuff.append("FROM c2s_transfers CTRF left outer join key_values KV on ( KV.key=CTRF.error_code AND KV.type=?) left outer join key_values KV1 on (KV1.key=CTRF.transfer_status AND KV1.type=?) , products PROD,service_type ST,users U,user_phones UP ");
        selectQueryBuff.append("WHERE U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id  ");
        selectQueryBuff.append("AND CTRF.product_code=PROD.product_code ");
        selectQueryBuff.append("AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND ST.service_type=CTRF.service_type ");
        selectQueryBuff.append("AND CTRF.transfer_id=? ");
        selectQueryBuff.append("ORDER BY CTRF.transfer_date_time DESC ,CTRF.transfer_id ");
        final String selectQuery = selectQueryBuff.toString();
        LogFactory.printLog(methodName, selectQuery, LOG);
        return selectQuery;
    }


}
