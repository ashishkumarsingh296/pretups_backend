package com.btsl.db.query.oracle;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.NetworkDailyClosingStockQry;
import com.btsl.util.Constants;

public class NetworkDailyClosingStockOracleQry implements NetworkDailyClosingStockQry{

	@Override
	public String updateDailyClosingStock() {
		
		 final StringBuilder selectQueryBuf = new StringBuilder("SELECT network_code,network_code_for,product_code,stock_created,");
	        selectQueryBuf.append("stock_returned,stock,stock_sold,last_txn_no,last_txn_type,last_txn_stock,previous_stock,");
	        selectQueryBuf.append("foc_stock_created, foc_stock_returned, foc_stock, foc_stock_sold,");
	        selectQueryBuf.append("foc_last_txn_no, foc_last_txn_type, foc_last_txn_stock, foc_previous_stock,");
	        selectQueryBuf.append("inc_stock_created, inc_stock_returned, inc_stock, inc_stock_sold,");
	        selectQueryBuf.append("inc_last_txn_no, inc_last_txn_type, inc_last_txn_stock, inc_previous_stock,");
	        selectQueryBuf.append("modified_by,modified_on,created_on,created_by,daily_stock_updated_on FROM network_stocks ");
	        // DB220120123for update WITH RS
	        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
	            selectQueryBuf.append("WHERE trunc(daily_stock_updated_on)<>trunc(?) FOR UPDATE WITH RS");
	        } else {
	            selectQueryBuf.append("WHERE trunc(daily_stock_updated_on)<>trunc(?) FOR UPDATE");
	        }
	        
	   return selectQueryBuf.toString();
	}
}
