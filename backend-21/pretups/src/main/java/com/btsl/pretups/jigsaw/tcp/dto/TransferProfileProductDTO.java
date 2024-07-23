package com.btsl.pretups.jigsaw.tcp.dto;

public class TransferProfileProductDTO {
	
	private String profileId;
	private String productCode;
	private String minResidualBalance;
	private String maxBalance;
	private String alteringBalance;
	private String maxPctTransferAllowed;
	private String c2sMinTxnAmt;
	private String c2sMaxTxnAmt;
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getMinResidualBalance() {
		return minResidualBalance;
	}
	public void setMinResidualBalance(String minResidualBalance) {
		this.minResidualBalance = minResidualBalance;
	}
	public String getMaxBalance() {
		return maxBalance;
	}
	public void setMaxBalance(String maxBalance) {
		this.maxBalance = maxBalance;
	}
	public String getAlteringBalance() {
		return alteringBalance;
	}
	public void setAlteringBalance(String alteringBalance) {
		this.alteringBalance = alteringBalance;
	}
	public String getMaxPctTransferAllowed() {
		return maxPctTransferAllowed;
	}
	public void setMaxPctTransferAllowed(String maxPctTransferAllowed) {
		this.maxPctTransferAllowed = maxPctTransferAllowed;
	}
	public String getC2sMinTxnAmt() {
		return c2sMinTxnAmt;
	}
	public void setC2sMinTxnAmt(String c2sMinTxnAmt) {
		this.c2sMinTxnAmt = c2sMinTxnAmt;
	}
	public String getC2sMaxTxnAmt() {
		return c2sMaxTxnAmt;
	}
	public void setC2sMaxTxnAmt(String c2sMaxTxnAmt) {
		this.c2sMaxTxnAmt = c2sMaxTxnAmt;
	}
	
	
	@Override
	public String toString() {
		return "TransferProfileProductDTO [profileId=" + profileId + ", productCode=" + productCode
				+ ", minResidualBalance=" + minResidualBalance + ", maxBalance=" + maxBalance + ", alteringBalance="
				+ alteringBalance + ", maxPctTransferAllowed=" + maxPctTransferAllowed + ", c2sMinTxnAmt="
				+ c2sMinTxnAmt + ", c2sMaxTxnAmt=" + c2sMaxTxnAmt + "]";
	}

	
	
	
}
