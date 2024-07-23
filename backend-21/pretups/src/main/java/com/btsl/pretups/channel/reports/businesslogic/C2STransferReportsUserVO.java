package com.btsl.pretups.channel.reports.businesslogic;

import java.io.Serializable;



/**
 * @author tarun.kumar
 *
 */
public class C2STransferReportsUserVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String transactionId;
	private String transferTime;
	private String requestSource;
	private String userName;
	private String senderMobileNumber;
	private String receiverMobileNumber;
	private String serviceClass;
	private String service;
	private String subService;
	private String requestAmount;
	private String creditAmount;
	private String bonus;
	private String processFee;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}
	public String getRequestSource() {
		return requestSource;
	}
	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSenderMobileNumber() {
		return senderMobileNumber;
	}
	public void setSenderMobileNumber(String senderMobileNumber) {
		this.senderMobileNumber = senderMobileNumber;
	}
	public String getReceiverMobileNumber() {
		return receiverMobileNumber;
	}
	public void setReceiverMobileNumber(String receiverMobileNumber) {
		this.receiverMobileNumber = receiverMobileNumber;
	}
	public String getServiceClass() {
		return serviceClass;
	}
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
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
	public String getRequestAmount() {
		return requestAmount;
	}
	public void setRequestAmount(String requestAmount) {
		this.requestAmount = requestAmount;
	}
	public String getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}
	public String getBonus() {
		return bonus;
	}
	public void setBonus(String bonus) {
		this.bonus = bonus;
	}
	public String getProcessFee() {
		return processFee;
	}
	public void setProcessFee(String processFee) {
		this.processFee = processFee;
	}
	@Override
	public String toString() {
		return "C2STransferReportsUserVO [transactionId=" + transactionId
				+ ", transferTime=" + transferTime + ", requestSource="
				+ requestSource + ", userName=" + userName
				+ ", senderMobileNumber=" + senderMobileNumber
				+ ", receiverMobileNumber=" + receiverMobileNumber
				+ ", serviceClass=" + serviceClass + ", service=" + service
				+ ", subService=" + subService + ", requestAmount="
				+ requestAmount + ", creditAmount=" + creditAmount + ", bonus="
				+ bonus + ", processFee=" + processFee + "]";
	}
	
	
}
