package restassuredapi.pojo.c2cbuyvouchertypeinforequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeData {


	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("voucherList")
	private String voucherList;
	
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

	public String getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(String voucherList) {
		this.voucherList = voucherList;
	}
	
	

	
}
