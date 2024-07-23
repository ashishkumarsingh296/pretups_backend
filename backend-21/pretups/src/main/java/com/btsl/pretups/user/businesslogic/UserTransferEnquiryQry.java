package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UserTransferEnquiryQry {
	
	/**
	 * @return
	 */
	String loadUserTransferBalancesCountSelectOpeningBalanceQry();
	
	/**
	 * @param con
	 * @param userID
	 * @param productCode
	 * @return
	 * @throws SQLException
	 */
	PreparedStatement loadUserTransferBalancesCountTotalHierarchyBalanceQry(Connection con, String userID, String productCode) throws SQLException ;
}
