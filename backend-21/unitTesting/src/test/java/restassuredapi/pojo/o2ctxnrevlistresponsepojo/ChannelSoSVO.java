
package restassuredapi.pojo.o2ctxnrevlistresponsepojo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "country",
    "msisdn",
    "phoneLanguage",
    "sosAllowed",
    "sosAllowedAmount",
    "sosThresholdLimit",
    "userId"
})
@Generated("jsonschema2pojo")
public class ChannelSoSVO {

    @JsonProperty("country")
    private String country;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("phoneLanguage")
    private String phoneLanguage;
    @JsonProperty("sosAllowed")
    private String sosAllowed;
    @JsonProperty("sosAllowedAmount")
    private Integer sosAllowedAmount;
    @JsonProperty("sosThresholdLimit")
    private Integer sosThresholdLimit;
    @JsonProperty("userId")
    private String userId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("phoneLanguage")
    public String getPhoneLanguage() {
        return phoneLanguage;
    }

    @JsonProperty("phoneLanguage")
    public void setPhoneLanguage(String phoneLanguage) {
        this.phoneLanguage = phoneLanguage;
    }

    @JsonProperty("sosAllowed")
    public String getSosAllowed() {
        return sosAllowed;
    }

    @JsonProperty("sosAllowed")
    public void setSosAllowed(String sosAllowed) {
        this.sosAllowed = sosAllowed;
    }

    @JsonProperty("sosAllowedAmount")
    public Integer getSosAllowedAmount() {
        return sosAllowedAmount;
    }

    @JsonProperty("sosAllowedAmount")
    public void setSosAllowedAmount(Integer sosAllowedAmount) {
        this.sosAllowedAmount = sosAllowedAmount;
    }

    @JsonProperty("sosThresholdLimit")
    public Integer getSosThresholdLimit() {
        return sosThresholdLimit;
    }

    @JsonProperty("sosThresholdLimit")
    public void setSosThresholdLimit(Integer sosThresholdLimit) {
        this.sosThresholdLimit = sosThresholdLimit;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
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
