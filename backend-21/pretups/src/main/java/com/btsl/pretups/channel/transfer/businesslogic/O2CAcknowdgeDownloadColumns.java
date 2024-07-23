package com.btsl.pretups.channel.transfer.businesslogic;

public enum O2CAcknowdgeDownloadColumns {

   DATE_TIME("dateTime") ,TRANSACTION_ID("transactionID"),USER_NAME("userName"),STATUS("status"),DOMAIN("domain"),CATEGORY("category"),
   GEOGRAPHY("geography"),MOBILE_NUMBER("mobileNumber"),NETWORK_NAME("networkName"),COMMISION_PROFILE("commissionProfile"),
   TRANSFER_PROFILE("transferProfile"),TRANSFER_TYPE("transferType"),TRANSFER_CATEGORY("transferCategory"),TRANS_DATE_EXTERNAL("transDateExternal"),TRANS_NUMBER_EXTERNAL("transNumberExternal"),
   
   REFERENCE_NUMBER("referenceNumber"),ERP_CODE("erpCode"),ADDRESS("address"),PRODUCT_SHORT_CODE("productShortCode"),PRODUCT_NAME("productName"),
   DENOMINATION("denomination"),QUANTITY("quantity"),APPROVED_QUANTITY("approvedQuantity"),LEVEL1_APPROVED_QUANTITY("level1ApprovedQuantity"),
   LEVEL2_APPROVED_QUANTITY("level2ApprovedQuantity"),LEVEL3_APPROVED_QUANTITY("level3ApprovedQuantity"),TAX1_RATE("tax1Rate"),TAX1_TYPE("tax1Type"),
   TAX1_AMOUNT("tax1Amount"),TAX2_RATE("tax2Rate"),TAX2_TYPE("tax2Type"),TAX2_AMOUNT("tax2Amount"),TAX3_RATE("tax3Rate"),TDS("tds"),
   COMMISSION_RATE("commisionRate"),COMMISSION_TYPE("commisionType"),COMMISSION_AMOUNT("commisionAmount"),RECEIVER_CREDIT_QUANTIY("receiverCreditQuantity"),
   CBC_RATE("cbcRate"),CBC_TYPE("cbcType"),CBC_AMOUNT("cbcAmount"),DENOMINATION_AMOUNT("denominationAmount"),PAYABLE_AMOUNT("payableAmount"),NET_AMOUNT("netAmount"),
   PAYMENT_INSTRUMENT_NUMBER("paymentInstrumentNumber"),PAYMENT_INSTRUMENT_DATE("paymentInstrumentDate"),PAYMENT_INSTRUMENT_AMOUNT("paymentInstrumentAmount"),
   PAYMENT_MODE("paymentMode"),FIRST_APPROVER_REMARKS("firstApprovedRemarks"),SECOND_APPROVER_REMARKS("secondApprovedRemarks"),THIRD_APPROVER_REMARKS("thirdApprovedRemarks"),
   VOUCHER_BATCH_NUMBER("voucherBatchNumber"),VOMS_PRODUCT_NAME("vomsProductName"),BATCH_TYPE("batchType"),TOTAL_NO_OF_VOUCHERS("totalNoofVouchers"),
   FROM_SERIAL_NUMBER("fromSerialNumber"),TO_SERIAL_NUMBER("toSerialNumber");

	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private O2CAcknowdgeDownloadColumns(String columnName) {
		this.columnName = columnName;
	}

}
