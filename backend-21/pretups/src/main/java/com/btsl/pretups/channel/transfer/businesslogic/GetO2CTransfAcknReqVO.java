
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Locale;

public class GetO2CTransfAcknReqVO extends CommonDownloadReqDTO {
	private String transactionID;
	private String distributionType;
	private String msisdn;
	private String userID;
	private Locale locale;
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GetO2CTransfAcknReqVO [ ");

		sb.append("transactionID: ");
		sb.append(transactionID).append(" ,");
		sb.append(",distributionType: ");
		sb.append(distributionType).append(" ,");

		sb.append(" ]");
		return sb.toString();
	}

}
