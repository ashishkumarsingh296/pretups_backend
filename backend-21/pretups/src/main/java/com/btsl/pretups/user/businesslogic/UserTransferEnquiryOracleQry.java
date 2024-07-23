package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class UserTransferEnquiryOracleQry implements UserTransferEnquiryQry{
	
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadUserTransferBalancesCountSelectOpeningBalanceQry() {
		return  "SELECT BALANCE AS OPENING_BALANCE FROM (SELECT BALANCE FROM USER_DAILY_BALANCES WHERE USER_ID=? AND PRODUCT_CODE=? ORDER BY BALANCE_DATE DESC) A WHERE ROWNUM <= 1 ";
	}

	@Override
	public PreparedStatement loadUserTransferBalancesCountTotalHierarchyBalanceQry(
			Connection con, String userID, String productCode) throws SQLException {
		//Query for Total Hierarchy Balance
		StringBuilder strBuff = new StringBuilder("SELECT SUM(DISTINCT(ub.balance)) AS TOTAL_BALANCE, ub.product_code FROM users u, user_balances ub WHERE ub.user_id=u.user_id AND ub.network_code = u.network_code ");
    	strBuff.append("AND ub.product_code = ? CONNECT BY PRIOR u.user_id = u.parent_id START WITH u.user_id = ? GROUP BY ub.product_code");
    	LogFactory.printLog("loadUserTransferBalancesCountTotalHierarchyBalanceQry", strBuff.toString(), log);
    	PreparedStatement pstmtChildrenSelect = con.prepareStatement(strBuff.toString());
    	pstmtChildrenSelect.setString(1, productCode);
    	pstmtChildrenSelect.setString(2, userID);
		return pstmtChildrenSelect;
	}

	

}
