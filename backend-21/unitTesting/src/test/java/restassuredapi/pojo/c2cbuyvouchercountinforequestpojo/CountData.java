package restassuredapi.pojo.c2cbuyvouchercountinforequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountData {


	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	
	
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	@JsonProperty("loginId")
	public String getLoginId() {
		return loginId;
	}

	@JsonProperty("msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
}
