package com.btsl.pretups.networkstock.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class NetworkStockPostgresQry implements NetworkStockQry{
	private Log log = LogFactory.getLog(this.getClass());
	@Override
	public String debitNetworkStockSelectForQry() {
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT wallet_type,");
		strBuffSelect.append(" wallet_balance , wallet_sold ");
		strBuffSelect.append(" FROM network_stocks ");
		strBuffSelect.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? and wallet_type=? ");
		strBuffSelect.append(" FOR UPDATE  ");
		LogFactory.printLog("debitNetworkStockSelectForQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}

	@Override
	public String creditNetworkStockSelectForQry() {
		StringBuilder strBuffSelect = new StringBuilder(" SELECT wallet_balance , wallet_type, wallet_returned ");
		strBuffSelect.append(" FROM network_stocks ");
		strBuffSelect.append(" WHERE network_code = ? AND product_code = ? AND network_code_for = ? AND wallet_type = ? FOR UPDATE ");
		LogFactory.printLog(" creditNetworkStockSelectForQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}

	@Override
	public String loadCurrentStockListQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT NET.network_name, NETFOR.network_name network_code_for, PROD.product_name, PROD.unit_value, ");
		strBuff.append(" NSTOCK.wallet_type,NSTOCK.wallet_balance, NSTOCK.wallet_sold, NSTOCK.wallet_returned, NSTOCK.wallet_created ");
		strBuff.append(" FROM network_stocks NSTOCK,networks NET, products PROD, networks NETFOR ");
		strBuff.append(" WHERE NSTOCK.network_code=? AND NSTOCK.network_code = NET.network_code ");
		strBuff.append(" AND NSTOCK.network_code_for=CASE WHEN 'ALL' = ? then NSTOCK.network_code_for else ? end  ");
		strBuff.append(" AND NETFOR.NETWORK_TYPE=CASE WHEN NETFOR.network_code = ? then NETFOR.NETWORK_TYPE else ? end ");
		strBuff.append(" AND (NSTOCK.network_code_for = NETFOR.network_code)AND (NSTOCK.product_code = PROD.product_code) ");
		strBuff.append(" ORDER BY network_code_for ");
		return strBuff.toString();
	}

	@Override
	public String updateNetworkDailyStockQry() {
		StringBuilder selectStrBuff = new StringBuilder();
		selectStrBuff.append(" SELECT * FROM network_stocks ");
		selectStrBuff.append(" WHERE  network_code = ? AND network_code_for = ? AND wallet_type=? AND ");
		selectStrBuff.append(" date_trunc('day',daily_stock_updated_on::TIMESTAMP)  <> date_trunc('day',?::TIMESTAMP) FOR UPDATE ");
		LogFactory.printLog("updateNetworkDailyStockSelectForQry", selectStrBuff.toString(), log);
		return selectStrBuff.toString();
	}
}
