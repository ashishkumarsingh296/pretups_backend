package com.btsl.pretups.whitelist.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface WhiteListQry {
	
	public PreparedStatement loadInterfaceDetailsQry(Connection pCon, String pMsisdn)throws SQLException;
	public String loadWhiteListSubsDetailsQry();
	public String insertIndWhiteListDetail();

}
