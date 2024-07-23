
package com.btsl.pretups.channel.transfer.businesslogic;

public class PinPassHistoryReqDTO extends CommonDownloadReqDTO {

	private String domain;
	private String userType;
	private String reqType;
	private String categoryCode;
	private String geography;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	@Override
	public String toString() {
		super.toString();
		StringBuilder sb = new StringBuilder();
		sb.append("PinPassHistoryReqDTO [extnetworkcode=").append(extnwcode).append(",domain=").append(domain)
				.append(",fromDate").append(fromDate).append("toDate").append(toDate).append(",userType=")
				.append(userType).append(",reqType").append(reqType);
		return sb.toString();
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

}
