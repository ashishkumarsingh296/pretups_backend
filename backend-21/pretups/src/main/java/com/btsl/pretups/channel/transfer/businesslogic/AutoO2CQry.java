package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


/**
 * AutoO2CQry
 * @author sadhan.k
 *
 */
public interface AutoO2CQry {
	
	Log LOG = LogFactory.getLog(AutoO2CQry.class.getName());
	
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchy(Connection pCon, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode, String p_loginUserID, String user_type) throws SQLException;
	
	public PreparedStatement loadChannelUsersWithinGeoDomainHirearchy(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode, String p_loginUserID, String user_type, int approval_level) throws SQLException;

}
