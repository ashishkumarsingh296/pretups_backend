package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface GeographicalDomainQry {
	public final Log _log = LogFactory.getLog(GeographicalDomainDAO.class.getName());
	public String isGeoDomainExistInHierarchyQry();
	public String isUserExistsInGeoDomainExistHierarchyQry();
	public PreparedStatement loadGeoDomainCodeHeirarchyQry(Connection con, boolean isTopToBottom, String geodomaintype, String geodomainCode) throws SQLException;
	public PreparedStatement loadGeographyHierarchyUnderParentQry(Connection p_con,String p_GeoDomain_Code,String p_networkCode,String p_GeoDomain_Type)throws SQLException;
	}
