
package com.btsl.pretups.channel.transfer.businesslogic;

public class PassbookOthersReqDTO extends CommonDownloadReqDTO {

	
	private String categoryCode;
	private String domain;
	private String geography;
	private String user;
	private String product;
	private String networkCode;

	
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PassbookOthersReqDTO [ ");
		sb.append("From Date : ");
		sb.append(fromDate).append(" ,");
		sb.append("To Date : ");
		sb.append(toDate).append(" ,");
		sb.append("NetworkCode: ");
		sb.append(networkCode).append(" ,");
		sb.append("categoryCode: ");
		sb.append(categoryCode).append(" ,");
		sb.append("domain: ");
		sb.append(domain).append(" ,");
		sb.append("geography: ");
		sb.append(geography).append(" ,");
		sb.append("User: ");
		sb.append(user).append(" ,");
		sb.append("Product").append(product);
		
		sb.append(" ]");
		return sb.toString();
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

}
