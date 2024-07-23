
package restassuredapi.pojo.autocompleteuserdetailsrequestpojo;

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
    "password",
    "msisdn",
    "pin",
    "extnwcode",
    "language1",
    "language2",
    "msisdnToSearch",
    "categoryCode",
    "loginidToSearch",
    "usernameToSearch",
    "domainCode",
    "geoDomainCode"
})
public class Data {

    @JsonProperty("extcode")
    private String extcode;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("password")
    private String password;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("msisdnToSearch")
    private String msisdnToSearch;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("loginidToSearch")
    private String loginidToSearch;
    @JsonProperty("usernameToSearch")
    private String usernameToSearch;
    @JsonProperty("domainCode")
    private String domainCode;
    @JsonProperty("geoDomainCode")
    private String geoDomainCode;
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

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String string) {
        this.password = string;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String data1) {
        this.msisdn = data1;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @JsonProperty("msisdnToSearch")
    public String getMsisdnToSearch() {
        return msisdnToSearch;
    }

    @JsonProperty("msisdnToSearch")
    public void setMsisdnToSearch(String msisdnToSearch) {
        this.msisdnToSearch = msisdnToSearch;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("loginidToSearch")
    public String getLoginidToSearch() {
        return loginidToSearch;
    }

    @JsonProperty("loginidToSearch")
    public void setLoginidToSearch(String loginidToSearch) {
        this.loginidToSearch = loginidToSearch;
    }

    @JsonProperty("usernameToSearch")
    public String getUsernameToSearch() {
        return usernameToSearch;
    }

    @JsonProperty("usernameToSearch")
    public void setUsernameToSearch(String usernameToSearch) {
        this.usernameToSearch = usernameToSearch;
    }

    @JsonProperty("domainCode")
    public String getDomainCode() {
        return domainCode;
    }

    @JsonProperty("domainCode")
    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    @JsonProperty("geoDomainCode")
    public String getGeoDomainCode() {
        return geoDomainCode;
    }

    @JsonProperty("geoDomainCode")
    public void setGeoDomainCode(String geoDomainCode) {
        this.geoDomainCode = geoDomainCode;
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
