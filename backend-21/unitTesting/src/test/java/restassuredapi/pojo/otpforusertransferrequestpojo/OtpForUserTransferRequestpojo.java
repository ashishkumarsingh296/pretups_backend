package restassuredapi.pojo.otpforusertransferrequestpojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	  "mode",
	  "msisdn",
	  "reSend"
})

public class OtpForUserTransferRequestpojo {

	@JsonProperty("mode")
	private String mode;
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("reSend")
	private String reSend;
	
	
	public OtpForUserTransferRequestpojo() {
	}
	
	
	@JsonProperty("mode")
	public String getMode() {
		return mode;
	}
	@JsonProperty("mode")
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	@JsonProperty("msisdn")
	public String getMsisdn() {
		return msisdn;
	}
	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	@JsonProperty("reSend")
	public String getReSend() {
		return reSend;
	}
	@JsonProperty("reSend")
	public void setReSend(String reSend) {
		this.reSend = reSend;
	}

}
