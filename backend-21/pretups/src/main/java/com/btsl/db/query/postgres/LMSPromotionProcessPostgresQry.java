package com.btsl.db.query.postgres;

import com.btsl.pretups.processes.LMSPromotionProcessQry;


public class LMSPromotionProcessPostgresQry implements LMSPromotionProcessQry{
	
	@Override
	public String loadAssociatedUsersAndProfilesQry() {
		StringBuilder selectQueryBuffer= new StringBuilder();
		selectQueryBuffer.append(" SELECT distinct ps.set_id, cu.user_id, ps.set_name, up.msisdn, up.country, up.phone_language ph_language,ps.MESSAGE_MANAGEMENT_ENABLED,");
		selectQueryBuffer.append(" psv.applicable_from, psv.applicable_to,pd.end_range , psv.message1, psv.message2,pd.detail_type,ps.REF_BASED_ALLOWED,pd.detail_id,pd.period_id ");//brajesh uot,ref_based,uop.target USER_OTH_PROFILES uop,
		selectQueryBuffer.append(" ,ps.OPT_IN_OUT_ENABLED ,OPT_IN_OUT_STATUS ,psv.VERSION ,pd.product_code,pd.service_code ");
		selectQueryBuffer.append(" FROM profile_set_version psv, channel_users cu, users u, user_phones up, profile_set ps,PROFILE_DETAILS pd"); 
		selectQueryBuffer.append(" WHERE u.user_id=cu.user_id and up.user_id=cu.user_id ");
		selectQueryBuffer.append(" and cu.lms_profile=psv.set_id ");
		selectQueryBuffer.append(" and psv.version=pd.version ");
		selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS IN ( ? , ? ) ");
		selectQueryBuffer.append(" AND ps.PROMOTION_TYPE IN (? , ? )");
		selectQueryBuffer.append(" and (date_trunc('day',cu.lms_profile_updated_on::timestamp) <=date_trunc('day',applicable_from::timestamp) ");
		selectQueryBuffer.append("and  DATE_TRUNC('day',applicable_from::TIMESTAMP)>=DATE_TRUNC('day',current_timestamp::timestamp) and DATE_TRUNC('day',applicable_to::timestamp)>=DATE_TRUNC('day',current_timestamp::timestamp))  ");
		selectQueryBuffer.append(" and cu.lms_profile=ps.set_id AND pd.set_id=ps.set_id and  psv.set_id=ps.set_id ");		
		selectQueryBuffer.append(" and ps.status=? and u.status=? ");
		selectQueryBuffer.append(" and cu.CONTROL_GROUP=? ");
		selectQueryBuffer.append(" AND (applicable_from>=(SELECT MIN(applicable_from)FROM PROFILE_SET_VERSION WHERE   DATE_TRUNC('day',applicable_from::TIMESTAMP)>=DATE_TRUNC('day',current_timestamp::timestamp) AND DATE_TRUNC('day',applicable_to::timestamp)>=DATE_TRUNC('day',current_timestamp::timestamp) AND status=? AND set_id=pd.set_id) ");
		selectQueryBuffer.append(" AND psv.status=? ) ");
		selectQueryBuffer.append(" order by psv.VERSION,pd.detail_id, cu.user_id, pd.product_code,pd.service_code ");
		return selectQueryBuffer.toString();
	}

}
