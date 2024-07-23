package com.btsl.db.query.postgres;

import com.btsl.pretups.processes.DailySummaryChannelTxnProcessQry;

public class DailySummaryChannelTxnProcessPostgresQry implements DailySummaryChannelTxnProcessQry{

	@Override
	public String selectFromUserDailyBalance(boolean categoryALL, String[] category ) {
		 final StringBuffer balanceQuery = new StringBuffer();
         balanceQuery.append("SELECT Y.balance_date,X.user_id,X.msisdn,X.category_name, X.product_code,X.opening_balance, COALESCE(Y.closing_bal, X.opening_balance) CLOSING_BAL  FROM");
         balanceQuery.append("(SELECT UDB.balance_date,U.user_id,U.msisdn,C.category_name,UDB.product_code,COALESCE(UDB.balance,0) OPENING_BALANCE");
         balanceQuery.append(" FROM USERS U left join USER_DAILY_BALANCES UDB on U.user_id=UDB.user_id,  CATEGORIES C");
         balanceQuery.append(" WHERE ");
         balanceQuery.append(" U.status =?");
         balanceQuery.append(" AND U.user_type=?");
         balanceQuery.append(" AND C.category_code=U.category_code");
         
         if (!categoryALL) {
             balanceQuery.append(" AND U.category_code IN(");
             for (int i = 0; i < category.length; i++) {
                 if (i != 0) {
                     balanceQuery.append(",");
                 }
                 balanceQuery.append("?");
             }
             balanceQuery.append(")");
         }
         balanceQuery.append(" AND UDB.balance_date=?");
         balanceQuery.append(")X left join  ");
         balanceQuery.append("(SELECT UDB1.balance_date,COALESCE(balance,0) CLOSING_BAL, UDB1.user_id,product_code");
         balanceQuery.append(" FROM USER_DAILY_BALANCES UDB1 WHERE balance_date=?)Y on X.user_id=Y.user_id AND X.product_code=Y.product_code ");

		return balanceQuery.toString();
	}
	
}
