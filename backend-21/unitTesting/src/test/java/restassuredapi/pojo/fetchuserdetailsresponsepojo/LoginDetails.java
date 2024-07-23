
package restassuredapi.pojo.fetchuserdetailsresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "allowedDays",
    "allowedFromTime",
    "allowedIp",
    "allowedToTime",
    "description",
    "invalidPinCount",
    "isPrimary",
    "loginId",
    "networkCode",
    "networkLis",
    "networkName",
    "primaryMsisdn",
    "profileName",
    "secMsisdn",
    "userCode",
    "userPhoneId",
    "userPhoneList"
})
public class LoginDetails {

    @JsonProperty("allowedDays")
    private List<String> allowedDays = null;
    @JsonProperty("allowedFromTime")
    private String allowedFromTime;
    @JsonProperty("allowedIp")
    private String allowedIp;
    @JsonProperty("allowedToTime")
    private String allowedToTime;
    @JsonProperty("description")
    private String description;
    @JsonProperty("invalidPinCount")
    private String invalidPinCount;
    @JsonProperty("isPrimary")
    private String isPrimary;
    @JsonProperty("loginId")
    private String loginId;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("networkLis")
    private List<NetworkLi> networkLis = null;
    @JsonProperty("networkName")
    private String networkName;
    @JsonProperty("primaryMsisdn")
    private String primaryMsisdn;
    @JsonProperty("profileName")
    private String profileName;
    @JsonProperty("secMsisdn")
    private List<String> secMsisdn = null;
    @JsonProperty("userCode")
    private String userCode;
    @JsonProperty("userPhoneId")
    private String userPhoneId;
    @JsonProperty("userPhoneList")
    private List<UserPhoneList> userPhoneList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("allowedDays")
    public List<String> getAllowedDays() {
        return allowedDays;
    }

    @JsonProperty("allowedDays")
    public void setAllowedDays(List<String> allowedDays) {
        this.allowedDays = allowedDays;
    }

    @JsonProperty("allowedFromTime")
    public String getAllowedFromTime() {
        return allowedFromTime;
    }

    @JsonProperty("allowedFromTime")
    public void setAllowedFromTime(String allowedFromTime) {
        this.allowedFromTime = allowedFromTime;
    }

    @JsonProperty("allowedIp")
    public String getAllowedIp() {
        return allowedIp;
    }

    @JsonProperty("allowedIp")
    public void setAllowedIp(String allowedIp) {
        this.allowedIp = allowedIp;
    }

    @JsonProperty("allowedToTime")
    public String getAllowedToTime() {
        return allowedToTime;
    }

    @JsonProperty("allowedToTime")
    public void setAllowedToTime(String allowedToTime) {
        this.allowedToTime = allowedToTime;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("invalidPinCount")
    public String getInvalidPinCount() {
        return invalidPinCount;
    }

    @JsonProperty("invalidPinCount")
    public void setInvalidPinCount(String invalidPinCount) {
        this.invalidPinCount = invalidPinCount;
    }

    @JsonProperty("isPrimary")
    public String getIsPrimary() {
        return isPrimary;
    }

    @JsonProperty("isPrimary")
    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    @JsonProperty("loginId")
    public String getLoginId() {
        return loginId;
    }

    @JsonProperty("loginId")
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("networkLis")
    public List<NetworkLi> getNetworkLis() {
        return networkLis;
    }

    @JsonProperty("networkLis")
    public void setNetworkLis(List<NetworkLi> networkLis) {
        this.networkLis = networkLis;
    }

    @JsonProperty("networkName")
    public String getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    @JsonProperty("primaryMsisdn")
    public String getPrimaryMsisdn() {
        return primaryMsisdn;
    }

    @JsonProperty("primaryMsisdn")
    public void setPrimaryMsisdn(String primaryMsisdn) {
        this.primaryMsisdn = primaryMsisdn;
    }

    @JsonProperty("profileName")
    public String getProfileName() {
        return profileName;
    }

    @JsonProperty("profileName")
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    @JsonProperty("secMsisdn")
    public List<String> getSecMsisdn() {
        return secMsisdn;
    }

    @JsonProperty("secMsisdn")
    public void setSecMsisdn(List<String> secMsisdn) {
        this.secMsisdn = secMsisdn;
    }

    @JsonProperty("userCode")
    public String getUserCode() {
        return userCode;
    }

    @JsonProperty("userCode")
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @JsonProperty("userPhoneId")
    public String getUserPhoneId() {
        return userPhoneId;
    }

    @JsonProperty("userPhoneId")
    public void setUserPhoneId(String userPhoneId) {
        this.userPhoneId = userPhoneId;
    }

    @JsonProperty("userPhoneList")
    public List<UserPhoneList> getUserPhoneList() {
        return userPhoneList;
    }

    @JsonProperty("userPhoneList")
    public void setUserPhoneList(List<UserPhoneList> userPhoneList) {
        this.userPhoneList = userPhoneList;
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
