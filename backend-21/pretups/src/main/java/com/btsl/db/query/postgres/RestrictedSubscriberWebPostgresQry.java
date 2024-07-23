package com.btsl.db.query.postgres;

import java.util.Date;

import com.btsl.util.BTSLUtil;
import com.web.pretups.restrictedsubs.businesslogic.RestrictedSubscriberWebQry;


public class RestrictedSubscriberWebPostgresQry implements RestrictedSubscriberWebQry {
	
	@Override
	public String loadResSubsDetails(boolean isOwnerID,String msisdn,Date fromDate,Date toDate){

		 final StringBuffer strBuff = new StringBuffer("SELECT r.msisdn,r.subscriber_id,r.channel_user_id,r.channel_user_category,r.employee_code,r.owner_id,");
	        strBuff.append("r.employee_name,r.network_code,r.min_txn_amount,r.max_txn_amount,r.monthly_limit,r.association_date,");
	        strBuff.append("r.total_txn_count,r.total_txn_amount,r.black_list_status,r.remark,r.approved_by,lk.lookup_name status_desc,");
	        strBuff.append("r.approved_on,r.associated_by,r.status,r.created_on,r.created_by,r.modified_on,r.modified_by,");
	        strBuff.append("r.subscriber_type,r.language,r.country, lk1.lookup_name black_list_status_desc ");
	        strBuff.append(" FROM restricted_msisdns r,lookups lk, lookups lk1 ");
	        strBuff.append(" WHERE lk.lookup_code=r.status AND lk.lookup_type=? AND r.restricted_type = ? AND ");
	        strBuff.append(" lk1.lookup_code=r.black_list_status AND lk1.lookup_type=? AND ");
	        if (isOwnerID)// check for owner_id
	        {
	            if (BTSLUtil.isNullString(msisdn)) {
	                // show all data of chennel user
	                strBuff.append(" r.owner_id=? ");
	            } else {
	                strBuff.append(" r.msisdn=? AND r.owner_id=? ");
	            }
	        } else // check for channel_user_id
	        {
	            if (BTSLUtil.isNullString(msisdn)) {
	                // show all data of chennel user
	                strBuff.append(" r.channel_user_id=? ");
	            } else {
	                strBuff.append(" r.msisdn=? AND channel_user_id=? ");
	            }
	        }
	        if (fromDate != null && toDate != null)// date range check
	        {
	            strBuff.append(" AND date_trunc('day',r.created_on :: TIMESTAMP) >= ?");
	            strBuff.append(" AND date_trunc('day',r.created_on :: TIMESTAMP) <= ?");
	        }
	        strBuff.append(" ORDER BY r.employee_name ");
	        return strBuff.toString();
	
		
	}
}
