
package com.btsl.pretups.channel.transfer.requesthandler;

public class ProductPaymentModesVO {	
	
	private String paymentMode;
	private Long minTransferValue;
	private Long maxTransferValue;
	private String transferType;
	private Boolean isSlabExisted;
	private Boolean isDefault;
	
	
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
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
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	
	
	public Boolean getIsSlabExisted() {
		return isSlabExisted;
	}
	public void setIsSlabExisted(Boolean isSlabExisted) {
		this.isSlabExisted = isSlabExisted;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("ProductPaymentModesVO [paymentMode=" + paymentMode + ", minTransferValue=" + minTransferValue
				+ ", maxTransferValue=" + maxTransferValue + ", transferType=" + transferType + ", isSlabExisted="
				+ isSlabExisted + ", isDefault=" + isDefault + "]");
		return sb.toString();
	}
	
	
	
	
	
	
	
}
