package com.btsl.pretups.channel.transfer.businesslogic;

public class PassbookOthersRecordVO {

	private String transDate;
	private String productName;
	// sender details
	private String userName;
	private String userMobilenumber;
	private String userGeography;
	private String userCategory;
	private String externalCode;
	// ParentDetails
	private String parentName;
	private String parentMobilenumber;
	private String parentCategory;
	private String parentGeography;
	// Owner details
	private String ownerName;
	private String ownerMobileNumber;
	private String ownerCategory;
	private String ownerGeography;
	// O2C details
	private String o2cTransferCount;
	private String o2cTransferAmount;
	private String o2cReturnCount;
	private String o2cReturnAmount;
	private String o2cWithdrawCount;
	private String o2cWithdrawAmount;

	// C2C details
	// Transfer
	private String c2cTransfer_InCount;
	private String c2cTransfer_InAmount;
	private String c2cTransfer_OutCount;
	private String c2cTransfer_OutAmount;
	// Return
	private String c2cTransferRet_InCount;
	private String c2cTransferRet_InAmount;
	private String c2cTransferRet_OutCount;
	private String c2cTransferRet_OutAmount;
	// withdraw
	private String c2cTransferWithdraw_InCount;
	private String c2cTransferWithdraw_InAmount;
	private String c2cTransferWithdraw_OutCount;
	private String c2cTransferWithdraw_OutAmount;

	// c2s details
	private String c2sTransfer_count;
	private String c2sTransfer_amount;
	private String c2sReveral_count;
	private String c2sReveral_amount;
	// commission
	private String additionalcommissionAmount;

	private String reconStatus;
	private String openingBalance;
	private String closingBalance;

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserMobilenumber() {
		return userMobilenumber;
	}

	public void setUserMobilenumber(String userMobilenumber) {
		this.userMobilenumber = userMobilenumber;
	}

	public String getUserGeography() {
		return userGeography;
	}

	public void setUserGeography(String userGeography) {
		this.userGeography = userGeography;
	}

	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentMobilenumber() {
		return parentMobilenumber;
	}

	public void setParentMobilenumber(String parentMobilenumber) {
		this.parentMobilenumber = parentMobilenumber;
	}

	public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public String getParentGeography() {
		return parentGeography;
	}

