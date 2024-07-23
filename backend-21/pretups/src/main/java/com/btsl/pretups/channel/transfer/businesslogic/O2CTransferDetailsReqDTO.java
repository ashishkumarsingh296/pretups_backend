
package com.btsl.pretups.channel.transfer.businesslogic;

public class O2CTransferDetailsReqDTO extends CommonDownloadReqDTO {
   

	private String domain;
	private String categoryCode;
    private String geography;
    private String user;
    private String transferSubType;
    private String transferCategory;
   private String distributionType;
	    

	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
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


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getTransferSubType() {
		return transferSubType;
	}


	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}





	

	public String getDistributionType() {
		return distributionType;
	}


	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LowThresholdDownloadReqDTO [ ");
		sb.append("From Date : ");
		sb.append( fromDate ).append(" ,");
		sb.append(",To Date : ");
		sb.append( toDate ).append(" ,");
		sb.append(",NetworkCode: ");
		sb.append( extnwcode ).append(" ,");
		sb.append(",categoryCode: ");
		sb.append( categoryCode ).append(" ,");
		sb.append(",domain: ");
		sb.append( domain).append(" ,");
		sb.append(",geography: ");
		sb.append( geography).append(" ,");
		sb.append(",user: ");
		sb.append( user).append(" ,");
		sb.append(" ]");
		return sb.toString();
	}


	public String getTransferCategory() {
		return transferCategory;
	}


	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}	

}
