package com.restapi.channelenquiry.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class C2SEnquiryRequestVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "RC", required = true)
	@JsonProperty("service")
	private String service;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DD/MM/YY", required = true)
	@JsonProperty("fromDate")
	private String fromDate;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DD/MM/YY", required = true)
	@JsonProperty("toDate")
	private String toDate;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "R210728.1606.100001", required = true)
	@JsonProperty("transferID")
	private String transferID;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "725252525", required = true)
	@JsonProperty("senderMsisdn")
	private String senderMsisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "752525252", required = true)
	@JsonProperty("receiverMsisdn")
	private String receiverMsisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "true", required = true)
	@JsonProperty("isStaffSearch")
	private boolean isStaffSearch;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGD0002351", required = true)
	@JsonProperty("staffUserID")
	private String staffUserID;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGD0002351", required = true)
	@JsonProperty("parentUserID")
	private String parentUserID;

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	/**
	 * @return the transferID
	 */
	public String getTransferID() {
		return transferID;
	}

	/**
	 * @param transferID the transferID to set
	 */
	public void setTransferID(String transferID) {
		this.transferID = transferID;
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
	 * @return the recieverMsisdn
	 */
	public String getReceiverMsisdn() {
		return receiverMsisdn;
	}

	/**
	 * @param recieverMsisdn the recieverMsisdn to set
	 */
	public void setReceiverMsisdn(String recieverMsisdn) {
		this.receiverMsisdn = recieverMsisdn;
	}

	public boolean isStaffSearch() {
		return isStaffSearch;
	}

	public void setStaffSearch(boolean isStaffSearch) {
		this.isStaffSearch = isStaffSearch;
	}

	public String getStaffUserID() {
		return staffUserID;
	}

	public void setStaffUserID(String staffUserID) {
		this.staffUserID = staffUserID;
	}

	public String getParentUserID() {
		return parentUserID;
	}

	public void setParentUserID(String parentUserID) {
		this.parentUserID = parentUserID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2SEnquiryRequestVO [service=");
		builder.append(service);
		builder.append(", fromDate=");
		builder.append(fromDate);
		builder.append(", toDate=");
		builder.append(toDate);
		builder.append(", transferID=");
		builder.append(transferID);
		builder.append(", senderMsisdn=");
		builder.append(senderMsisdn);
		builder.append(", receiverMsisdn=");
		builder.append(receiverMsisdn);
		builder.append(", isStaffSearch=");
		builder.append(isStaffSearch);
		builder.append(", staffUserID=");
		builder.append(staffUserID);
		builder.append("]");
		return builder.toString();
	}
	
	
}
