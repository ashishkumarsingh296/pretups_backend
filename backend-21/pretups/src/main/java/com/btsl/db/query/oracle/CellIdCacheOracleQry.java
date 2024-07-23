package com.btsl.db.query.oracle;

import com.btsl.user.businesslogic.CellIdCacheQry;

public class CellIdCacheOracleQry implements CellIdCacheQry {

	@Override
	public String loadCellIdQry() {
		 StringBuilder strBuff = new StringBuilder();
	        strBuff.append("select gd1.grph_domain_code,grph_cellid,GRPH_DOMAIN_type from GEOGRAPHICAL_DOMAIN_CELLS gdc,(select gd.grph_domain_code, (connect_by_root gd.grph_domain_code) subarea,");
	        strBuff.append("grph_domain_type, network_code, grph_domain_name, parent_grph_domain_code from geographical_domains gd where gd.status='Y' ");
	        strBuff.append("connect by prior parent_grph_domain_code=gd.grph_domain_code start with grph_domain_type='SA') gd1 where subarea=gdc.GRPH_DOMAIN_CODE");
		return strBuff.toString();
	}

}
