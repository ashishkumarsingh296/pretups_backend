package com.btsl.db.query.oracle;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.LMSTargetVsAchievementReportQry;

public class LMSTargetVsAchievementReportOracleQry  implements LMSTargetVsAchievementReportQry {

	@Override
	public String loadRefTargetProfileQry() {
		StringBuilder selectQueryBuffer= new StringBuilder();
		selectQueryBuffer.append(" SELECT distinct cu.user_id, pd.set_id,pd.detail_id, pd.service_code,pd.period_id, pd.points_type,ps.set_name, pd.version, pd.points, ps.ref_based_allowed,  pd.type, u.msisdn, ");
		selectQueryBuffer.append(" u.parent_id, u.network_code, u.category_code,cat.CATEGORY_NAME,up.msisdn parent_msisdn, up.sms_pin parent_sms_pin, uop.target, psv.applicable_from, pd.product_code, ");
		selectQueryBuffer.append(" p.product_short_code, psv.opt_contribution, psv.prt_contribution,psv.applicable_to, ");
		selectQueryBuffer.append(" ps.OPT_IN_OUT_ENABLED ,OPT_IN_OUT_STATUS,  ");
		selectQueryBuffer.append(" dom.DOMAIN_NAME,gdomains.GRPH_DOMAIN_NAME,u.user_name ");
		selectQueryBuffer.append(" FROM profile_details pd, profile_set ps, profile_set_version psv, channel_users cu, users u, user_phones up, products p, user_oth_profiles uop,user_geographies geo,categories cat,domains dom,geographical_domains gdomains");
		selectQueryBuffer.append(" WHERE pd.detail_type=? AND ps.set_id=pd.set_id AND cu.lms_profile=pd.set_id AND ps.status=? AND ps.profile_type=? ");
		selectQueryBuffer.append(" AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code AND cat.domain_code= dom.domain_code AND geo.grph_domain_code=gdomains.grph_domain_code ");
		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED IN(?,?) ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS IN (?,?) ");
		} else {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED=? ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS=? ");
		}
		selectQueryBuffer.append(" AND cu.CONTROL_GROUP=? ");
		selectQueryBuffer.append(" AND ps.PROMOTION_TYPE=? ");
		selectQueryBuffer.append(" AND u.user_id=cu.user_id AND psv.set_id=pd.set_id AND trunc(psv.applicable_from) < ? AND u.status in ('Y','S') AND up.user_id(+)=u.parent_id AND p.product_code=pd.product_code AND psv.VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
		selectQueryBuffer.append(" WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE TRUNC(applicable_from)<?  and status='Y' ");
		selectQueryBuffer.append(" AND psv.set_id=set_id) ");
		selectQueryBuffer.append(" AND psv.set_id=pd.set_id) ");
		selectQueryBuffer.append(" AND psv.status='Y')  and psv.version=pd.version ");//Closing bracket for version here ");
		selectQueryBuffer.append(" AND uop.user_id=cu.user_id AND uop.set_id=cu.lms_profile AND uop.detail_id=pd.detail_id ");
		selectQueryBuffer.append(" AND uop.version=psv.version ");
		selectQueryBuffer.append(" and TRUNC(psv.applicable_to + 1) >=? "); // Plus 1 added to provide the bonus if weekly/monthly profile created
		selectQueryBuffer.append(" order by user_id desc,pd.set_id desc, uop.target desc,pd.service_code desc,pd.detail_id desc  ");
		return selectQueryBuffer.toString();
	}

	@Override
	public String loadNonRefTargetProfilesQry() {
		StringBuilder selectQueryBuffer= new StringBuilder();
		selectQueryBuffer.append(" SELECT distinct cu.user_id, pd.set_id,pd.detail_id, pd.service_code,pd.period_id,pd.end_range, pd.points_type, ps.set_name,pd.version, ps.ref_based_allowed,  pd.type, u.msisdn, u.parent_id, ");
		selectQueryBuffer.append(" u.network_code, u.category_code,cat.CATEGORY_NAME,up.msisdn parent_msisdn, up.sms_pin parent_sms_pin,  pd.points, psv.applicable_from, pd.product_code, ");
		selectQueryBuffer.append(" p.product_short_code, psv.opt_contribution, psv.prt_contribution,psv.applicable_to, ");
		selectQueryBuffer.append(" ps.OPT_IN_OUT_ENABLED ,OPT_IN_OUT_STATUS, ");
		selectQueryBuffer.append(" dom.DOMAIN_NAME,gdomains.GRPH_DOMAIN_NAME,u.user_name ");
		selectQueryBuffer.append(" FROM profile_details pd, profile_set ps, profile_set_version psv, channel_users cu, users u, user_phones up, products p,user_geographies geo,categories cat,domains dom,geographical_domains gdomains ");
		selectQueryBuffer.append(" WHERE pd.detail_type=? AND ps.set_id=pd.set_id AND cu.lms_profile=pd.set_id AND ps.status=? AND ps.profile_type=? AND ps.ref_based_allowed='N' ");
		selectQueryBuffer.append(" AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code AND cat.domain_code= dom.domain_code AND geo.grph_domain_code=gdomains.grph_domain_code ");
		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED IN (?,?) ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS IN (?,?) ");
		} else {
			selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED=? ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS =? ");
		}
		selectQueryBuffer.append(" AND cu.CONTROL_GROUP=? ");
		selectQueryBuffer.append(" AND ps.PROMOTION_TYPE=? ");
		selectQueryBuffer.append(" AND u.user_id=cu.user_id AND psv.set_id=pd.set_id AND trunc(psv.applicable_from) < ? AND u.status in ('Y','S') AND up.user_id(+)=u.parent_id AND p.product_code=pd.product_code ");
		selectQueryBuffer.append(" AND psv.VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
		selectQueryBuffer.append(" WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE TRUNC(applicable_from)< ?  and status='Y' ");
		selectQueryBuffer.append(" AND psv.set_id=set_id) ");
		selectQueryBuffer.append(" AND psv.set_id=pd.set_id) ");
		selectQueryBuffer.append(" AND psv.status='Y') and psv.version=pd.version  ");//Closing bracket for version here ");
		selectQueryBuffer.append(" and TRUNC(psv.applicable_to + 1 ) >=? "); // Plus 1 added to provide the bonus if weekly/monthly profile created
		selectQueryBuffer.append(" order by user_id desc,pd.set_id desc,pd.end_range desc, pd.service_code desc,pd.detail_id desc ");
	
		return selectQueryBuffer.toString();
	}

}
