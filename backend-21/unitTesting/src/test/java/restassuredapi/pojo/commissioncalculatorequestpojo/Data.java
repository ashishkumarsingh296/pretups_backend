
package restassuredapi.pojo.commissioncalculatorequestpojo;

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
    "fromDate",
    "toDate",
    "extnwcode",
    "msisdn",
    "pin",
    "loginid",
    "password",
    "extcode",
    "msisdn2",
    "loginid2"
})
public class Data {

    @JsonProperty("fromdate")
    private String fromdate;
    @JsonProperty("todate")
    private String todate;
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
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("loginid2")
    private String loginid2;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("fromdate")
    public String getFromDate() {
        return fromdate;
    }

    @JsonProperty("fromdate")
    public void setFromDate(String fromDate) {
        this.fromdate = fromDate;
    }

    @JsonProperty("todate")
    public String getToDate() {
        return todate;
    }

    @JsonProperty("todate")
    public void setToDate(String toDate) {
        this.todate = toDate;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

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

    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }

    @JsonProperty("msisdn2")
    public String getMsisdn2() {
        return msisdn2;
    }

    @JsonProperty("msisdn2")
    public void setMsisdn2(String msisdn2) {
        this.msisdn2 = msisdn2;
    }

    @JsonProperty("loginid2")
    public String getLoginid2() {
        return loginid2;
    }

    @JsonProperty("loginid2")
    public void setLoginid2(String loginid2) {
        this.loginid2 = loginid2;
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
