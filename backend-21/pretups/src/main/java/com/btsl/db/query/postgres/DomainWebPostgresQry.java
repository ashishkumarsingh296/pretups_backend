package com.btsl.db.query.postgres;

import com.web.pretups.domain.businesslogic.DomainWebQry;

public class DomainWebPostgresQry implements DomainWebQry {

	@Override
	public String updateDomainQry() {
		StringBuilder updateQueryBuff = new StringBuilder();
		updateQueryBuff
				.append("UPDATE domains SET");
		updateQueryBuff.append(" domain_name=?,num_of_categories=?::integer,status=?,");
        updateQueryBuff.append("modified_on=?,modified_by=? WHERE domain_code=?");
		return updateQueryBuff.toString();
		
	}
}
