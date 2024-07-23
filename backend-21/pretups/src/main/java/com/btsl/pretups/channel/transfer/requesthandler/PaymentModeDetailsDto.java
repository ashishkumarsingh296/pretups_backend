package com.btsl.pretups.channel.transfer.requesthandler;

public class PaymentModeDetailsDto {
	
	
	private String  userId;
	private String  commProfileSetId;
	private String  commProfileSetName;
	private String  commLastVersion;
	private String  paymentMode;
	private String  productcode;
	private Long minTransferValue;
	private Long maxTransferValue;
	private Long transferMultipleOff;
	private String transferType;
	
	
	
	public PaymentModeDetailsDto() {
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCommProfileSetId() {
		return commProfileSetId;
	}
	public void setCommProfileSetId(String commProfileSetId) {
		this.commProfileSetId = commProfileSetId;
	}
	public String getCommProfileSetName() {
		return commProfileSetName;
	}
	public void setCommProfileSetName(String commProfileSetName) {
		this.commProfileSetName = commProfileSetName;
	}
	public String getCommLastVersion() {
		return commLastVersion;
	}
	public void setCommLastVersion(String commLastVersion) {
		this.commLastVersion = commLastVersion;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getProductcode() {
		return productcode;
	}
	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}
	public Long getMinTransferValue() {
		return minTransferValue;
	}
	public void setMinTransferValue(Long minTransferValue) {
		this.minTransferValue = minTransferValue;
	}
	public Long getMaxTransferValue() {
		return maxTransferValue;
	}
	public void setMaxTransferValue(Long maxTransferValue) {
		this.maxTransferValue = maxTransferValue;
	}
	public Long getTransferMultipleOff() {
		return transferMultipleOff;
	}

	public void setTransferMultipleOff(Long transferMultipleOff) {
		this.transferMultipleOff = transferMultipleOff;
	}
	
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	@Override
	public String toString() {
		StringBuffer sbf=new StringBuffer();
		sbf.append( "PaymentModeDetailsDto [userId=" + userId + ", commProfileSetId=" + commProfileSetId
				+ ", commProfileSetName=" + commProfileSetName + ", commLastVersion=" + commLastVersion
				+ ", paymentMode=" + paymentMode + ", productcode=" + productcode + ", minTransferValue="
				+ minTransferValue + ", maxTransferValue=" + maxTransferValue + ", transferMultipleOff="
				+ transferMultipleOff + ", transferType=" + transferType + "]");
	  return sbf.toString();
	}	
}
