package com.btsl.pretups.domain.businesslogic;

public class DomainPostgresQry implements DomainQry{

	@Override
	public String loadDomainCategoryMappingQry() {
		StringBuilder strBuff = new StringBuilder();
  		strBuff.append(" SELECT   d.domain_code, c.category_code, c.category_name, ");
  		strBuff.append(" COALESCE (gr.grade_code, ' ') AS grade_code, ");
  		strBuff.append(" COALESCE (gr.grade_name, ' ') AS grade_name, dt.display_allowed ");
  		strBuff.append(" FROM domains d, domain_types dt, categories c left outer join  channel_grades gr on c.category_code = gr.category_code ");
  		strBuff.append(" WHERE d.status <> 'N'");
  		strBuff.append(" AND d.domain_type_code = dt.domain_type_code ");
  		strBuff.append(" AND d.domain_code = c.domain_code ");
  		strBuff.append(" AND dt.domain_type_code <> ? ");
  		strBuff.append(" AND COALESCE (gr.status, 'Y') <> 'N' ");
  		strBuff.append(" ORDER BY domain_name");
		return strBuff.toString();
	}
}
