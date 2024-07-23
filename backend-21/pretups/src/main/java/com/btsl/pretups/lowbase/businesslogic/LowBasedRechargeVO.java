package com.btsl.pretups.lowbase.businesslogic;

import java.io.Serializable;
import java.util.Date;


/**
 * This class provide VO object for Low Base Transaction
 * @author lalit.chattar
 */
public class LowBasedRechargeVO implements Serializable {
	
	private static final long serialVersionUID = 6852351252578991955L;
	
	private long minTransferValue=0;
	private long maxTrnasferValue=0;
	private String minTransferValueAsString;
	private String maxTrnasferValueAsString;

	private long count=0;
	private long amount=0;
	private boolean isExists=false;
	private String agentMobileNo;
	private String subscriberMobileNo;
	private String rechargeDate;
	private String transactionNo;
	private String rechargeAmount;
	private String message;
	private String isEligible;

	private String msisdn;
	private long lbmin;
	private long lbmax;
	private double commissionPercentage;
	private Date expiryDate;
	private LowBaseSubscriberOperationType lowBaseSubscriberOperationType;
	
	public String getMinTransferValueAsString() {
		return minTransferValueAsString;
	}
	public void setMinTransferValueAsString(String minTransferValueAsString) {
		this.minTransferValueAsString = minTransferValueAsString;
	}
	public String getMaxTrnasferValueAsString() {
		return maxTrnasferValueAsString;
	}
	public void setMaxTrnasferValueAsString(String maxTrnasferValueAsString) {
		this.maxTrnasferValueAsString = maxTrnasferValueAsString;
	}
	public long getMinTransferValue() {
		return minTransferValue;
	}
	public void setMinTransferValue(long minTransferValue) {
		this.minTransferValue = minTransferValue;
	}
	public long getMaxTrnasferValue() {
		return maxTrnasferValue;
	}
	public void setMaxTrnasferValue(long maxTrnasferValue) {
		this.maxTrnasferValue = maxTrnasferValue;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public boolean isExists() {
		return isExists;
	}
	public void setExists(boolean isExists) {
		this.isExists = isExists;
	}
	public String getAgentMobileNo() {
		return agentMobileNo;
	}
	public void setAgentMobileNo(String agentMobileNo) {
		this.agentMobileNo = agentMobileNo;
	}
	public String getSubscriberMobileNo() {
		return subscriberMobileNo;
	}
	public void setSubscriberMobileNo(String subscriberMobileNo) {
		this.subscriberMobileNo = subscriberMobileNo;
	}
	public String getRechargeDate() {
		return rechargeDate;
	}
	public void setRechargeDate(String rechargeDate) {
		this.rechargeDate = rechargeDate;
	}
	public String getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getIsEligible() {
		return isEligible;
	}
	public void setIsEligible(String isEligible) {
		this.isEligible = isEligible;
	}
	public String getRechargeAmount() {
		return rechargeAmount;
	}
	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb = sb.append("LowBasedRechargeVO [minTransferValue=").append(minTransferValue).append(", maxTrnasferValue=")
				.append(maxTrnasferValue).append(", count=").append(count).append(", amount=").append(amount).append(", isExists=")
				.append(isExists).append(", agentMobileNo=").append(agentMobileNo).append(", subscriberMobileNo=")
				.append(subscriberMobileNo).append(", rechargeDate=").append(rechargeDate).append(", transactionNo=")
				.append(transactionNo).append(", rechargeAmount=").append(rechargeAmount).append(", message=")
				.append(message).append(", isEligible=").append(isEligible).append("]");
		
				return sb.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msisdn == null) ? 0 : msisdn.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		LowBasedRechargeVO other = (LowBasedRechargeVO) obj;
		if (msisdn == null) {
			if (other.msisdn != null)
				return false;
		} else if (!msisdn.equals(other.msisdn))
			return false;
		return true;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public long getLbmin() {
		return lbmin;
	}
	public long getLbmax() {
		return lbmax;
	}
	public double getCommissionPercentage() {
		return commissionPercentage;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public LowBaseSubscriberOperationType getLowBaseSubscriberOperationType() {
		return lowBaseSubscriberOperationType;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public void setLbmin(long lbmin) {
		this.lbmin = lbmin;
	}
	public void setLbmax(long lbmax) {
		this.lbmax = lbmax;
	}
	public void setCommissionPercentage(double commissionPercentage) {
		this.commissionPercentage = commissionPercentage;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public void setLowBaseSubscriberOperationType(
			LowBaseSubscriberOperationType lowBaseSubscriberOperationType) {
		this.lowBaseSubscriberOperationType = lowBaseSubscriberOperationType;
	}
	

}
