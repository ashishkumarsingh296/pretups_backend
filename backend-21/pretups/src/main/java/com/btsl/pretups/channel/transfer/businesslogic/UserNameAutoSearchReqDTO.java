
package com.btsl.pretups.channel.transfer.businesslogic;

public class UserNameAutoSearchReqDTO extends BaseRequestdata {
   
	private String UserName;
	private String categoryCode;
	private String domainCode;
	private String geography;
	

    
	public String getUserName() {
		return UserName;
	}


	public void setUserName(String userName) {
		UserName = userName;
	}


	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	public String getDomainCode() {
		return domainCode;
	}


	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}


	public String getGeography() {
		return geography;
	}


	public void setGeography(String geography) {
		this.geography = geography;
	}


		@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserNameAutoSearchReqDTO [ ");
		
		sb.append("categoryCode: ");
		sb.append( categoryCode ).append(" ,");
		sb.append(",domain: ");
		sb.append( domainCode).append(" ,");
		sb.append(",geography: ");
		sb.append( geography).append(" ,");
		sb.append(",userName: ");
		sb.append( UserName).append(" ,");
	
		sb.append(" ]");
		return sb.toString();
	}	

}
