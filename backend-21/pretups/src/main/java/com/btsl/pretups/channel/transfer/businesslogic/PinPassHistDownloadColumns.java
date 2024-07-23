package com.btsl.pretups.channel.transfer.businesslogic;

public enum PinPassHistDownloadColumns {

	USER_NAME("userName"), MSISDN_LOGINID("msisdnOrLoginID"),MODIFIED_BY("modifiedBy"),MOIDFIED_ON("modifiedOn");
	
	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private PinPassHistDownloadColumns(String columnName) {
		this.columnName = columnName;
	}

}
