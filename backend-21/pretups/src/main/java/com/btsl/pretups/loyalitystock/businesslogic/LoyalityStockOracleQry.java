package com.btsl.pretups.loyalitystock.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class LoyalityStockOracleQry implements LoyalityStockQry{
	private String className = "LoyalityStockOracleQry";
	@Override
	public PreparedStatement loadProductsForStockQry(Connection pCon, String pNetworkCode, String pNetworkFor, String pModule)  throws SQLException{
		String methodName = className+"#loadProductsForStockQry";
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT P.product_code ,P.product_name, P.unit_value, NS.loyalty_stock  ");
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
			strBuff.append(",NS.FOC_STOCK, NS.INC_STOCK ");
		}
		strBuff.append("FROM products P,network_product_mapping npm,loyalty_stock NS ");
		strBuff.append("WHERE P.status = 'Y' AND P.product_code = NPM.product_code ");
		strBuff.append("AND P.module_code=decode(?,?,P.module_code,?)   ");
		strBuff.append("AND NPM.product_code = NS.product_code(+)AND NPM.network_code = ? AND NPM.status ='Y'  ");
		strBuff.append("AND NPM.network_code=NS.network_code(+) AND NS.network_code_for(+) = ? and NS.product_code= 'ETOPUP' ");
		strBuff.append("ORDER BY product_name ");
		String sql = strBuff.toString();
		LogFactory.printLog(methodName, QUERY + sql, LOG);
		PreparedStatement pstmtSelect = pCon.prepareStatement(sql);
		pstmtSelect.setString(1, pModule);
		pstmtSelect.setString(2, PretupsI.ALL);
		pstmtSelect.setString(3, pModule);
		pstmtSelect.setString(4, pNetworkCode);
		pstmtSelect.setString(5, pNetworkFor);
		return pstmtSelect;
	}
	@Override
	public String loadStockTransactionListQry(String pStatus) {
		String methodName = "loadStockTransactionListQry";
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("select ST.requested_points REQUESTED_QUANTITY, ");
		strBuff.append("ST.created_by REQUESTER,ST.txn_no  REFNO,ST.created_on TRANSACTIONDATE, ");
		strBuff.append("ST.txn_status STOCK_TRANSACTIONSTATUS,SUD.user_name REQUESTER_NAME,SUD.user_name REQUESTER_NAME, ");
		strBuff.append("LOC.network_name NETWORKNAME ");
		strBuff.append("FROM loyalty_stock_transaction ST,users SUD,lookups L ,networks LOC  ");
		strBuff.append("WHERE ST.txn_status IN (" + pStatus + ") ");
		strBuff.append("AND ST.requested_points > 0 ");
		strBuff.append("AND ST.created_by=SUD.user_id ");
		strBuff.append("AND (SUD.status='Y' OR SUD.status is null) ");
		strBuff.append("AND ST.network_code= DECODE(?,'ALL',ST.network_code,?) ");
		strBuff.append("AND ST.network_code=LOC.network_code AND ST.txn_status=L.lookup_code AND L.lookup_type=? AND ST.txn_status=? ");
		strBuff.append("ORDER BY SUD.user_name,ST.created_on DESC ");
		String query = strBuff.toString();
		LogFactory.printLog(methodName, QUERY + query, LOG);
		return query;
	}

}
