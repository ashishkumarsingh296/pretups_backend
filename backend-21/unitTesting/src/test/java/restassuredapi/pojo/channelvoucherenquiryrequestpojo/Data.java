
package restassuredapi.pojo.channelvoucherenquiryrequestpojo;

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
    "msisdn",
    "pin",
    "loginid",
    "password",
    "extcode",
    "vouchertype",
    "vouchersegment",
    "denomination",
    "voucherprofile"
})
public class Data {

    
	@JsonProperty("extnwcode")
    private String extnwcode;
	@JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("password")
    private String password;
    @JsonProperty("extcode")
    private String extcode;
	@JsonProperty("vouchertype")
    private String vouchertype;
    @JsonProperty("vouchersegment")
    private String vouchersegment;
    @JsonProperty("denomination")
    private String denomination;
    @JsonProperty("voucherprofile")
    private String voucherprofile;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("msisdn")
    public String getMsisdn() {
		return msisdn;
	}
    @JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
    @JsonProperty("pin")
	public String getPin() {
		return pin;
	}
    @JsonProperty("pin")
	public void setPin(String pin) {
		this.pin = pin;
	}
    @JsonProperty("extcode")
	public String getExtcode() {
		return extcode;
	}
    @JsonProperty("extcode")
	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

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

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("vouchertype")
    public String getVouchertype() {
        return vouchertype;
    }

    @JsonProperty("vouchertype")
    public void setVouchertype(String vouchertype) {
        this.vouchertype = vouchertype;
    }

    @JsonProperty("vouchersegment")
    public String getVouchersegment() {
        return vouchersegment;
    }

    @JsonProperty("vouchersegment")
    public void setVouchersegment(String vouchersegment) {
        this.vouchersegment = vouchersegment;
    }

    @JsonProperty("denomination")
    public String getDenomination() {
        return denomination;
    }

    @JsonProperty("denomination")
    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    @JsonProperty("voucherprofile")
    public String getVoucherprofile() {
        return voucherprofile;
    }

    @JsonProperty("voucherprofile")
    public void setVoucherprofile(String voucherprofile) {
        this.voucherprofile = voucherprofile;
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
