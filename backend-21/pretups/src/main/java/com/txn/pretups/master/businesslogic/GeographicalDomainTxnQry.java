package com.txn.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface GeographicalDomainTxnQry {
	public Log log = LogFactory.getLog(GeographicalDomainTxnDAO.class.getName());
	public PreparedStatement loadGeoDomainCodeHeirarchyForOptQry(Connection pcon,boolean pisTopToBottom,String pgeodomainCodes,String pgeodomaintype)throws SQLException;
	
	public PreparedStatement loadGeographiesForAPIQry(Connection pcon, String pgeoCode, String pcategoryCode)throws SQLException;
	public PreparedStatement loadGeographiesForAPIByParentQry(Connection pcon, String rsSelectParent, String pcategoryCode)throws SQLException;
}
