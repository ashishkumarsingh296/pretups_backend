package com.txn.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GeographicalDomainTxnPostgresQry implements GeographicalDomainTxnQry {
	@Override
	public PreparedStatement loadGeoDomainCodeHeirarchyForOptQry(Connection pcon,boolean pisTopToBottom,String pgeodomainCodes,String pgeodomaintype)throws SQLException{
		StringBuilder selectQuery = new StringBuilder(" ");
        if (!pisTopToBottom) {
        	selectQuery.append("with recursive q as ( select grph_domain_code, network_code, grph_domain_name,");
            selectQuery.append(" parent_grph_domain_code, grph_domain_short_name, description,");
            selectQuery.append(" status, grph_domain_type, created_on, created_by, modified_on, modified_by,is_default ");
            selectQuery.append(" from geographical_domains where  grph_domain_code in('" + pgeodomainCodes + "') union all  ");
            selectQuery.append(" select m.grph_domain_code, m.network_code, m.grph_domain_name,m.parent_grph_domain_code, m.grph_domain_short_name, m.description, ");
            selectQuery.append(" m.status, m.grph_domain_type, m.created_on, m.created_by, m.modified_on, m.modified_by,m.is_default from geographical_domains m join q on ");
            selectQuery.append(" q.parent_grph_domain_code= m.grph_domain_code )");
            selectQuery.append(" select q.grph_domain_code, q.network_code, q.grph_domain_name, ");
            selectQuery.append(" q.parent_grph_domain_code, q.grph_domain_short_name, q.description,q.status, q.grph_domain_type, q.created_on, q.created_by, q.modified_on, q.modified_by,q.is_default  from q where q.grph_domain_type=?  ");
        } else {
        	selectQuery.append("with recursive q as ( select grph_domain_code, network_code, grph_domain_name,");
            selectQuery.append(" parent_grph_domain_code, grph_domain_short_name, description,");
            selectQuery.append(" status, grph_domain_type, created_on, created_by, modified_on, modified_by,is_default ");
            selectQuery.append(" from geographical_domains where  grph_domain_code in('" + pgeodomainCodes + "') union all ");
            selectQuery.append(" select m.grph_domain_code, m.network_code, m.grph_domain_name,m.parent_grph_domain_code, m.grph_domain_short_name, m.description, ");
            selectQuery.append(" m.status, m.grph_domain_type, m.created_on, m.created_by, m.modified_on, m.modified_by,m.is_default from geographical_domains m join q on ");
            selectQuery.append(" q.grph_domain_code= m.grph_domain_code )");
            selectQuery.append(" select q.grph_domain_code, q.network_code, q.grph_domain_name, ");
            selectQuery.append(" q.parent_grph_domain_code, q.grph_domain_short_name, q.description,q.status, q.grph_domain_type, q.created_on, q.created_by, q.modified_on, q.modified_by,q.is_default  from q where q.grph_domain_type=? ");

        }
        if (log.isDebugEnabled()) {
            log.debug("loadGeoDomainCodeHeirarchyForOptQry", "Query=" + selectQuery);
        }
        PreparedStatement pstmtSelect = pcon.prepareStatement(selectQuery.toString());
        pstmtSelect.setString(1, pgeodomaintype);   
        return pstmtSelect;

	}
	@Override
	public PreparedStatement loadGeographiesForAPIQry(Connection pcon, String p_geoCode, String pcategoryCode)throws SQLException{
		 PreparedStatement pstmtSelectGeography = null;
		 StringBuilder strBuff = new StringBuilder();
		strBuff.append("with recursive q as (SELECT distinct gd.grph_domain_code,gd.grph_domain_name,gd.parent_grph_domain_code,gd.status,gd.grph_domain_type ");
        strBuff.append(" FROM geographical_domains gd WHERE ");
        strBuff.append("  parent_grph_domain_code =? ");
        strBuff.append(" union all SELECT distinct m.grph_domain_code,m.grph_domain_name,m.parent_grph_domain_code,m.status,m.grph_domain_type ");
        strBuff.append(" FROM geographical_domains m join q on q.parent_grph_domain_code=m.grph_domain_code  ");
        strBuff.append(" )");
        strBuff.append(" select distinct q.grph_domain_code,q.grph_domain_name from q where q.status='Y' AND q.grph_domain_type in (SELECT GRPH_DOMAIN_TYPE FROM categories where category_code=?) ");

        String sqlSelectGeography = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadGeographiesForAPI," + "Query : ", sqlSelectGeography);
        }
        pstmtSelectGeography = pcon.prepareStatement(sqlSelectGeography);
        int i = 1;
        pstmtSelectGeography.setString(i, p_geoCode);
        pstmtSelectGeography.setString(++i, pcategoryCode);
        return pstmtSelectGeography;
	}
	@Override
	public PreparedStatement loadGeographiesForAPIByParentQry(Connection pcon, String rsSelectParent, String pcategoryCode)throws SQLException{
		PreparedStatement pstmtSelectGeography = null;
		 StringBuilder strBuff = new StringBuilder();
		   strBuff.append("SELECT gd.grph_domain_code,gd.grph_domain_name FROM geographical_domains gd ");
	        strBuff.append("WHERE gd.grph_domain_type=(SELECT grph_domain_type FROM categories WHERE category_code=?)");
	        strBuff.append("AND gd.status='Y' AND gd.grph_domain_code in ");
	        strBuff.append("( ");
	        strBuff.append("with recursive q as ( SELECT grph_domain_code from geographical_domains where parent_grph_domain_code=? union all  ");
	        strBuff.append("SELECT m.grph_domain_code from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ) ");
	        strBuff.append("select  q.grph_domain_code from q ");
	        strBuff.append(") ");
	        String sqlSelectGeography = strBuff.toString();

	        if (log.isDebugEnabled()) {
	            log.debug("loadGeographiesForAPIByParent," + "Query : ", sqlSelectGeography);
	        }
	        pstmtSelectGeography = pcon.prepareStatement(sqlSelectGeography);
	        int i = 1;
            pstmtSelectGeography.setString(i, pcategoryCode);
            pstmtSelectGeography.setString(++i, rsSelectParent);
            return pstmtSelectGeography;
	}
}
