package restassuredapi.pojo.userhierarchyrequestpojo;

public class UserHierarchyVO {

	
	private String msisdn;
	private String firstName;
	private String lastName;
	private String loginId;
	private String categoryCode;
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	@Override
	public String toString() {
		return "UserHierarchyVO [msisdn=" + msisdn + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", loginId=" + loginId + ", categoryCode=" + categoryCode + "]";
	}
	
	
	


}
