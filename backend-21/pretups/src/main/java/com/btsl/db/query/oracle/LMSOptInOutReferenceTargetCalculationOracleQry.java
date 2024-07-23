package com.btsl.db.query.oracle;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.processes.LMSOptInOutReferenceTargetCalculationQry;

public class LMSOptInOutReferenceTargetCalculationOracleQry implements LMSOptInOutReferenceTargetCalculationQry {
	
	private Log log  = LogFactory.getLog(getClass());

	@Override
	public String loadTargetProfileQry() {
		   final StringBuilder selectQueryBuffer = new StringBuilder();
	        selectQueryBuffer.append(" SELECT distinct pd.set_id, pd.period_id, pd.service_code, pd.points_type,pd.DETAIL_ID,ps.ref_based_allowed, cu.user_id, pd.type, u.msisdn, u.parent_id, u.network_code, u.category_code, ");
	        selectQueryBuffer.append(" up.msisdn parent_msisdn, up.sms_pin parent_sms_pin, pd.end_range, pd.points, psv.applicable_from, pd.product_code, ");
	        selectQueryBuffer.append(" p.product_short_code, psv.opt_contribution, psv.prt_contribution, cu.lms_profile_updated_on, ");
	        selectQueryBuffer.append(" psv.REFERENCE_FROM,psv.REFERENCE_TO,pd.detail_subtype ");
	        selectQueryBuffer.append(" , psv.VERSION ");
	        selectQueryBuffer.append(" FROM profile_details pd, profile_set ps, profile_set_version psv, channel_users cu, users u, user_phones up, products p ");
	        selectQueryBuffer.append(" WHERE pd.DETAIL_TYPE=? and ps.REF_BASED_ALLOWED=? AND ps.set_id=pd.set_id AND cu.lms_profile=pd.set_id AND ps.status=? AND ps.profile_type=? ");
	        selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED=? ");
			selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS IN (?,?,?) ");
			selectQueryBuffer.append(" AND cu.CONTROL_GROUP=? ");
			selectQueryBuffer.append(" AND ps.PROMOTION_TYPE IN (? , ?) ");
	        selectQueryBuffer.append(" AND u.user_id=cu.user_id AND psv.set_id=pd.set_id AND psv.applicable_from >=TRUNC(SYSDATE) AND u.status=? AND up.user_id(+)=u.parent_id AND p.product_code=pd.product_code AND "); 
	        selectQueryBuffer.append(" psv.VERSION IN (SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
	        selectQueryBuffer.append(" WHERE (applicable_from<=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE applicable_from>=TRUNC(SYSDATE)  AND TRUNC(applicable_to)>=TRUNC(SYSDATE) AND set_id=cu.lms_profile) ) ");
	        selectQueryBuffer.append(" AND psv.status='Y' and psv.SET_ID=cu.lms_profile ) and  psv.VERSION=pd.VERSION");
	        selectQueryBuffer.append(" order by applicable_from desc ");
	        LogFactory.printLog("loadTargetProfileQry", selectQueryBuffer.toString(), log);
		return selectQueryBuffer.toString();
	}

}
