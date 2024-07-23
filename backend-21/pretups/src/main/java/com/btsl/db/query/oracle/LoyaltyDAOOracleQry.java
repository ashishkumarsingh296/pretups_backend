package com.btsl.db.query.oracle;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAOQry;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * This class provides oracle compatible query
 * @author lalit.chattar
 *
 */
public class LoyaltyDAOOracleQry implements LoyaltyDAOQry {

	/**
	 * This method return query for loading LMS profiles
	 * @param String service type
	 * @return String query
	 */
	@Override
	public String loadLMSProfileAndVersion(String lmsProfileServiceType) {
		 StringBuilder selectQueryBuild = new StringBuilder("SELECT pd.start_range,pd.end_range,pd.points_type,pd.period_id,pd.points,pd.detail_type,pd.detail_subtype, ");
         selectQueryBuild.append(" pd.subscriber_type, pd.type,pd.user_type,pd.service_code,pd.min_limit,pd.max_limit,pd.subscriber_type,pd.version,pd.points_type,pd.product_code ");
         selectQueryBuild.append(" FROM PROFILE_DETAILS pd ");
         selectQueryBuild.append(" WHERE pd.set_id=? ");
         if (lmsProfileServiceType.equals("C2C") || lmsProfileServiceType.equals("O2C")) {
             selectQueryBuild.append("  AND pd.service_code=? ");
         } else {
             selectQueryBuild.append("  AND pd.service_code in (?,'ALL')");
         }
         selectQueryBuild.append(" AND pd.detail_type=? AND pd.start_range<= ? AND pd.end_range>=? AND pd.VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
         selectQueryBuild.append(" WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE applicable_from<=SYSDATE and psv.applicable_to>=SYSDATE ");
         selectQueryBuild.append(" AND psv.set_id=set_id and status='Y' ) "); // gaurav
         selectQueryBuild.append("AND psv.set_id=?) ");
         selectQueryBuild.append("AND psv.status=?) ");
         return selectQueryBuild.toString();
	}

	/**
	 * This method return query for checking user already exist in system.
	 * @param String action type
	 * @return String query
	 */
	@Override
	public String checkUserAlreadyExist(String actionType) {
		 StringBuilder qryBuilder = new StringBuilder();
         qryBuilder.append(" SELECT profile_type,user_id_or_msisdn,points, ");
         qryBuilder.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
         qryBuilder.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
         qryBuilder.append(" modified_by,transfer_id FROM BONUS WHERE user_id_or_msisdn=? AND profile_type='LMS' ");
         // DB220120123for update WITH RS
         if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
             qryBuilder.append(" AND product_code=? AND points_date=? AND bucket_code=? FOR UPDATE OF points WITH RS");
         } else {
             if (BTSLUtil.isNullString(actionType)) {
                 qryBuilder.append(" AND product_code=? AND points_date=? AND bucket_code=? FOR UPDATE OF points ");
             } else if (!BTSLUtil.isNullString(actionType) && (PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType) || PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType))) {
                 qryBuilder.append(" AND product_code=? AND points_date=? FOR UPDATE OF points ");
             }
         }
         
         return qryBuilder.toString();
	}

	
	/**
	 * This method return query updating user bonus
	 * @return String query
	 */
	@Override
	public String updateBonusOfUser() {
		StringBuilder sqlBuilder = null;
        sqlBuilder = new StringBuilder("select ACCUMULATED_POINTS, points from BONUS ");
        sqlBuilder.append("	WHERE USER_ID_OR_MSISDN=? and POINTS_DATE=(SELECT MAX(POINTS_DATE) FROM BONUS WHERE USER_ID_OR_MSISDN=?  AND PRODUCT_CODE= ?) ");
        sqlBuilder.append(" AND PRODUCT_CODE= ? ");
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
        	sqlBuilder.append("  FOR UPDATE OF ACCUMULATED_POINTS WITH RS");
        } else {
        	sqlBuilder.append("  FOR UPDATE OF ACCUMULATED_POINTS ");
        }
        
        return sqlBuilder.toString();
	}

}
