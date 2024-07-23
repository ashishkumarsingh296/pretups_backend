package com.txn.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GeographicalDomainTxnOracleQry implements GeographicalDomainTxnQry {
	@Override
	public PreparedStatement loadGeoDomainCodeHeirarchyForOptQry(Connection pcon,boolean pisTopToBottom,String pgeodomainCodes,String pgeodomaintype)throws SQLException{
		
		StringBuilder selectQuery = new StringBuilder("select grph_domain_code, network_code, grph_domain_name,");
        selectQuery.append(" parent_grph_domain_code, grph_domain_short_name, description,");
        selectQuery.append(" status, grph_domain_type, created_on, created_by, modified_on, modified_by,is_default ");
        selectQuery.append(" from geographical_domains where grph_domain_type=? ");
        if (!pisTopToBottom) {
            selectQuery.append("connect by prior parent_grph_domain_code=grph_domain_code");
        } else {
            selectQuery.append("connect by prior grph_domain_code=parent_grph_domain_code");
        }
        selectQuery.append(" start with grph_domain_code in('" + pgeodomainCodes + "') ");
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
		strBuff.append("SELECT distinct gd.grph_domain_code,gd.grph_domain_name ");
        strBuff.append("FROM geographical_domains gd WHERE gd.status='Y' ");
        strBuff.append("AND gd.grph_domain_type in ");
        strBuff.append("(SELECT GRPH_DOMAIN_TYPE FROM categories where category_code=?) ");
        strBuff.append("CONNECT BY PRIOR parent_grph_domain_code=grph_domain_code ");
        strBuff.append("START WITH parent_grph_domain_code =? ");
        String sqlSelectGeography = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadGeographiesForAPI," + "Query : ", sqlSelectGeography);
        }
        pstmtSelectGeography = pcon.prepareStatement(sqlSelectGeography);
        int i = 1;
        pstmtSelectGeography.setString(i, pcategoryCode);
        pstmtSelectGeography.setString(++i, p_geoCode);
        return pstmtSelectGeography;
	}
	
	@Override
	public PreparedStatement loadGeographiesForAPIByParentQry(Connection pcon, String rsSelectParent, String pcategoryCode)throws SQLException{
		PreparedStatement pstmtSelectGeography = null;
		 StringBuilder strBuff = new StringBuilder();
		   strBuff.append("SELECT gd.grph_domain_code,gd.grph_domain_name FROM geographical_domains gd ");
	        strBuff.append("WHERE gd.grph_domain_type=(SELECT grph_domain_type FROM categories WHERE category_code=?)");
	        strBuff.append("AND gd.status='Y' AND gd.grph_domain_code in ");
	        strBuff.append("(SELECT grph_domain_code from geographical_domains ");
	        strBuff.append("CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code ");
	        strBuff.append("START WITH parent_grph_domain_code=? ) ");
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
