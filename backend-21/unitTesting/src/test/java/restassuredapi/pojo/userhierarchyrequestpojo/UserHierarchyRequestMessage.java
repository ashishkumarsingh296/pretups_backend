package restassuredapi.pojo.userhierarchyrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Help class - UserHierarchy Service
 * @author akhilesh.mittal1
 *
 */

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class UserHierarchyRequestMessage {


	@JsonProperty("extcode")
	private String extcode;
	
	@JsonProperty("loginid")
	private String loginid;
	
	@JsonProperty("language2")
	private String language2;
	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("userdetail")
	UserHierarchyVO userdetail;
	
	@JsonProperty("password")
	private String password;
	
	@JsonProperty("pin")
	private String pin;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	

	// Getter Methods

	@JsonProperty("extcode")
	public String getExtcode() {
		return extcode;
	}

	
	@JsonProperty("loginid")
	public String getLoginid() {
		return loginid;
	}

	@JsonProperty("language2")
	public String getLanguage2() {
		return language2;
	}

	
	@JsonProperty("language1")
	public String getLanguage1() {
		return language1;
	}

	@JsonProperty("extnwcode")
	public String getExtnwcode() {
		return extnwcode;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	@JsonProperty("pin")
	public String getPin() {
		return pin;
	}

	@JsonProperty("msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	// Setter Methods

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}


	public UserHierarchyVO getUserdetail() {
		return userdetail;
	}

	public void setUserdetail(UserHierarchyVO userdetail) {
		this.userdetail = userdetail;
	}


	@Override
	public String toString() {
		return "UserHierarchyRequestMessage [extcode=" + extcode + ", loginid=" + loginid + ", language2=" + language2
				+ ", language1=" + language1 + ", extnwcode=" + extnwcode + ", type=" + type + ", userdetail="
				+ userdetail + ", password=" + password + ", pin=" + pin + ", msisdn=" + msisdn + "]";
	}

	
	

}
