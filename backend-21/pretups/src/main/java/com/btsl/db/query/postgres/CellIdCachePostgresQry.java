package com.btsl.db.query.postgres;

import com.btsl.user.businesslogic.CellIdCacheQry;


public class CellIdCachePostgresQry implements CellIdCacheQry{
	
	@Override
	public String loadCellIdQry() {
		 StringBuilder strBuff = new StringBuilder();
	        strBuff.append("select gd1.grph_domain_code,grph_cellid,GRPH_DOMAIN_type from GEOGRAPHICAL_DOMAIN_CELLS gdc,(");
	        strBuff.append(" WITH RECURSIVE q AS ( ");
	        strBuff.append("select gd.grph_domain_code, gd.grph_domain_code as  subarea , ");
	        strBuff.append("gd.grph_domain_type, gd.network_code, gd.grph_domain_name, gd.parent_grph_domain_code, status from geographical_domains gd  ");
	        strBuff.append("where grph_domain_type='SA' ");
	        strBuff.append("UNION ALL ");
	        strBuff.append("select gd1.grph_domain_code, q.subarea , ");
	        strBuff.append("gd1.grph_domain_type, gd1.network_code, gd1.grph_domain_name, gd1.parent_grph_domain_code, gd1.status  ");
	        strBuff.append(" from geographical_domains gd1  ");
	        strBuff.append("INNER JOIN q ON q.parent_grph_domain_code=gd1.grph_domain_code  ");
	        strBuff.append(") ");
	        strBuff.append("Select grph_domain_code, subarea, ");
	        strBuff.append("grph_domain_type, network_code, grph_domain_name, parent_grph_domain_code from q where status='Y' ");
	        strBuff.append(") gd1 where subarea=gdc.GRPH_DOMAIN_CODE");
		return strBuff.toString();
	}

}
