package restassuredapi.pojo.c2cbuyvouchersegmentinforequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SegmentData {

	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("voucherType")
	private String voucherType;
	
	@JsonProperty("voucherType")
	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	
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
