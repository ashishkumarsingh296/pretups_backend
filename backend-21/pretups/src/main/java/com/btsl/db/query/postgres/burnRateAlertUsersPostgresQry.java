package com.btsl.db.query.postgres;

import com.btsl.pretups.processes.BurnRateIndicatorProcessQry;

public class burnRateAlertUsersPostgresQry implements BurnRateIndicatorProcessQry {

	@Override
	public String burnRateAlertUsersQry() {
		final StringBuilder queryBuf = new StringBuilder("select v.user_id, u.msisdn , v.user_network_code , v.product_id,v.total_distributed,v.total_recharged,");
		queryBuf.append(" (total_recharged / cast(total_distributed as float))*100");
		queryBuf.append(" as burn_rate, p.mrp/? as mrp from users u, voms_products p,voms_daily_burned_vouchers v where v.user_id = u.user_id and p.product_id=v.product_id");
		queryBuf.append(" and (total_recharged / cast(total_distributed as float))*100 >= ? and summary_date<? and summary_date> ?::date - interval '2' day");


		return queryBuf.toString();
	}

}
