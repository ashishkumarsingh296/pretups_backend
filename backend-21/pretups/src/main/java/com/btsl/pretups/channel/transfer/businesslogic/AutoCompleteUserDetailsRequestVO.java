package com.btsl.pretups.channel.transfer.businesslogic;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class AutoCompleteUserDetailsRequestVO {
	private String msisdn2;
	private String loginId2;
	private String UserName2;
	private String domain;
	private String category;
	private boolean specificSearch=false;
	
	public boolean isSpecificSearch() {
		return specificSearch;
	}
	public void setSpecificSearch(boolean specificSearch) {
		this.specificSearch = specificSearch;
	}
	
	public String getMsisdn2() {
		return msisdn2;
	}
	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}
	public String getLoginId2() {
		return loginId2;
	}
	public void setLoginId2(String loginId2) {
		this.loginId2 = loginId2;
	}
	public String getUserName2() {
		return UserName2;
	}
	public void setUserName2(String userName2) {
		UserName2 = userName2;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public String toString() {
		return "AutoCompleteUserDetailsRequestVO [msisdn2=" + msisdn2 + ", loginId2=" + loginId2 + ", UserName2="
				+ UserName2 + ", domain=" + domain + ", category=" + category + "]";
	}
	
	

}
