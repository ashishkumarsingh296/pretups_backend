package com.restapi.channelAdmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;



public class CAC2STransferReversalConfirmVO {
	
	@JsonProperty("transactionID")
	@io.swagger.v3.oas.annotations.media.Schema(example = "R201222.1756.720001", required = true/* , defaultValue = "" */)
	private String transactionID;
	
	@JsonProperty("senderMsisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72757575"/* , defaultValue = "" */)
	private String senderMsisdn;
	
	@JsonProperty("userMsisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "70016754"/* , defaultValue = "" */)
	private String userMsisdn;
	
	
	/**
	 * @return the transactionID
	 */
	public String getTransactionID() {
		return transactionID;
	}

	/**
	 * @param transactionID the transactionID to set
	 */
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	/**
	 * @return the senderMsisdn
	 */
	public String getSenderMsisdn() {
		return senderMsisdn;
	}

	/**
	 * @param senderMsisdn the senderMsisdn to set
	 */
	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}

	/**
	 * @return the userMsisdn
	 */
	public String getUserMsisdn() {
		return userMsisdn;
	}

	/**
	 * @param userMsisdn the userMsisdn to set
	 */
	public void setUserMsisdn(String userMsisdn) {
		this.userMsisdn = userMsisdn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CAC2STransferReversalConfirmVO [transactionID=").append(transactionID).append(", senderMsisdn=")
				.append(senderMsisdn).append(", userMsisdn=").append(userMsisdn).append("]");
		return builder.toString();
	}

	
	
	
	

}
