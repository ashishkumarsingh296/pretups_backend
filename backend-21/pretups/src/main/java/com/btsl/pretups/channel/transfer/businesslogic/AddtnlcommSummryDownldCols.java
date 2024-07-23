package com.btsl.pretups.channel.transfer.businesslogic;

public enum AddtnlcommSummryDownldCols {

	TRANSDATE("TransferDateOrMonth"), LOGIN_ID("loginID"), USER_NAME("userName"), USER_MOB_NUMBER("userMobileNumber"),
	USER_CATEGORY("userCategory"), USER_GEOGRAPHY("userGeography"), PARENT_NAME("parentName"),
	PARENT_MOB_NUM("parentMobileNumber"), PARENT_CATEGORY("parentCategory"), PARENT_GEOGRAPHY("parentGeography"),
	OWNER_NAME("ownerName"), OWNER_MOB_NUM("ownerMobileNumber"), OWNER_CATEGORY("ownerCategory"),
	OWNER_GEOGRAPHY("ownerGeography"), SERVICE("service"),
	SUB_SERVICE("subService"),
	TRANSACTION_COUNT("transactionCount"),
	DIFFERENTIAL_COMMISSION("differentialCommission");
	
	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private AddtnlcommSummryDownldCols(String columnName) {
		this.columnName = columnName;
	}

}
