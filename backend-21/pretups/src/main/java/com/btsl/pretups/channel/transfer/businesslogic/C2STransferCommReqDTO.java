
package com.btsl.pretups.channel.transfer.businesslogic;

public class C2STransferCommReqDTO extends CommonDownloadReqDTO {
   
    private String allowedFromTime;
    private String allowedToTime;
    private String reportDate;
    private String categoryCode;
    private String domain;
    private String geography;
    private String service;
    private String transStatus;
    private String reqTab;
    private String mobileNumber;  // for mobile tab
    private String channelUserID; // for advanced tab
    private String optionStaff_LoginIDOrMsisdn;
    private String loginIDOrMsisdn;
    private String networkCode;
    private String userType;
 	
	public String getChannelUserID() {
		return channelUserID;
	}
	public void setChannelUserID(String channelUserID) {
		this.channelUserID = channelUserID;
	}
	public String getOptionStaff_LoginIDOrMsisdn() {
		return optionStaff_LoginIDOrMsisdn;
	}
	public void setOptionStaff_LoginIDOrMsisdn(String optionStaff_LoginIDOrMsisdn) {
		this.optionStaff_LoginIDOrMsisdn = optionStaff_LoginIDOrMsisdn;
	}
	public String getLoginIDOrMsisdn() {
		return loginIDOrMsisdn;
	}
	public void setLoginIDOrMsisdn(String loginIDOrMsisdn) {
		this.loginIDOrMsisdn = loginIDOrMsisdn;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getTransStatus() {
		return transStatus;
	}
	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	
	
	public String getReqTab() {
		return reqTab;
	}
	public void setReqTab(String reqTab) {
		this.reqTab = reqTab;
	}
	
	public String getAllowedFromTime() {
		return allowedFromTime;
	}
	public void setAllowedFromTime(String allowedFromTime) {
		this.allowedFromTime = allowedFromTime;
	}
	public String getAllowedToTime() {
		return allowedToTime;
	}
	public void setAllowedToTime(String allowedToTime) {
		this.allowedToTime = allowedToTime;
	}
	
	
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LowThresholdDownloadReqDTO [ ");
		sb.append("From Date : ");
		sb.append( fromDate ).append(" ,");
		sb.append("To Date : ");
		sb.append( toDate ).append(" ,");
		sb.append("NetworkCode: ");
		sb.append( extnwcode ).append(" ,");
		sb.append("categoryCode: ");
		sb.append( categoryCode ).append(" ,");
		sb.append("domain: ");
		sb.append( domain).append(" ,");
		sb.append("geography: ");
		sb.append( geography).append(" ,");
		sb.append("service: ");
		sb.append( service).append(" ,");
		sb.append("service: ");
		sb.append( transStatus).append(" ]");
		return sb.toString();
	}	

}
