package com.txn.pretups.loyaltymgmt.businesslogic;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;

/**
 * 
 * @author gaurav.pandey
 *
 */

public class LoyaltyTxnOracleQry implements LoyaltyTxnQry{
	
	@Override
	public String loadLMSProfileAndVersionQry(String plmsProfileServiceType)
	{
		StringBuilder selectQueryBuff = new StringBuilder("SELECT pd.start_range,pd.end_range,pd.points_type,pd.period_id,pd.points,pd.detail_type,pd.detail_subtype, ");
        selectQueryBuff.append(" pd.subscriber_type, pd.type,pd.user_type,pd.service_code,pd.min_limit,pd.max_limit,pd.subscriber_type,pd.version,pd.points_type,pd.product_code ");
        selectQueryBuff.append(" FROM PROFILE_DETAILS pd ");
        selectQueryBuff.append(" WHERE pd.set_id=? ");
        if ("C2C".equals(plmsProfileServiceType) || "O2C".equals(plmsProfileServiceType)) {
            selectQueryBuff.append("  AND pd.service_code=? ");
        } else {
            selectQueryBuff.append("  AND pd.service_code in (?,'ALL')");
        }
        selectQueryBuff.append(" AND pd.detail_type=? AND pd.start_range<= ? AND pd.end_range>=? AND pd.VERSION=(SELECT psv.VERSION FROM PROFILE_SET_VERSION psv ");
        selectQueryBuff.append(" WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE applicable_from<=SYSDATE and psv.applicable_to>=SYSDATE ");
        selectQueryBuff.append(" AND psv.set_id=set_id ) "); 
        selectQueryBuff.append("AND psv.set_id=?) ");
        selectQueryBuff.append("AND psv.status=?) ");
               return   selectQueryBuff.toString();                                   
		
	}
@Override
	public String checkUserAlreadyExistQry()
	{
	StringBuilder qryBuffer = new StringBuilder(" SELECT profile_type,user_id_or_msisdn,points, ");
    qryBuffer.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
    qryBuffer.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
    qryBuffer.append(" modified_by,transfer_id FROM BONUS WHERE user_id_or_msisdn=? AND profile_type='LMS' ");
    // DB220120123for update WITH RS
    if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
        qryBuffer.append(" AND product_code=? AND points_date=? AND bucket_code=? FOR UPDATE OF points WITH RS");
    } else {
        qryBuffer.append(" AND product_code=? AND points_date=? AND bucket_code=? FOR UPDATE OF points ");
    }
    return qryBuffer.toString();
    
	}


}
