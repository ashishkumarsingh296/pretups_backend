package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GeographicalDomainPostgresQry implements GeographicalDomainQry {
	@Override
	public String isGeoDomainExistInHierarchyQry(){
		StringBuilder sqlRecordExist = new StringBuilder();
		sqlRecordExist.append("SELECT 1 FROM geographical_domains WHERE grph_domain_code  IN ");
        sqlRecordExist.append("(with recursive q as (SELECT grph_domain_code, status FROM geographical_domains gd1 where grph_domain_code IN ");
        sqlRecordExist.append("(SELECT grph_domain_code FROM user_geographies WHERE user_id=?) union all  ");
        sqlRecordExist.append("SELECT m.grph_domain_code, m.status FROM geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code) ");
        sqlRecordExist.append("SELECT grph_domain_code from q WHERE status='Y') ");
        sqlRecordExist.append("AND grph_domain_code=? ");
        return sqlRecordExist.toString();
	}
	@Override
	public String isUserExistsInGeoDomainExistHierarchyQry(){
		StringBuilder sqlRecordExist = new StringBuilder();
		sqlRecordExist.append("SELECT 1 FROM geographical_domains WHERE grph_domain_code  IN ");
        sqlRecordExist.append("(with recursive q as (SELECT grph_domain_code, status FROM geographical_domains gd1 WHERE  grph_domain_code IN ");
        sqlRecordExist.append("(SELECT grph_domain_code FROM user_geographies WHERE user_id=?) union all  ");
        sqlRecordExist.append("SELECT m.grph_domain_code, m.status FROM geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code)");
        sqlRecordExist.append("SELECT grph_domain_code from q WHERE status='Y') ");
        sqlRecordExist.append("AND grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES where user_id=?) ");
        return sqlRecordExist.toString();
	}
	@Override
	public PreparedStatement loadGeoDomainCodeHeirarchyQry(Connection con, boolean isTopToBottom, String geodomaintype, String geodomainCode) throws SQLException{
		StringBuilder selectQuery = new StringBuilder("with recursive q as (select grph_domain_code, network_code, grph_domain_name, ");
        selectQuery.append("parent_grph_domain_code, grph_domain_short_name, description, status, grph_domain_type, created_on, created_by, modified_on, modified_by ");
        selectQuery.append("from geographical_domains where  grph_domain_code=?  union all select m.grph_domain_code, m.network_code, m.grph_domain_name, ");
        selectQuery.append(" m.parent_grph_domain_code, m.grph_domain_short_name, m.description, m.status, m.grph_domain_type, m.created_on, m.created_by, m.modified_on, m.modified_by from geographical_domains m join q on ");
        if (!isTopToBottom) {
            selectQuery.append("q.parent_grph_domain_code=m.grph_domain_code) ");
        } else {
            selectQuery.append("q.grph_domain_code=m.parent_grph_domain_code) ");
        }
        selectQuery.append(" select q.grph_domain_code, q.network_code, q.grph_domain_name,q.parent_grph_domain_code, q.grph_domain_short_name, q.description,q.status, q.grph_domain_type, q.created_on, q.created_by, q.modified_on, q.modified_by from q ");
        selectQuery.append(" where grph_domain_type=? ");
        if (_log.isDebugEnabled()){
		    _log.debug("loadGeoDomainCodeHeirarchyQry", "QUERY sqlSelect=" + selectQuery.toString());
		}
        
        PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString()); 
        pstmtSelect.setString(1, geodomainCode);
        pstmtSelect.setString(2, geodomaintype);
        return pstmtSelect;
	}
	@Override
	public PreparedStatement loadGeographyHierarchyUnderParentQry(Connection p_con,String p_GeoDomain_Code,String p_networkCode,String p_GeoDomain_Type)throws SQLException{
		PreparedStatement pstmt = null;
		StringBuilder strBuff = new StringBuilder();	
		strBuff.append(" with recursive q as (SELECT gd.grph_domain_code,network_code,status,GRPH_DOMAIN_TYPE  FROM geographical_domains gd WHERE grph_domain_code = ? ");
		strBuff.append(" union all ");
		strBuff.append(" select m.grph_domain_code , m.network_code,m.status,m.GRPH_DOMAIN_TYPE from geographical_domains m  join q on ");
		strBuff.append(" q.grph_domain_code=m.parent_grph_domain_code  ) ");
		strBuff.append(" select q.grph_domain_code from q ");
		strBuff.append(" where network_code = ? AND status = 'Y' ");
		strBuff.append(" AND GRPH_DOMAIN_TYPE = ? ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled()){
		    _log.debug("loadGeographyHierarchyUnderParentQry", "QUERY sqlSelect=" + sqlSelect);
		}
		pstmt = p_con.prepareStatement(sqlSelect);
		pstmt.setString(1, p_GeoDomain_Code);
		pstmt.setString(2, p_networkCode);
		pstmt.setString(3, p_GeoDomain_Type);
	
		return pstmt;
	}
	
}
