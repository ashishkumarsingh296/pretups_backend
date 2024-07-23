package com.btsl.db.query.oracle;

import com.btsl.pretups.processes.BurnRateIndicatorProcessQry;

public class burnRateAlertUsersOracleQry implements BurnRateIndicatorProcessQry {

	@Override
	public String burnRateAlertUsersQry() {
		final StringBuilder queryBuf = new StringBuilder("select v.user_id, u.msisdn , v.user_network_code , v.product_id,v.total_distributed,v.total_recharged,");
		queryBuf.append(" ((total_recharged/total_distributed)*100)");
		queryBuf.append(" as burn_rate, p.mrp/? as mrp from users u, voms_products p,voms_daily_burned_vouchers v where v.user_id = u.user_id and p.product_id=v.product_id");
		queryBuf.append(" and ((total_recharged/total_distributed)*100) >= ? and summary_date<TO_DATE(?) and summary_date> TO_DATE(?) - 2");


		return queryBuf.toString();
	}

}
