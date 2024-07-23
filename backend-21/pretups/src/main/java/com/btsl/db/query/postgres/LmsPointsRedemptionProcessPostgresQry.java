package com.btsl.db.query.postgres;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.processes.LmsPointsRedemptionProcessQry;

public class LmsPointsRedemptionProcessPostgresQry implements LmsPointsRedemptionProcessQry{
	private Log log = LogFactory.getLog(getClass());

	@Override
	public String getBonusDataListQry() {
		final StringBuilder query = new StringBuilder();
		query.append(" select distinct  bn.USER_ID_OR_MSISDN,max (bn.points_date) bonus_date,bn.PRODUCT_CODE  from bonus bn,users u ");
		query.append("where u.user_id=bn.USER_ID_OR_MSISDN and u.status='Y' ");
		//Added related to performance related issue
		query.append(" AND date_trunc('day',bn.points_date::timestamp) >= date_trunc('day',?::timestamp)");
		query.append(" AND date_trunc('day',bn.points_date::timestamp) < date_trunc('day',?::timestamp)");
		query.append(" group by  bn.USER_ID_OR_MSISDN,bn.PRODUCT_CODE ");
		LogFactory.printLog("getBonusDataListQry", query.toString(), log);
		return query.toString();
	}

	@Override
	public String updateUserLoyaltyPointsDetailQry() {
		final StringBuilder selectBuff = new StringBuilder("select ACCUMULATED_POINTS, points from BONUS ");
		selectBuff.append("	WHERE USER_ID_OR_MSISDN=? and POINTS_DATE=(SELECT MAX(POINTS_DATE) FROM BONUS WHERE USER_ID_OR_MSISDN=?  AND PRODUCT_CODE= ? ) ");
		selectBuff.append(" AND PRODUCT_CODE= ? ");

		selectBuff.append("  FOR UPDATE  ");

		LogFactory.printLog("updateUserLoyaltyPointsDetailQry", selectBuff.toString(), log);
		return selectBuff.toString();
	}
	
	@Override
	public String loaduserProfileDetailsQry() {
		final StringBuilder selectQueryBuff = new StringBuilder(" SELECT psv.OPT_CONTRIBUTION ,psv.PRT_CONTRIBUTION,u.PARENT_ID, psv.set_id,psv.version ");
        selectQueryBuff.append("  FROM PROFILE_SET_VERSION psv,Channel_users ch,users u  WHERE u.user_id=? and u.user_id=ch.user_ID  ");
        selectQueryBuff.append("  and ch.lms_profile=psv.SET_ID  ");
        selectQueryBuff.append("   and  VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
        selectQueryBuff.append("   WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE DATE_TRUNC('day',applicable_from::TIMESTAMP)<=CURRENT_TIMESTAMP  and status='Y' AND set_id=ch.lms_profile) ) ");
        selectQueryBuff.append(" AND psv.status='Y' and psv.SET_ID=ch.lms_profile)");
        LogFactory.printLog("loaduserProfileDetailsQry", selectQueryBuff.toString(), log);
		return selectQueryBuff.toString();
	}

}