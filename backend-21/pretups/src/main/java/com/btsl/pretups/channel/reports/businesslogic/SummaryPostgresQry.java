package com.btsl.pretups.channel.reports.businesslogic;

public class SummaryPostgresQry implements SummaryQry{
	@Override
	public String loadGeoDomainTypeListQry(){
		   final StringBuilder qryBuff = new StringBuilder(50);

	        qryBuff.append("with recursive q as (	SELECT GDT.grph_domain_parent, GDT.grph_domain_type, GDT.grph_domain_type_name  ");
	        qryBuff.append("FROM geographical_domain_types GDT where GDT.grph_domain_type IN (SELECT distinct GD.grph_domain_type  ");
	        qryBuff.append("FROM user_geographies UG, geographical_domains GD WHERE UG.grph_domain_code = GD.grph_domain_code AND GD.status IN('Y','S')  ");
	        qryBuff.append("AND GD.network_code=? AND UG.user_id=?) union all SELECT m.grph_domain_parent, m.grph_domain_type, m.grph_domain_type_name ");
	        qryBuff.append("from geographical_domain_types m join q on q.grph_domain_type=m.grph_domain_parent) ");
	        qryBuff.append("SELECT q.grph_domain_parent, q.grph_domain_type, q.grph_domain_type_name from q ");
	        return qryBuff.toString();
	}
	@Override
	public String loadParentGeoDomainListQry(){
		 final StringBuilder qryBuff = new StringBuilder(50);
         qryBuff.append("with recursive q as (SELECT GD1.grph_domain_type,gd1.grph_domain_code,gd1.grph_domain_name  ");
         qryBuff.append("FROM geographical_domains GD1 where GD1.grph_domain_code IN (SELECT GD.grph_domain_code ");
         qryBuff.append("FROM user_geographies UG, geographical_domains GD WHERE UG.grph_domain_code = GD.grph_domain_code AND GD.status IN('Y','S') ");
         qryBuff.append("AND GD.network_code=? AND UG.user_id=? ) union all  ");
         qryBuff.append("select m.grph_domain_type,m.grph_domain_code,m.grph_domain_name from geographical_domains m  join q on q.grph_domain_code=m.parent_grph_domain_code) ");
         qryBuff.append("select q.grph_domain_type,q.grph_domain_code,q.grph_domain_name from q");
         return qryBuff.toString();
	}
}
