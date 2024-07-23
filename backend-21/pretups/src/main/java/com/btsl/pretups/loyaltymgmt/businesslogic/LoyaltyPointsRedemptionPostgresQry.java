package com.btsl.pretups.loyaltymgmt.businesslogic;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;

public class LoyaltyPointsRedemptionPostgresQry implements LoyaltyPointsRedemptionQry{
	
	@Override
	public String loaduserProfileRelatedDetailsQry(){
		 final StringBuilder selectQueryBuff = new StringBuilder();
		 selectQueryBuff.append(" SELECT psv.OPT_CONTRIBUTION ,psv.PRT_CONTRIBUTION,u.PARENT_ID,psv.set_id,psv.version, up.country, up.phone_language lang ");
	        selectQueryBuff.append(" FROM PROFILE_SET_VERSION psv,Channel_users ch,users u, user_phones up WHERE u.user_id=? and u.user_id=ch.user_ID and u.user_id=up.user_ID and ch.lms_profile=psv.SET_ID and  VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv   ");
			selectQueryBuff.append("WHERE (psv.applicable_from=(SELECT MAX(psv.applicable_from) FROM PROFILE_SET_VERSION psv, profile_details pd WHERE psv.applicable_from<=CURRENT_TIMESTAMP AND psv.set_id=ch.lms_profile and pd.set_id=psv.set_id AND pd.PRODUCT_CODE = ?) ) AND psv.status='Y' and psv.SET_ID=ch.lms_profile)");
	        return selectQueryBuff.toString();
	}

	@Override
	public String updateUserLoyaltyPointsDetailQry(){
		 final StringBuilder selectBuff = new StringBuilder();
		 selectBuff.append(" select ACCUMULATED_POINTS, points from BONUS ");
		 selectBuff.append("	WHERE USER_ID_OR_MSISDN=? and POINTS_DATE=(SELECT MAX(POINTS_DATE) FROM BONUS WHERE USER_ID_OR_MSISDN=? AND PRODUCT_CODE= ? ) ");
			selectBuff.append(" AND PRODUCT_CODE= ? ");
         if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
             selectBuff.append("  FOR UPDATE OF ACCUMULATED_POINTS WITH RS");
         } else {
             selectBuff.append("  FOR UPDATE  ");
         }
	        return selectBuff.toString();
	}
}
