package com.btsl.pretups.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.iat.enquiry.businesslogic.IATTransferQry;

public interface TransferQry {
	
	Log LOG = LogFactory.getLog(IATTransferQry.class.getName());
	String QUERY = "Query : ";
	
	public PreparedStatement loadP2PReconciliationItemsList(Connection con, String transferID) throws SQLException;
	
	public String updateReconcilationStatusQry();

}
