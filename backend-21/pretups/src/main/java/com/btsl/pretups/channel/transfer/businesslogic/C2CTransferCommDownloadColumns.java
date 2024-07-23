package com.btsl.pretups.channel.transfer.businesslogic;

public enum C2CTransferCommDownloadColumns {

	TRANS_DATETIME("transdateTime"), TRANSACTION_ID("transactionID"), SENDER_NAME("senderName"),
	SENDER_MOBILE_NUM("senderMsisdn"), SENDER_CATEGORY("senderCategory"), SENDER_DEBIT_QUANTITY("senderDebitQuantity"),
	RECEIVER_NAME("receiverName"), RECEIVER_MOB_NUM("receiverMsisdn"), RECEIVER_CATEGORY("receiverCategory"),
	RECEIVER_CREDIT_QUANTITY("receiverCreditQuantity"), PRODUCT_NAME("productName"), TRANSFER_IN_OUT("transferInOut"),
	TRANSFER_SUBTYPE("transferSubType"), SOURCE("source"), REQUESTED_QUANTITY("requestedQuantity"),
	DENOMINATION("denomination"), COMMISSION("commission"), CUMULATIVE_BASE_COMMISSION("cumulativeBaseCommission"),
	TAX3("tax3"), PAYABLE_AMOUNT("payableAmount"), NET_PAYABLE_AMOUNT("netPayableAmount"),
	
	TRANSACTION_STATUS("transactionStatus"),TRANSFER_CATEGORY("transferCategory"),REQUEST_GATEWAY("requestGateway"),
	DISTRIBUTION_TYPE("distributionType"),SENDER_PREVIOUS_STOCK("senderPreviousStock"),RECEIVER_PREVIOUS_STOCK("receiverPreviousStock"),
	SENDER_POST_STOCK("senderPostStock"),RECEIVER_POST_STOCK("receiverPostStock"),MODIFIED_ON("modifiedOn"),
	REQUESTED_SOURCE("requestedSource"),TAX1("tax1"),TAX2("tax2"),INITIATOR_USER_NAME("initiatorUserName");
	
	

	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private C2CTransferCommDownloadColumns(String columnName) {
		this.columnName = columnName;
	}

}
