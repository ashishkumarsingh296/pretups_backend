package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UssdUserQry {
	
	/**
	 * @return
	 */
	String loadUsersDetailsQry();

	/**
	 * @param con
	 * @param parentMsisdn
	 * @param childCatgCode
	 * @return
	 * @throws SQLException
	 */
	PreparedStatement loadDefaultGeographyQry(Connection con, String parentMsisdn, String childCatgCode) throws SQLException;

}
