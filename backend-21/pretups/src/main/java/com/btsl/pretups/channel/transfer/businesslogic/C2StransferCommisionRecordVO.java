package com.btsl.pretups.channel.transfer.businesslogic;

public class C2StransferCommisionRecordVO {
	
	
	private String transdateTime;
	private String transactionID;

	private String senderMobileType;
	private String senderName;
	private String senderMobileNumber;
	private String senderCategory;
	private String senderGeography;
	private String receiverName;
	private String receiverMobileNumber;
	private String receiverServiceClass;
	//private String 
	
	private String requestSource;
	
	//Transaction Details.
	private String transferAmount;
	private String requestedAmount;
	private String bonusType;
	private String bonus;
	private String creditedAmount;
	private String roamPenalty;
	private String processingFee;
	private String externalCode;
	private String service;
	private String subService;
	
	private String pinSentTo;
	private String voucherserialNo;
	private String adjustmentTransID;
	private String status;
	
	
	
	private String commissionType;
	private String additionalCommission;
	private String cacRate;
	private String cacType;
	private String cacAmount;
	private String currencyDetail;
	
	//Parent Info
	private String parentMobileNumber;
	private String parentName;
	private String parentCategory;	
	private String parentGeography;
	//Owner Info
	private String ownerMobileNumber;
	private String ownerName;
	private String ownerCategory;
	private String ownerGeography;
	
	private String marginRate;
	private String marginAmount;
	private String marginType;
	
	private String requestGateway;
	private String previousBalance;
	private String postBalance;
	private String externalReferenceID;
	private String receiverBonusValue;
	private String tax1;
	private String tax2;
	
	
	public String getTransdateTime() {
		return transdateTime;
	}
	public void setTransdateTime(String transdateTime) {
		this.transdateTime = transdateTime;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getSubService() {
		return subService;
	}
	public void setSubService(String subService) {
		this.subService = subService;
	}
	public String getSenderMobileType() {
		return senderMobileType;
	}
	public void setSenderMobileType(String senderMobileType) {
		this.senderMobileType = senderMobileType;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderMobileNumber() {
		return senderMobileNumber;
	}
	public void setSenderMobileNumber(String senderMobileNumber) {
		this.senderMobileNumber = senderMobileNumber;
	}
	public String getSenderCategory() {
		return senderCategory;
	}
	public void setSenderCategory(String senderCategory) {
		this.senderCategory = senderCategory;
	}
	public String getSenderGeography() {
		return senderGeography;
	}
	public void setSenderGeography(String senderGeography) {
		this.senderGeography = senderGeography;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverMobileNumber() {
		return receiverMobileNumber;
	}
	public void setReceiverMobileNumber(String receiverMobileNumber) {
		this.receiverMobileNumber = receiverMobileNumber;
	}
	public String getReceiverServiceClass() {
		return receiverServiceClass;
	}
	public void setReceiverServiceClass(String receiverServiceClass) {
		this.receiverServiceClass = receiverServiceClass;
	}
	public String getExternalCode() {
		return externalCode;
	}
	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}
	public String getRequestSource() {
		return requestSource;
	}
	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}
	public String getRequestedAmount() {
		return requestedAmount;
	}
	public void setRequestedAmount(String requestedAmount) {
		this.requestedAmount = requestedAmount;
	}
	public String getBonus() {
		return bonus;
	}
	public void setBonus(String bonus) {
		this.bonus = bonus;
	}
	public String getProcessingFee() {
		return processingFee;
	}
	public void setProcessingFee(String processingFee) {
		this.processingFee = processingFee;
	}
	public String getCommissionType() {
		return commissionType;
	}
	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}
	public String getAdditionalCommission() {
		return additionalCommission;
	}
	public void setAdditionalCommission(String additionalCommission) {
		this.additionalCommission = additionalCommission;
	}
	public String getCacRate() {
		return cacRate;
	}
	public void setCacRate(String cacRate) {
		this.cacRate = cacRate;
	}
	public String getCacType() {
		return cacType;
	}
	public void setCacType(String cacType) {
		this.cacType = cacType;
	}
	public String getCacAmount() {
		return cacAmount;
	}
	public void setCacAmount(String cacAmount) {
		this.cacAmount = cacAmount;
	}
	public String getRoamPenalty() {
		return roamPenalty;
	}
	public void setRoamPenalty(String roamPenalty) {
		this.roamPenalty = roamPenalty;
	}
	public String getCreditedAmount() {
		return creditedAmount;
	}
	public void setCreditedAmount(String creditedAmount) {
		this.creditedAmount = creditedAmount;
	}
	public String getTransferAmount() {
		return transferAmount;
	}
	public void setTransferAmount(String transferAmount) {
		this.transferAmount = transferAmount;
	}
	public String getPinSentTo() {
		return pinSentTo;
	}
	public void setPinSentTo(String pinSentTo) {
		this.pinSentTo = pinSentTo;
	}
	public String getVoucherserialNo() {
		return voucherserialNo;
	}
	public void setVoucherserialNo(String voucherserialNo) {
		this.voucherserialNo = voucherserialNo;
	}
	public String getAdjustmentTransID() {
		return adjustmentTransID;
	}
	public void setAdjustmentTransID(String adjustmentTransID) {
		this.adjustmentTransID = adjustmentTransID;
	}

	public String getParentMobileNumber() {
		return parentMobileNumber;
	}
	public void setParentMobileNumber(String parentMobileNumber) {
		this.parentMobileNumber = parentMobileNumber;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
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
	public String getOwnerMobileNumber() {
		return ownerMobileNumber;
	}
	public void setOwnerMobileNumber(String ownerMobileNumber) {
		this.ownerMobileNumber = ownerMobileNumber;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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
	public String getMarginRate() {
		return marginRate;
	}
	public void setMarginRate(String marginRate) {
		this.marginRate = marginRate;
	}
	public String getRequestGateway() {
		return requestGateway;
	}
	public void setRequestGateway(String requestGateway) {
		this.requestGateway = requestGateway;
	}
	public String getPreviousBalance() {
		return previousBalance;
	}
	public void setPreviousBalance(String previousBalance) {
		this.previousBalance = previousBalance;
	}
	public String getPostBalance() {
		return postBalance;
	}
	public void setPostBalance(String postBalance) {
		this.postBalance = postBalance;
	}
	public String getExternalReferenceID() {
		return externalReferenceID;
	}
	public void setExternalReferenceID(String externalReferenceID) {
		this.externalReferenceID = externalReferenceID;
	}
	public String getMarginAmount() {
		return marginAmount;
	}
	public void setMarginAmount(String marginAmount) {
		this.marginAmount = marginAmount;
	}
	public String getMarginType() {
		return marginType;
	}
	public void setMarginType(String marginType) {
		this.marginType = marginType;
	}
	public String getCurrencyDetail() {
		return currencyDetail;
	}
	public void setCurrencyDetail(String currencyDetail) {
		this.currencyDetail = currencyDetail;
	}
	public String getBonusType() {
		return bonusType;
	}
	public void setBonusType(String bonusType) {
		this.bonusType = bonusType;
	}
	public String getReceiverBonusValue() {
		return receiverBonusValue;
	}
	public void setReceiverBonusValue(String receiverBonusValue) {
		this.receiverBonusValue = receiverBonusValue;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTax1() {
		return tax1;
	}
	public void setTax1(String tax1) {
		this.tax1 = tax1;
	}
	public String getTax2() {
		return tax2;
	}
	public void setTax2(String tax2) {
		this.tax2 = tax2;
	}
	

	
	
	
	




}
