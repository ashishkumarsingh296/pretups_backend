package com.web.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface ChannelUserTransferWebQry {

	Log LOG = LogFactory.getLog(ChannelUserTransferWebQry.class.getName());
	
	public PreparedStatement loadGeogphicalHierarchyListByToParentId(Connection p_con, String p_toParentID) throws SQLException;
}
