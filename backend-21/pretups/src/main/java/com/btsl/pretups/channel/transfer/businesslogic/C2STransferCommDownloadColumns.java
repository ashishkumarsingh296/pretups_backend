package com.btsl.pretups.channel.transfer.businesslogic;

public enum C2STransferCommDownloadColumns {

	TRANS_DATETIME("transdateTime"), TRANSACTION_ID("transactionID"), SERVICE("service"), SUB_SERVICE("subService"),
	SENDER_MOBILE_TYPE("senderMobileType"), SENDER_NAME("senderName"), SENDER_MOBILE_NUM("senderMobileNumber"),
	SENDER_CATEGORY("senderCategory"), SENDER_GEOGRAPHY("senderGeography"), RECEIVER_NAME("receiverName"),
	RECEIVER_MOB_NUM("receiverMobileNumber"), RECEIVER_SERVICECLASS("receiverServiceClass"),
	EXTERNAL_CODE("networkCode"), REQUEST_SOURCE("requestSource"), REQUESTED_AMOUNT("requestedAmount"), BONUS("bonus"),
	PROCESSING_FEE("processingFee"), COMMISSION_TYPE("commissionType"), ADDITIONAL_COMMISSION("additionalCommission"),
	CAC_RATE("cacRate"), CAC_TYPE("cacType"), CAC_AMOUNT("cacAmount"), ROAM_PENALTY("roamPenalty"),
	CREDITED_AMOUNT("creditedAmount"), TRANSFER_AMOUNT("transferAmount"), VOUCHER_PIN_SENT_TO("pinSentTo"),
	VOUCHER_SERIAL_NUMBER("voucherserialNo"), ADJUSTMENT_TRANS_ID("adjustmentTransID"),
	PARENT_MOBILE_NUMBER("parentMobileNumber"),PARENT_NAME("parentName"),PARENT_CATEGORY("parentCategory"),PARENT_GEOGRAPHY("parentGeography"),
	OWNER_MOBILE_NUMBER("ownerMobileNumber"),OWNER_NAME("ownerName"),OWNER_CATEGORY("ownerCategory"),OWNER_GEOGRAPHY("ownerGeography"),
	MARGIN_RATE("marginRate"),REQUEST_GATEWAY("requestGateway"),PREVIOUS_BALANCE("previousBalance"),POST_BALANCE("postBalance"),EXTERNAL_REF_ID("externalReferenceID"),
	MARGIN_AMOUNT("marginAmount"),MARGIN_TYPE("marginType"),CURRENCY_DETAIL("currencyDetail"),BONUS_TYPE("bonusType"),RECEIVER_BONUS_VALUE("receiverBonusValue"),STATUS("status"),
	TAX1("tax1"),TAX2("tax2") ;
	
	/*
	 * RATE("rate"), LOGIN_ID("loginID"),
	 * DIFFERENTIAL_COMMISSION("differentialCommission"),
	 * TRANSACTION_COUNT("transactionCount"),
	 * GRAND_PARENT_MOBILE_NUM("grantParentMobileNumber"),
	 * GRAND_PARENT_NAME("grandParentName"),TAX2
	 * GRAND_PARENT_CATEGORY("grandParentCategory"),S
	 * GRAND_PARENT_GEOGRAPHY("grandParentGeography"),
	 * DIFFERENTIAL_APPLICABLE("differentialApplicable"),
	 * DIFFERENTIAL_GIVEN("differentialGiven"), TRANSFER_VALUE("transferValue");
	 */

	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private C2STransferCommDownloadColumns(String columnName) {
		this.columnName = columnName;
	}

}
