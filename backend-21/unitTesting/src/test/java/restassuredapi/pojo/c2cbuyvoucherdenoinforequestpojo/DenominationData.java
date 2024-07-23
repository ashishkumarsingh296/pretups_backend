package restassuredapi.pojo.c2cbuyvoucherdenoinforequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DenominationData {
	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("voucherType")
	private String voucherType;
	
	@JsonProperty("voucherSegment")
	private String voucherSegment;
	
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

	@JsonProperty("voucherType")
	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getVoucherSegment() {
		return voucherSegment;
	}

	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

}
