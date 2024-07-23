package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class UserTransferEnquiryPostgresQry implements UserTransferEnquiryQry{
	private Log log = LogFactory.getLog(this.getClass());
	@Override
	public String loadUserTransferBalancesCountSelectOpeningBalanceQry() {
		return  "SELECT BALANCE AS OPENING_BALANCE FROM (SELECT BALANCE FROM USER_DAILY_BALANCES WHERE USER_ID=? AND PRODUCT_CODE=? ORDER BY BALANCE_DATE DESC) A  LIMIT  1  ";
	}
	@Override
	public PreparedStatement loadUserTransferBalancesCountTotalHierarchyBalanceQry(
			Connection con, String userID, String productCode) throws SQLException {
		//Query for Total Hierarchy Balance
		StringBuilder strBuff = new StringBuilder();    	
    	strBuff.append(" WITH RECURSIVE q AS ( ");
    	strBuff.append(" SELECT u.user_id,ub.user_id ub_user_id,ub.balance,ub.product_code ,u.network_code,ub.network_code ub_network_code ");
    	strBuff.append(" FROM users u, user_balances ub WHERE  ");
    	strBuff.append(" u.user_id = ?  ");
    	strBuff.append(" UNION ALL ");
    	strBuff.append(" SELECT u1.user_id,ub1.user_id ub_user_id,ub1.balance,ub1.product_code ,u1.network_code,ub1.network_code ub_network_code ");
    	strBuff.append(" FROM user_balances ub1 , users u1 ");
    	strBuff.append(" JOIN q on q.user_id = u1.parent_id ) ");
    	strBuff.append(" SELECT SUM(DISTINCT(balance)) AS TOTAL_BALANCE, product_code  ");
    	strBuff.append(" FROM q  where ub_user_id=user_id  ");
    	strBuff.append(" AND ub_network_code = network_code  AND product_code = ? GROUP BY product_code ");
    	
    	LogFactory.printLog("loadUserTransferBalancesCountTotalHierarchyBalanceQry", strBuff.toString(), log);
    	PreparedStatement pstmtChildrenSelect = con.prepareStatement(strBuff.toString());
    	pstmtChildrenSelect.setString(1, userID);
    	pstmtChildrenSelect.setString(2, productCode);
		return pstmtChildrenSelect;
	}

}