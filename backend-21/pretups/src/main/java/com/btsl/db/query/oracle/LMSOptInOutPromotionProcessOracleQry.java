package com.btsl.db.query.oracle;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.processes.LMSOptInOutPromotionProcessQry;

public class LMSOptInOutPromotionProcessOracleQry implements LMSOptInOutPromotionProcessQry{

	private Log log = LogFactory.getLog(getClass());
	@Override
	public String loadAssociatedUsersAndProfilesQry() {
		final StringBuilder selectQueryBuffer = new StringBuilder();
		selectQueryBuffer.append(" SELECT distinct ps.set_id, cu.user_id, ps.set_name, up.msisdn, up.country, up.phone_language ph_language,ps.MESSAGE_MANAGEMENT_ENABLED,");
		selectQueryBuffer.append(" psv.applicable_from, psv.applicable_to,pd.end_range , psv.message1, psv.message2,pd.detail_type,ps.REF_BASED_ALLOWED,pd.detail_id,pd.period_id,psv.VERSION,pd.product_code,pd.service_code ");
		selectQueryBuffer.append(" FROM profile_set_version psv, channel_users cu, users u, user_phones up, profile_set ps,PROFILE_DETAILS pd ");
		selectQueryBuffer.append(" WHERE u.user_id=cu.user_id and up.user_id=cu.user_id ");
		selectQueryBuffer.append(" AND cu.lms_profile=psv.set_id ");
		selectQueryBuffer.append(" AND psv.version=pd.version ");
		selectQueryBuffer.append(" AND ps.OPT_IN_OUT_ENABLED=? ");
		selectQueryBuffer.append(" AND cu.OPT_IN_OUT_STATUS=? ");
		selectQueryBuffer.append(" AND ps.PROMOTION_TYPE IN (? , ?)");
		selectQueryBuffer.append(" AND (trunc(cu.lms_profile_updated_on) <=trunc(applicable_from) and  TRUNC(applicable_from)>=TRUNC(SYSDATE) and TRUNC(applicable_to)>=TRUNC(SYSDATE)) ");
		selectQueryBuffer.append(" AND cu.lms_profile=ps.set_id AND pd.set_id=ps.set_id AND  psv.set_id=ps.set_id ");
		selectQueryBuffer.append(" AND ps.status=? AND u.status=? ");
		selectQueryBuffer.append(" AND cu.CONTROL_GROUP=? ");
		selectQueryBuffer.append(" AND (applicable_from >=(SELECT MIN(applicable_from)FROM PROFILE_SET_VERSION WHERE  TRUNC(applicable_from)>=TRUNC(sysdate) AND TRUNC(applicable_to)>=TRUNC(SYSDATE) AND status=? AND set_id=pd.set_id) ");
		selectQueryBuffer.append(" AND psv.status=? ) ");
		selectQueryBuffer.append(" order by psv.VERSION,pd.detail_id, cu.user_id,pd.product_code,pd.service_code ");
		LogFactory.printLog("loadAssociatedUsersAndProfilesQry", selectQueryBuffer.toString(), log);
		return selectQueryBuffer.toString();
	}

}