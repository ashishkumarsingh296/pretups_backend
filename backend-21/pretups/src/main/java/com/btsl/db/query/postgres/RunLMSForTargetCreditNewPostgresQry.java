package com.btsl.db.query.postgres;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.RunLMSForTargetCreditNewQry;

public class RunLMSForTargetCreditNewPostgresQry implements RunLMSForTargetCreditNewQry{

	@Override
	public String loadRefTargetProfileQry() {
		StringBuffer selectQueryBuffer= new StringBuffer();
		selectQueryBuffer.append(" SELECT distinct cu.user_id, pd.set_id,pd.detail_id, pd.service_code,pd.period_id, pd.points_type, pd.version, pd.points, ps.ref_based_allowed,  pd.type, u.msisdn, ");
		selectQueryBuffer.append(" u.parent_id, u.network_code, u.category_code, up.msisdn parent_msisdn, up.sms_pin parent_sms_pin, uop.target, psv.applicable_from, pd.product_code, ");
		selectQueryBuffer.append(" p.product_short_code, psv.opt_contribution, psv.prt_contribution,psv.applicable_to ");
		selectQueryBuffer.append(" ,ps.OPT_IN_OUT_ENABLED ,OPT_IN_OUT_STATUS  ");
		selectQueryBuffer.append(" FROM profile_details pd, profile_set ps, profile_set_version psv, channel_users cu,  user_phones up right join users u on  up.user_id=u.parent_id , products p, user_oth_profiles uop ");
		selectQueryBuffer.append(" WHERE pd.detail_type=? AND ps.set_id=pd.set_id AND cu.lms_profile=pd.set_id AND ps.status=? AND ps.profile_type=? ");
		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED IN(?,?) ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS IN (?,?) ");
		} else {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED=? ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS=? ");
		}
		selectQueryBuffer.append(" AND cu.CONTROL_GROUP=? ");
		selectQueryBuffer.append(" AND ps.PROMOTION_TYPE IN (? , ?) ");
		selectQueryBuffer.append(" AND u.user_id=cu.user_id AND psv.set_id=pd.set_id AND date_trunc('day',psv.applicable_from::timestamp) <= ? AND u.status in ('Y','S')  AND p.product_code=pd.product_code AND psv.VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
		selectQueryBuffer.append(" WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE DATE_TRUNC('day',applicable_from::timestamp)<=?  and status='Y' ");
		selectQueryBuffer.append(" AND psv.set_id=set_id) ");
		selectQueryBuffer.append(" AND psv.set_id=pd.set_id) ");
		selectQueryBuffer.append(" AND psv.status='Y')  and psv.version=pd.version ");//Closing bracket for version here ");
		selectQueryBuffer.append(" AND uop.user_id=cu.user_id AND uop.set_id=cu.lms_profile AND uop.detail_id=pd.detail_id ");
		selectQueryBuffer.append(" AND uop.version=psv.version ");
		selectQueryBuffer.append(" and DATE_TRUNC('day',psv.applicable_to::timestamp ) >=? "); // Plus 1 added to provide the bonus if weekly/monthly profile created
		selectQueryBuffer.append(" order by user_id desc,pd.set_id desc,pd.service_code desc,uop.target desc,pd.detail_id desc  ");
		return selectQueryBuffer.toString();
	}

	@Override
	public String loadNonRefTargetProfilesQry() {
		StringBuilder selectQueryBuffer= new StringBuilder();
		selectQueryBuffer.append(" SELECT distinct cu.user_id, pd.set_id,pd.detail_id, pd.service_code,pd.period_id,pd.end_range, pd.points_type, pd.version, ps.ref_based_allowed,  pd.type, u.msisdn, u.parent_id, ");
		selectQueryBuffer.append(" u.network_code, u.category_code, up.msisdn parent_msisdn, up.sms_pin parent_sms_pin,  pd.points, psv.applicable_from, pd.product_code, ");
		selectQueryBuffer.append(" p.product_short_code, psv.opt_contribution, psv.prt_contribution,psv.applicable_to ");
		selectQueryBuffer.append(" ,ps.OPT_IN_OUT_ENABLED ,OPT_IN_OUT_STATUS  ");
		selectQueryBuffer.append(" FROM profile_details pd, profile_set ps, profile_set_version psv, channel_users cu,  user_phones up right join users u on up.user_id=u.parent_id, products p ");
		selectQueryBuffer.append(" WHERE pd.detail_type=? AND ps.set_id=pd.set_id AND cu.lms_profile=pd.set_id AND ps.status=? AND ps.profile_type=? AND ps.ref_based_allowed='N' ");
		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED IN (?,?) ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS IN (?,?) ");
		} else {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED=? ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS =? ");
		}
		selectQueryBuffer.append(" AND cu.CONTROL_GROUP=? ");
		selectQueryBuffer.append(" AND ps.PROMOTION_TYPE IN (? , ?) ");
		selectQueryBuffer.append(" AND u.user_id=cu.user_id AND psv.set_id=pd.set_id AND date_trunc('day',psv.applicable_from::timestamp) <= ? AND u.status in ('Y','S')  AND p.product_code=pd.product_code AND psv.VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
		selectQueryBuffer.append(" WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE DATE_TRUNC('day',applicable_from::timestamp)<= ?  and status='Y' ");
		selectQueryBuffer.append(" AND psv.set_id=set_id) ");
		selectQueryBuffer.append(" AND psv.set_id=pd.set_id) ");
		selectQueryBuffer.append(" AND psv.status='Y') and psv.version=pd.version  ");//Closing bracket for version here ");
		selectQueryBuffer.append(" and DATE_TRUNC('day',psv.applicable_to::timestamp ) >=? "); // Plus 1 added to provide the bonus if weekly/monthly profile created
		selectQueryBuffer.append(" order by user_id desc,pd.set_id desc,pd.service_code desc,pd.end_range desc,pd.detail_id desc ");

		return selectQueryBuffer.toString();
	}

	@Override
	public String checkUserDetailsExistInBonusTable() {
		StringBuilder qryBuffer= new StringBuilder();
		qryBuffer.append(" SELECT profile_type,user_id_or_msisdn,points, ");
		qryBuffer.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
		qryBuffer.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
		qryBuffer.append(" modified_by,transfer_id FROM BONUS WHERE user_id_or_msisdn=? AND profile_type='LMS' ");
		qryBuffer.append(" AND product_code=? AND points_date=? AND bucket_code=? FOR UPDATE  ");

		return qryBuffer.toString();
	}

}
