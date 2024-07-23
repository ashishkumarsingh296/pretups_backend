package com.btsl.db.query.postgres;

import com.btsl.mcom.common.CommonUtilQry;

/**
 * CommonUtilPostgresQry
 * @author sadhan.k
 *
 */
public class CommonUtilPostgresQry implements CommonUtilQry{

	@Override
	public String isUserBalance() {
		
		StringBuilder selectQuery = new StringBuilder("SELECT coalesce(MWB.balance,'0') balance FROM mtx_wallet_balances MWB, mtx_wallet MX, mtx_payment_methods MPM ");
        selectQuery.append(" WHERE MWB.payment_method_id=MX.payment_method_id ");
        selectQuery.append(" AND MX.payment_method_id=MPM.payment_method_id ");
        selectQuery.append(" AND MPM.party_user_id=?");
        // added by mohit for common msisdn usage on 15 july,2008
        selectQuery.append("AND MPM.user_type=?");         
        
        return selectQuery.toString();
	}

	@Override
	public String getNextID() {

		String sqlQuery = "SELECT last_no,frequency,last_initialised_date FROM sys_ids ids WHERE id_year=? AND id_type=? AND grph_domain_code=? FOR UPDATE";
		
		return sqlQuery;
	}

}
