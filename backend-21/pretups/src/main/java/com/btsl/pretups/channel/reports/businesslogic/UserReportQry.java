package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface UserReportQry {
	
	Log LOG = LogFactory.getLog(UserReportQry.class.getName());
	String QUERY = "Query : ";
	
	/**
	 * @param con
	 * @param networkCode
	 * @param zone
	 * @param domainCode
	 * @param categoryCode
	 * @param userId
	 * @param loginUserId
	 * @param formDate
	 * @param toDate
	 * @param fromAmt
	 * @param toAmt
	 * @param userType
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public PreparedStatement loadUserClosingBalanceQry(Connection con, String networkCode, String zone, String domainCode, String categoryCode, String userId, String loginUserId, Date formDate, Date toDate, String fromAmt, String toAmt, String userType)
	throws  BTSLBaseException, SQLException;

}
