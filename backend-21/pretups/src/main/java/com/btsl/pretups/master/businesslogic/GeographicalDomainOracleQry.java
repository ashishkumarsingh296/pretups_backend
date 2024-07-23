package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GeographicalDomainOracleQry implements GeographicalDomainQry{
	@Override
	public String isGeoDomainExistInHierarchyQry(){
		StringBuilder sqlRecordExist = new StringBuilder();
		sqlRecordExist.append("SELECT 1 FROM geographical_domains WHERE grph_domain_code  IN ");
        sqlRecordExist.append("(SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        sqlRecordExist.append("CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code   ");
        sqlRecordExist.append("START WITH grph_domain_code IN ");
        sqlRecordExist.append("(SELECT grph_domain_code FROM user_geographies WHERE user_id=?)) ");
        sqlRecordExist.append("AND grph_domain_code=? ");
        return sqlRecordExist.toString();
	}
	@Override
	public String isUserExistsInGeoDomainExistHierarchyQry(){
		StringBuilder sqlRecordExist = new StringBuilder();
		sqlRecordExist.append("SELECT 1 FROM geographical_domains WHERE grph_domain_code  IN ");
        sqlRecordExist.append("(SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        sqlRecordExist.append("CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code   ");
        sqlRecordExist.append("START WITH grph_domain_code IN ");
        sqlRecordExist.append("(SELECT grph_domain_code FROM user_geographies WHERE user_id=?)) ");
        sqlRecordExist.append("AND grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES where user_id=?) ");
        return sqlRecordExist.toString();
	}
	@Override
	public PreparedStatement loadGeoDomainCodeHeirarchyQry(Connection con, boolean isTopToBottom, String geodomaintype, String geodomainCode) throws SQLException{
		StringBuilder selectQuery = new StringBuilder("select grph_domain_code, network_code, grph_domain_name,");
        selectQuery.append("parent_grph_domain_code, grph_domain_short_name, description,");
        selectQuery.append("status, grph_domain_type, created_on, created_by, modified_on, modified_by");
        selectQuery.append(" from geographical_domains where grph_domain_type=? ");
        if (!isTopToBottom) {
            selectQuery.append("connect by prior parent_grph_domain_code=grph_domain_code");
        } else {
            selectQuery.append("connect by prior grph_domain_code=parent_grph_domain_code");
        }
        selectQuery.append(" start with grph_domain_code=? ");
        if (_log.isDebugEnabled()){
		    _log.debug("loadGeoDomainCodeHeirarchyQry", "QUERY sqlSelect=" + selectQuery.toString());
		}
        
       PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString()); 
       pstmtSelect.setString(1, geodomaintype);
       pstmtSelect.setString(2, geodomainCode);
       
        return pstmtSelect;
	}
	@Override
	public PreparedStatement loadGeographyHierarchyUnderParentQry(Connection p_con,String p_GeoDomain_Code,String p_networkCode,String p_GeoDomain_Type)throws SQLException{
		PreparedStatement pstmt = null;
		StringBuilder strBuff = new StringBuilder();	
		strBuff.append(" SELECT gd.grph_domain_code FROM geographical_domains gd ");
		strBuff.append(" WHERE gd.network_code = ? ");
		strBuff.append(" AND gd.status = 'Y' ");
		strBuff.append(" AND gd.GRPH_DOMAIN_TYPE = ? ");
		strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		strBuff.append(" START WITH grph_domain_code = ? ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		{
		    _log.debug("loadGeographyHierarchyUnderParentQry", "QUERY sqlSelect=" + sqlSelect);
		}
		pstmt = p_con.prepareStatement(sqlSelect);
		pstmt.setString(1, p_networkCode);
		pstmt.setString(2, p_GeoDomain_Type);
		pstmt.setString(3, p_GeoDomain_Code);
		return pstmt;
	}
}
