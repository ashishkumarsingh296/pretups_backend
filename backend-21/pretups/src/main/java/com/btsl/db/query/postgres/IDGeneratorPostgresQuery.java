package com.btsl.db.query.postgres;

import com.btsl.common.IDGeneratorQry;

public class IDGeneratorPostgresQuery implements IDGeneratorQry{

	@Override
	public String selectFromIDsQry() {
		return "SELECT last_no,frequency,last_initialised_date,CURRENT_TIMESTAMP currentdate FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE ";
	}
}
