package com.btsl.pretups.channel.reports.businesslogic;

public class SummaryOracleQry implements SummaryQry{
	@Override
	public String loadGeoDomainTypeListQry(){
		   final StringBuilder qryBuff = new StringBuilder(50);

	        qryBuff.append("SELECT GDT.grph_domain_parent, GDT.grph_domain_type, GDT.grph_domain_type_name ");
	        qryBuff.append("FROM geographical_domain_types GDT CONNECT BY PRIOR GDT.grph_domain_type=GDT.grph_domain_parent ");
	        qryBuff.append("START WITH GDT.grph_domain_type IN (SELECT distinct GD.grph_domain_type ");
	        qryBuff.append("FROM user_geographies UG, geographical_domains GD ");
	        qryBuff.append("WHERE UG.grph_domain_code = GD.grph_domain_code AND GD.status IN('Y','S') ");
	        qryBuff.append("AND GD.network_code=? AND UG.user_id=?)");
	        return qryBuff.toString();
	}
	@Override
	public String loadParentGeoDomainListQry(){
		 final StringBuilder qryBuff = new StringBuilder(50);
         qryBuff.append("SELECT GD1.grph_domain_type,gd1.grph_domain_code,gd1.grph_domain_name ");
         qryBuff.append("FROM geographical_domains GD1 CONNECT BY PRIOR GD1.grph_domain_code=GD1.parent_grph_domain_code ");
         qryBuff.append("START WITH GD1.grph_domain_code IN (SELECT GD.grph_domain_code ");
         qryBuff.append("FROM user_geographies UG, geographical_domains GD ");
         qryBuff.append("WHERE UG.grph_domain_code = GD.grph_domain_code AND GD.status IN('Y','S') ");
         qryBuff.append("AND GD.network_code=? AND UG.user_id=?)");
         return qryBuff.toString();
	}
}
