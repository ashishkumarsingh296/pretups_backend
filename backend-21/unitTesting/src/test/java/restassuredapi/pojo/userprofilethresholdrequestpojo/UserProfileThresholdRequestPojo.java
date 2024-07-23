package restassuredapi.pojo.userprofilethresholdrequestpojo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identifierType",
    "identifierValue",
    "loginId",
    "msisdn",
    
})
public class UserProfileThresholdRequestPojo {

	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginId() {
		return loginId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}
	
}

