package com.btsl.db.query.postgres;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.LMSProgressiveMessagesQry;


public class LMSProgressiveMessagesPostgresQry implements LMSProgressiveMessagesQry{
	@Override
	public String getUsersforMessageQry() {
		final StringBuilder selectQueryBuff =new StringBuilder(" SELECT UP.msisdn,UP.country,UP.phone_language ph_language,CU.user_id, pd.set_id,pd.start_range,pd.end_range,pd.points_type,pd.period_id,pd.points,pd.DETAIL_ID,pd.detail_type,pd.detail_subtype, ");
		selectQueryBuff.append(" pd.subscriber_type, pd.type,pd.user_type,pd.service_code,pd.min_limit,pd.max_limit,pd.subscriber_type,ps.MESSAGE_MANAGEMENT_ENABLED,psv.version,Ps.REF_BASED_ALLOWED,psv.applicable_from,psv.applicable_to,ps.set_name,pd.product_code  ");
		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()){
			selectQueryBuff.append(" , ps.OPT_IN_OUT_ENABLED ,CU.OPT_IN_OUT_STATUS ");
		}
		selectQueryBuff.append(" From CHANNEL_USERS cu,user_phones UP,Profile_Details Pd , Profile_Set Ps ,PROFILE_SET_VERSION psv  ");
		selectQueryBuff.append(" Where CU.user_id=up.user_id and CU.LMS_PROFILE=Ps.SET_ID and Pd.Set_Id=Ps.Set_Id  and psv.Set_Id=Ps.Set_Id  And Pd.Detail_Type='VOLUME' and  Ps.Set_Id=psv.Set_Id And Pd.version=psv.VERSION  ");
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
			selectQueryBuff.append(" AND ps.OPT_IN_OUT_ENABLED IN(?,?) ");
			selectQueryBuff.append(" AND cu.OPT_IN_OUT_STATUS IN (?,?) ");
		} else {
			selectQueryBuff.append(" AND ps.OPT_IN_OUT_ENABLED=? ");
			selectQueryBuff.append(" AND cu.OPT_IN_OUT_STATUS=? ");
		}
		selectQueryBuff.append(" AND CU.CONTROL_GROUP=? ");
		selectQueryBuff.append(" AND ps.PROMOTION_TYPE IN (? , ?) ");
		// Added by Diwakar for Fixing the issue of OCI
		selectQueryBuff.append(" AND date_trunc('day', psv.applicable_from::timestamp) <= ? ");
		selectQueryBuff.append(" and Pd.VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
		selectQueryBuff.append(" WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE DATE_TRUNC('day',applicable_from::TIMESTAMP)<=? and status='Y' ");
		selectQueryBuff.append(" AND psv.set_id=set_id) ");
		selectQueryBuff.append(" AND psv.set_id=pd.set_id) ");
		selectQueryBuff.append(" AND psv.status='Y') ");// Closing
		// bracket for
		// version here
		// ");
		selectQueryBuff.append(" AND psv.status=? and date_trunc ('day', (psv.applicable_to + interval '1 day')  ::timestamp)>=? ");// Closing
		// bracket
		// for
		// version
		// here
		selectQueryBuff.append(" AND Pd.period_id not in ( ? ) ");
		// Adding 1 into applicable_to date to sent final message to
		// associated channel users
		return selectQueryBuff.toString();
	}
	
	@Override
	public String sendAlertsGetSumSenderTransferAmountQry() {
        final StringBuilder selectQueryBuff = new StringBuilder("select sum(SENDER_TRANSFER_AMOUNT) from DAILY_C2S_TRANS_DETAILS");
        selectQueryBuff.append(" where user_id =? and service_type=? and TRANS_DATE >= ? and TRANS_DATE <=current_timestamp-interval '1 day' ");
		return selectQueryBuff.toString();
	}

	@Override
	public String sendAlertsGetSumC2CTransferOutAmountQry() {
	    final StringBuilder selectQueryBuff2 = new StringBuilder("select sum(C2C_TRANSFER_OUT_AMOUNT),sum(C2C_TRANSFER_IN_AMOUNT) from DAILY_CHNL_TRANS_MAIN");
        selectQueryBuff2.append(" where user_id =?  and TRANS_DATE >= ? and TRANS_DATE <=current_timestamp-interval '1 day' ");
		return selectQueryBuff2.toString();
	}

	@Override
	public String sendAlertsGetSumO2CTransferInAmountQry() {

        final StringBuilder selectQueryBuff3 = new StringBuilder("select sum(O2C_TRANSFER_IN_AMOUNT) from DAILY_CHNL_TRANS_MAIN");
        selectQueryBuff3.append(" where user_id =?  and TRANS_DATE >= ? and TRANS_DATE <=current_timestamp-interval '1 day' ");
		return selectQueryBuff3.toString();
	}

	
	
}
