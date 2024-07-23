package com.btsl.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface ChannelUserTransferQry {
	Log log = LogFactory.getLog(ChannelUserTransferQry.class.getName());
	public String createStatementForLloadInitiatedUserListForUserTransferQry(String status,String statusUsed);
	public PreparedStatement loadGeogphicalHierarchyListByToParentIdQry(Connection con,String status) throws SQLException;
	
}