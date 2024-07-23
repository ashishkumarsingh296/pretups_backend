package restassuredapi.pojo.addCateogryPojo;

import restassuredapi.pojo.BaseResponsePojo;

public class AddCategoryRespPojo extends BaseResponsePojo{
	
	private String categoryCode;
	private String domainCode;
	private String agentAllowed;
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
	public String getAgentAllowed() {
		return agentAllowed;
	}
	public void setAgentAllowed(String agentAllowed) {
		this.agentAllowed = agentAllowed;
	}

}
