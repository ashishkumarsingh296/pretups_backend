package com.btsl.db.query.oracle;

import com.web.pretups.master.businesslogic.GeographicalDomainWebQry;

public class GeographicalDomainWebOracleQry implements  GeographicalDomainWebQry{
	@Override
	public String getGeogCodeDetailsListQry(){
		final StringBuilder selectQuery = new StringBuilder();
		 selectQuery.append("SELECT GRPH_DOMAIN_CODE,GRPH_DOMAIN_NAME,PARENT_GRPH_DOMAIN_CODE FROM GEOGRAPHICAL_DOMAINS ");
         selectQuery.append(" WHERE network_code=? AND status <> 'N' AND GRPH_DOMAIN_TYPE= (select GRPH_DOMAIN_TYPE  from (select * from GEOGRAPHICAL_DOMAIN_TYPES order by sequence_no desc) where rownum=1)");
         return selectQuery.toString();
	}
}