
package restassuredapi.pojo.c2crecentbuyenquiryrequestpojo;

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
    "extcode",
    "loginid",
    "language2",
    "language1",
    "extnwcode",
    "password",
    "pin",
    "msisdn"
})
public class Data {

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
    @JsonProperty("password")
    private String password;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }

    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
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

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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
