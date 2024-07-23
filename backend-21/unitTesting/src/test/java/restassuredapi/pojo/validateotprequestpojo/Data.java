
package restassuredapi.pojo.validateotprequestpojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "extnwcode",
    "loginid",
    "password",
    "msisdn",
    "pin",
    "extcode",
    "serviceType",
    "language2",
    "language1"
})
public class Data {

    @JsonProperty("extnwcode")
    private String extnwcode;
    
    @JsonProperty("loginid")
    private String loginid;
    
    @JsonProperty("msisdn")
    private String msisdn;
    
    @JsonProperty("extcode")
    private String extcode;
    
    @JsonProperty("language2")
    private String language2;
    
    @JsonProperty("language1")
    private String language1;
    
    @JsonProperty("otp")
	private String otp;
	
	@JsonProperty("newpin")
	private String newpin;
	
	@JsonProperty("confirmpin")
	private String confirmpin;
    
    
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }
    
    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }
    
    @JsonProperty("otp")
    public String getOtp() {
		return otp;
	}

    @JsonProperty("otp")
	public void setOtp(String otp) {
		this.otp = otp;
	}

    @JsonProperty("newpin")
	public String getNewpin() {
		return newpin;
	}

    @JsonProperty("newpin")
	public void setNewpin(String newpin) {
		this.newpin = newpin;
	}

    @JsonProperty("confirmpin")
	public String getConfirmpin() {
		return confirmpin;
	}

    @JsonProperty("confirmpin")
	public void setConfirmpin(String confirmpin) {
		this.confirmpin = confirmpin;
	}

	@JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
