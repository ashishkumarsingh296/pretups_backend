package com.btsl.pretups.channel.transfer.businesslogic;

public enum LTHDownloadColumns {

	USER_NAME("userName"), MOBILE_NUM("mobileNumber"), USER_STATUS("userStatus"), DATE_TIME("dateTime"),
	TRANSACTION_ID("transactionID"), TRANSFER_TYPE("transferType"), CATEGORY_NAME("categoryName"),
	THRESH_HOLD("threshHold"), PRODUCT_NAME("productName"), PREVIOUS_BALANCE("previousBalance"),
	CURRENT_BALANCE("currentBalance"),THRESH_HOLD_VALUE("thresholdValue");

	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private LTHDownloadColumns(String columnName) {
		this.columnName = columnName;
	}

}
