
package restassuredapi.pojo.fetchuserdetailsresponsepojo;

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
    "baredType",
    "barredBy",
    "barredOn",
    "module",
    "msisdn",
    "networkName",
    "reasonOfBarring",
    "userId",
    "userName",
    "userType"
})
public class BarredUserDetails {

    @JsonProperty("baredType")
    private String baredType;
    @JsonProperty("barredBy")
    private String barredBy;
    @JsonProperty("barredOn")
    private String barredOn;
    @JsonProperty("module")
    private String module;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("networkName")
    private String networkName;
    @JsonProperty("reasonOfBarring")
    private String reasonOfBarring;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userType")
    private String userType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("baredType")
    public String getBaredType() {
        return baredType;
    }

    @JsonProperty("baredType")
    public void setBaredType(String baredType) {
        this.baredType = baredType;
    }

    @JsonProperty("barredBy")
    public String getBarredBy() {
        return barredBy;
    }

    @JsonProperty("barredBy")
    public void setBarredBy(String barredBy) {
        this.barredBy = barredBy;
    }

    @JsonProperty("barredOn")
    public String getBarredOn() {
        return barredOn;
    }

    @JsonProperty("barredOn")
    public void setBarredOn(String barredOn) {
        this.barredOn = barredOn;
    }

    @JsonProperty("module")
    public String getModule() {
        return module;
    }

    @JsonProperty("module")
    public void setModule(String module) {
        this.module = module;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("networkName")
    public String getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    @JsonProperty("reasonOfBarring")
    public String getReasonOfBarring() {
        return reasonOfBarring;
    }

    @JsonProperty("reasonOfBarring")
    public void setReasonOfBarring(String reasonOfBarring) {
        this.reasonOfBarring = reasonOfBarring;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("userType")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("userType")
    public void setUserType(String userType) {
        this.userType = userType;
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