	public void setParentGeography(String parentGeography) {
		this.parentGeography = parentGeography;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerMobileNumber() {
		return ownerMobileNumber;
	}

	public void setOwnerMobileNumber(String ownerMobileNumber) {
		this.ownerMobileNumber = ownerMobileNumber;
	}

	public String getOwnerCategory() {
		return ownerCategory;
	}

	public void setOwnerCategory(String ownerCategory) {
		this.ownerCategory = ownerCategory;
	}

	public String getOwnerGeography() {
		return ownerGeography;
	}

	public void setOwnerGeography(String ownerGeography) {
		this.ownerGeography = ownerGeography;
	}

	public String getO2cTransferCount() {
		return o2cTransferCount;
	}

	public void setO2cTransferCount(String o2cTransferCount) {
		this.o2cTransferCount = o2cTransferCount;
	}

	public String getO2cTransferAmount() {
		return o2cTransferAmount;
	}

	public void setO2cTransferAmount(String o2cTransferAmount) {
		this.o2cTransferAmount = o2cTransferAmount;
	}

	public String getO2cReturnCount() {
		return o2cReturnCount;
	}

	public void setO2cReturnCount(String o2cReturnCount) {
		this.o2cReturnCount = o2cReturnCount;
	}

	public String getO2cReturnAmount() {
		return o2cReturnAmount;
	}

	public void setO2cReturnAmount(String o2cReturnAmount) {
		this.o2cReturnAmount = o2cReturnAmount;
	}

	public String getO2cWithdrawCount() {
		return o2cWithdrawCount;
	}

	public void setO2cWithdrawCount(String o2cWithdrawCount) {
		this.o2cWithdrawCount = o2cWithdrawCount;
	}

	public String getO2cWithdrawAmount() {
		return o2cWithdrawAmount;
	}

	public void setO2cWithdrawAmount(String o2cWithdrawAmount) {
		this.o2cWithdrawAmount = o2cWithdrawAmount;
	}

	public String getC2cTransfer_InCount() {
		return c2cTransfer_InCount;
	}

	public void setC2cTransfer_InCount(String c2cTransfer_InCount) {
		this.c2cTransfer_InCount = c2cTransfer_InCount;
	}

	public String getC2cTransfer_InAmount() {
		return c2cTransfer_InAmount;
	}

	public void setC2cTransfer_InAmount(String c2cTransfer_InAmount) {
		this.c2cTransfer_InAmount = c2cTransfer_InAmount;
	}

	public String getC2cTransfer_OutCount() {
		return c2cTransfer_OutCount;
	}

	public void setC2cTransfer_OutCount(String c2cTransfer_OutCount) {
		this.c2cTransfer_OutCount = c2cTransfer_OutCount;
	}

	public String getC2cTransfer_OutAmount() {
		return c2cTransfer_OutAmount;
	}

	public void setC2cTransfer_OutAmount(String c2cTransfer_OutAmount) {
		this.c2cTransfer_OutAmount = c2cTransfer_OutAmount;
	}

	public String getC2cTransferRet_InCount() {
		return c2cTransferRet_InCount;
	}

	public void setC2cTransferRet_InCount(String c2cTransferRet_InCount) {
		this.c2cTransferRet_InCount = c2cTransferRet_InCount;
	}

	public String getC2cTransferRet_InAmount() {
		return c2cTransferRet_InAmount;
	}

	public void setC2cTransferRet_InAmount(String c2cTransferRet_InAmount) {
		this.c2cTransferRet_InAmount = c2cTransferRet_InAmount;
	}

	public String getC2cTransferRet_OutCount() {
		return c2cTransferRet_OutCount;
	}

	public void setC2cTransferRet_OutCount(String c2cTransferRet_OutCount) {
		this.c2cTransferRet_OutCount = c2cTransferRet_OutCount;
	}

	public String getC2cTransferRet_OutAmount() {
		return c2cTransferRet_OutAmount;
	}

	public void setC2cTransferRet_OutAmount(String c2cTransferRet_OutAmount) {
		this.c2cTransferRet_OutAmount = c2cTransferRet_OutAmount;
	}

	public String getC2cTransferWithdraw_InCount() {
		return c2cTransferWithdraw_InCount;
	}

	public void setC2cTransferWithdraw_InCount(String c2cTransferWithdraw_InCount) {
		this.c2cTransferWithdraw_InCount = c2cTransferWithdraw_InCount;
	}

	public String getC2cTransferWithdraw_InAmount() {
		return c2cTransferWithdraw_InAmount;
	}

	public void setC2cTransferWithdraw_InAmount(String c2cTransferWithdraw_InAmount) {
		this.c2cTransferWithdraw_InAmount = c2cTransferWithdraw_InAmount;
	}

	public String getC2cTransferWithdraw_OutCount() {
		return c2cTransferWithdraw_OutCount;
	}

	public void setC2cTransferWithdraw_OutCount(String c2cTransferWithdraw_OutCount) {
		this.c2cTransferWithdraw_OutCount = c2cTransferWithdraw_OutCount;
	}

	public String getC2cTransferWithdraw_OutAmount() {
		return c2cTransferWithdraw_OutAmount;
	}

	public void setC2cTransferWithdraw_OutAmount(String c2cTransferWithdraw_OutAmount) {
		this.c2cTransferWithdraw_OutAmount = c2cTransferWithdraw_OutAmount;
	}

	public String getC2sTransfer_count() {
		return c2sTransfer_count;
	}

	public void setC2sTransfer_count(String c2sTransfer_count) {
		this.c2sTransfer_count = c2sTransfer_count;
	}

	public String getC2sTransfer_amount() {
		return c2sTransfer_amount;
	}

	public void setC2sTransfer_amount(String c2sTransfer_amount) {
		this.c2sTransfer_amount = c2sTransfer_amount;
	}

	public String getC2sReveral_count() {
		return c2sReveral_count;
	}

	public void setC2sReveral_count(String c2sReveral_count) {
		this.c2sReveral_count = c2sReveral_count;
	}

	public String getC2sReveral_amount() {
		return c2sReveral_amount;
	}

	public void setC2sReveral_amount(String c2sReveral_amount) {
		this.c2sReveral_amount = c2sReveral_amount;
	}

	public String getAdditionalcommissionAmount() {
		return additionalcommissionAmount;
	}

	public void setAdditionalcommissionAmount(String additionalcommissionAmount) {
		this.additionalcommissionAmount = additionalcommissionAmount;
	}

	public String getReconStatus() {
		return reconStatus;
	}

	public void setReconStatus(String reconStatus) {
		this.reconStatus = reconStatus;
	}

	public String getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}

	public String getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(String closingBalance) {
		this.closingBalance = closingBalance;
	}

}
