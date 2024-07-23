package com.btsl.pretups.channel.userreturn.web;

import java.io.Serializable;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class C2CWithdrawVO implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private String domainName;
	private String domainCode;
	private String geography;
	private String fromCategory;
	private String toCategory;
	private String fromUserId;
	private String toUserId;
	private String fromMsisdn;
	private String toMsisdn;
	private String fromUserName;
	private String toUserName;
	private ChannelUserVO receiverVO;
	private ChannelUserVO senderVO;
	private ChannelTransferItemsVO productList;
	private String amount;
	private String remarks;
	private String requiredQuantityAsString;
	private String transferMRP;
	private String totalReqQuantity;
	private String payableAmount;
	private String netPayableAmount;
	private String totalTax1;
	private String totalTax2;
	private String totalTax3;
	private String totalComm;
	private String totalStock;
	private String senderDebitQuantity;
	private String receiverCreditQuantity;
	private String netCommQuantity;
	
	
	public String getTransferMRP() {
		return transferMRP;
	}
	public void setTransferMRP(String transferMRP) {
		this.transferMRP = transferMRP;
	}
	public String getTotalReqQuantity() {
		return totalReqQuantity;
	}
	public void setTotalReqQuantity(String totalReqQuantity) {
		this.totalReqQuantity = totalReqQuantity;
	}
	public String getPayableAmount() {
		return payableAmount;
	}
	public void setPayableAmount(String payableAmount) {
		this.payableAmount = payableAmount;
	}
	public String getNetPayableAmount() {
		return netPayableAmount;
	}
	public void setNetPayableAmount(String netPayableAmount) {
		this.netPayableAmount = netPayableAmount;
	}
	public String getTotalTax1() {
		return totalTax1;
	}
	public void setTotalTax1(String totalTax1) {
		this.totalTax1 = totalTax1;
	}
	public String getTotalTax2() {
		return totalTax2;
	}
	public void setTotalTax2(String totalTax2) {
		this.totalTax2 = totalTax2;
	}
	public String getTotalTax3() {
		return totalTax3;
	}
	public void setTotalTax3(String totalTax3) {
		this.totalTax3 = totalTax3;
	}
	public String getTotalComm() {
		return totalComm;
	}
	public void setTotalComm(String totalComm) {
		this.totalComm = totalComm;
	}
	public String getTotalStock() {
		return totalStock;
	}
	public void setTotalStock(String totalStock) {
		this.totalStock = totalStock;
	}
	public String getSenderDebitQuantity() {
		return senderDebitQuantity;
	}
	public void setSenderDebitQuantity(String senderDebitQuantity) {
		this.senderDebitQuantity = senderDebitQuantity;
	}
	public String getReceiverCreditQuantity() {
		return receiverCreditQuantity;
	}
	public void setReceiverCreditQuantity(String receiverCreditQuantity) {
		this.receiverCreditQuantity = receiverCreditQuantity;
	}
	public String getNetCommQuantity() {
		return netCommQuantity;
	}
	public void setNetCommQuantity(String netCommQuantity) {
		this.netCommQuantity = netCommQuantity;
	}
	public String getRequiredQuantityAsString() {
		return requiredQuantityAsString;
	}
	public void setRequiredQuantityAsString(String requiredQuantityAsString) {
		this.requiredQuantityAsString = requiredQuantityAsString;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public ChannelUserVO getReceiverVO() {
		return receiverVO;
	}
	public void setReceiverVO(ChannelUserVO receiverVO) {
		this.receiverVO = receiverVO;
	}
	public ChannelUserVO getSenderVO() {
		return senderVO;
	}
	public void setSenderVO(ChannelUserVO senderVO) {
		this.senderVO = senderVO;
	}
	public ChannelTransferItemsVO getProductList() {
		return productList;
	}
	public void setProductList(ChannelTransferItemsVO productList) {
		this.productList = productList;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getFromMsisdn() {
		return fromMsisdn;
	}
	public void setFromMsisdn(String fromMsisdn) {
		this.fromMsisdn = fromMsisdn;
	}
	public String getToMsisdn() {
		return toMsisdn;
	}
	public void setToMsisdn(String toMsisdn) {
		this.toMsisdn = toMsisdn;
	}
	
	
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	public String getFromCategory() {
		return fromCategory;
	}
	public void setFromCategory(String fromCategory) {
		this.fromCategory = fromCategory;
	}
	public String getToCategory() {
		return toCategory;
	}
	public void setToCategory(String toCategory) {
		this.toCategory = toCategory;
	}
	public String getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	
}