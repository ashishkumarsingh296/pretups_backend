package com.btsl.pretups.channel.transfer.businesslogic;





public enum PassbookDownloadColumns {
	
	
	TRANSACT_DATE("transDate"),
	PRODUCT_NAME("productName"),
	OPENING_BALANCE("openingBalance"),
	STOCK_PURCHASE("stockPurchase"),
	CHANNEL_SALES("channelSales"),
	CUSTOMER_SALES("customerSales"),
	COMMISSION("commission"),
	C2C_WITHDRAWAL("c2cwithdrawal"),
	C2C_RETURNSALE("c2creturnSale"),
	O2C_RETURNAMT("o2cReturnAmount"),
	O2C_WITHDRAWALAMT("o2cWithdrawAmount"),	
	C2C_REVERSALAMT("c2cReverseAmount"),
	CLOSING_BALANCE("closingBalance"),
	C2S_REVERSALAMT("c2sReversalAmount");
	
	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private PassbookDownloadColumns(String columnName) {
		this.columnName = columnName;
	}
	
	
	
	
	

}
