package com.btsl.user.businesslogic;

public class TotalTransactionsDetailedViewResponseVO {
	
	String transactionId;
	String recieverMsisdn;
	String rechargeAmount;
	String status;
	String rechargeDateTime;
	String serviceType;
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getRecieverMsisdn() {
		return recieverMsisdn;
	}
	public void setRecieverMsisdn(String recieverMsisdn) {
		this.recieverMsisdn = recieverMsisdn;
	}
	
	
	public String getRechargeAmount() {
		return rechargeAmount;
	}
	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRechargeDateTime() {
		return rechargeDateTime;
	}
	public void setRechargeDateTime(String rechargeDateTime) {
		this.rechargeDateTime = rechargeDateTime;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	@Override
	public String toString() {
		return "TotalTransactionsDetailedViewResponseVO [transactionId=" + transactionId + ", recieverMsisdn="
				+ recieverMsisdn + ", rechargeAmount=" + rechargeAmount + ", status=" + status + ", rechargeDateTime="
				+ rechargeDateTime + ", serviceType=" + serviceType + "]";
	}
	

}
