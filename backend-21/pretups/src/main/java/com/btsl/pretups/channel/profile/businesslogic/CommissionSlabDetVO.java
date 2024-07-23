package com.btsl.pretups.channel.profile.businesslogic;

import java.util.List;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * CommissionSlabDetVO
 *

 */
public class CommissionSlabDetVO {

	// Commission Slabs details
	private String product;
	private String multipleOf;
	private String transactionType;
	private String paymentMode;
	private String minTransferValue;
	private String maxTransferValue;
	private String taxCalcOnFOC;
	private String taxCalcOnC2CTransfer;
	List<CommissionSlabDetails> listCommissionSlabDet;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getMultipleOf() {
		return multipleOf;
	}

	public void setMultipleOf(String multipleOf) {
		this.multipleOf = multipleOf;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getMinTransferValue() {
		return minTransferValue;
	}

	public void setMinTransferValue(String minTransferValue) {
		this.minTransferValue = minTransferValue;
	}

	public String getMaxTransferValue() {
		return maxTransferValue;
	}

	public void setMaxTransferValue(String maxTransferValue) {
		this.maxTransferValue = maxTransferValue;
	}

	public String getTaxCalcOnFOC() {
		return taxCalcOnFOC;
	}

	public void setTaxCalcOnFOC(String taxCalcOnFOC) {
		this.taxCalcOnFOC = taxCalcOnFOC;
	}

	public String getTaxCalcOnC2CTransfer() {
		return taxCalcOnC2CTransfer;
	}

	public void setTaxCalcOnC2CTransfer(String taxCalcOnC2CTransfer) {
		this.taxCalcOnC2CTransfer = taxCalcOnC2CTransfer;
	}

	public List<CommissionSlabDetails> getListCommissionSlabDet() {
		return listCommissionSlabDet;
	}

	public void setListCommissionSlabDet(List<CommissionSlabDetails> listCommissionSlabDet) {
		this.listCommissionSlabDet = listCommissionSlabDet;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" CommissionSlabDetVO : [ ParentName :")

				.append("]");
		return sb.toString();
	}

}
