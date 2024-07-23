package com.btsl.pretups.channel.transfer.businesslogic;

public enum PassbookOtherDownloadColumns {

	TRANS_DATE("transDate"), PRODUCT_NAME("productName"), USER_NAME("userName"), USER_MOB_NUM("userMobilenumber"),
	USER_GEOGRAPHY("userGeography"),USER_CATEGORY("userCategory"),EXTERNAL_CODE("externalCode"),PARENT_NAME("parentName"),
	PARENT_MOB_NUM("parentMobilenumber"),PARENT_CATEGORY("parentCategory"),PARENT_GEOGRAPHY("parentGeography"),
	OWNER_NAME("ownerName"),OWNER_MOBILE_NUMBER("ownerMobileNumber"),OWNER_CATEGORY("ownerCategory"),
	OWNER_GEOGRAPHY("ownerGeography"),O2C_TRANSFER_COUNT("o2cTransferCount"),O2C_TRANSFER_AMOUNT("o2cTransferAmount"),
	O2C_RETURN_COUNT("o2cReturnCount"),O2C_RETURN_AMOUNT("o2cReturnAmount"),O2C_WITHDRAWAL_COUNT("o2cWithdrawCount"),
	O2C_WITHDRAWAL_AMOUNT("o2cWithdrawAmount"),C2C_TRANSFER_INCOUNT("c2cTransfer_InCount"),C2C_TRANSFER_INAMOUNT("c2cTransfer_InAmount"),
	C2C_TRANSFER_OUTCOUNT("c2cTransfer_OutCount"),C2C_TRANSFER_OUTAMOUNT("c2cTransfer_OutAmount"),
	C2C_TRANSFER_RETURN_INCOUNT("c2cTransferRet_InCount"),C2C_TRANSFER_RETURN_INAMOUNT("c2cTransferRet_InAmount"),
	C2C_TRANSFER_RETURN_OUTCOUNT("c2cTransferRet_OUTCount"),C2C_TRANSFER_RETURN_OUTAMOUNT("c2cTransferRet_OUTAmount"),
	C2C_TRANSFER_WITHDRAW_INCOUNT("c2cTransferWithdraw_InCount"),C2C_TRANSFER_WITHDRAW_INAMOUNT("c2cTransferWithdraw_InAmount"),
	C2C_TRANSFER_WITHDRAW_OUTCOUNT("c2cTransferWithdraw_OutCount"),C2C_TRANSFER_WITHDRAW_OUTAMOUNT("c2cTransferWithdraw_OutAmount"),
	C2S_TRANSFER_COUNT("c2sTransfer_count"),C2S_TRANSFER_AMOUNT("c2sTransfer_amount"),C2S_REVERSAL_COUNT("c2sReveral_count"),C2S_REVERSAL_AMOUNT("c2sReveral_amount"),
	COMMISSION("commission"),ADDITIONAL_COMM_AMOUNT("additionalcommissionAmount"),PROMO_BONUS("promoBonus"),
	CBC("cummulativeAdditionalCommission"),RECON_STATUS("reconStatus"),OPENING_BALANCE("openingBalance"),CLOSING_BALANCE("closingBalance");

	
	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private PassbookOtherDownloadColumns(String columnName) {
		this.columnName = columnName;
	}

}
