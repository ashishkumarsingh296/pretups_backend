package com.btsl.pretups.channel.transfer.businesslogic;

public enum O2CTransfDetDownloadColumns {

   DATE_TIME("dateTime") ,TRANSACTION_ID("transactionID"),SENDER_NAME("senderName"),SENDER_MSISDN("senderMsisdn"),RECEIVER_NAME("receiverName"),RECEIVER_MSISDN("receiverMsisdn"),
   RECEIVER_QUANTITY("receiverQuantity"),TRANSFER_CATEGORY("transferCategory"),TRANSFER_SUB_TYPE("transferSubType"),
   MODIFEID_ON("modifiedOn"),PRODUCT_NAME("productName"),TRANS_DATE_EXTERNAL("externalTransferDate"),TRANS_NUMBER_EXTERNAL("externalTransferNumber"),
   TRANSACTION_MODE("transactionMode"),REQUESTED_QUANTITY("requestedQuantity"),APPROVED_QUANTITY("approvedQuantity"),COMMISSION("commission"),
   CUMMULATIVE_BASE_COMMISSION("cumulativeBaseCommission"),TAX1_AMOUNT("tax1"),TAX2_AMOUNT("tax2"),TAX3_AMOUNT("tax3"),PAYABLE_AMOUNT("payableAmount"),NET_PAYABLE_AMOUNT("netPayableAmount"),
   INITIAL_REMARKS("initiatorRemarks"),APPROVER1_REMARKS("approver1Remarks"),APPROVER2_REMARKS("approver2Remarks"),APPROVER3_REMARKS("approver3Remarks"),
   VOUCHER_BATCH_NUMBER("voucherBatchNumber"),VOMS_PRODUCT_NAME("vomsProductName"),BATCH_TYPE("batchType"),TOTAL_NO_OF_VOUCHERS("totalNoofVouchers"),
   FROM_SERIAL_NUMBER("fromSerialNumber"),TO_SERIAL_NUMBER("toSerialNumber"),
   VOUCHER_SEGMENT("voucherSegment"),VOUCHER_TYPE("voucherType"),VOUCHER_DENOMINATION("voucherDenomination"),DISTRIBUTION_TYPE("distributionType"),
   TRANSACTION_STATUS("transactionStatus"),FIRST_APPROVED_QUANTITY("firstLevelApprovedQuantity"),
   SECOND_APPROVED_QUANTITY("secondLevelApprovedQuantity"),THIRD_APPROVED_QUANTITY("thirdLevelApprovedQuantity"),
   PAYMENT_INTRUMENT_TYPE("paymentInstType"),PAYMENT_INTRUMENT_NUMBER("paymentInstNumber"),PAYMENT_INTRUMENT_DATE("paymentInstDate"),
   REQUEST_GATEWAY("requestGateWay"),SENDER_DEBIT_QUANTITY("senderDebitQuantity"),DOMAIN_NAME("domainName"),CREATED_ON("createdOn"),CLOSE_DATE("closeDate"),
   RECEIVER_PRE_BALANCE("receiverPreviousBalance"),   RECEIVER_POST_BALANCE("receiverPostBalance");
   
   
	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private O2CTransfDetDownloadColumns(String columnName) {
		this.columnName = columnName;
	}

}
