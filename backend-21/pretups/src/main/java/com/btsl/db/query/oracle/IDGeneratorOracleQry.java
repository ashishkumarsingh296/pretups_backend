package com.btsl.db.query.oracle;

import com.btsl.common.IDGeneratorQry;

public class IDGeneratorOracleQry implements IDGeneratorQry {

	@Override
	public String selectFromIDsQry() {
		
		return  "SELECT last_no,frequency,last_initialised_date,sysdate currentdate FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE OF ids.last_no";
		 
	}

}


