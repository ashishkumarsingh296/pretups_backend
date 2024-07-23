
package restassuredapi.pojo.defaultvouchercardgrouprequestpojo;

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
    "networkCode",
    "userId",
    "serviceTypeId",
    "subServiceTypeId",
    "cardGroupSetId",
    "moduleCode"
})
public class Data {

    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("serviceTypeId")
    private String serviceTypeId;
    @JsonProperty("subServiceTypeId")
    private String subServiceTypeId;
    @JsonProperty("cardGroupSetId")
    private String cardGroupSetId;
    @JsonProperty("moduleCode")
    private String moduleCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("serviceTypeId")
    public String getServiceTypeId() {
        return serviceTypeId;
    }

    @JsonProperty("serviceTypeId")
    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    @JsonProperty("subServiceTypeId")
    public String getSubServiceTypeId() {
        return subServiceTypeId;
    }

    @JsonProperty("subServiceTypeId")
    public void setSubServiceTypeId(String subServiceTypeId) {
        this.subServiceTypeId = subServiceTypeId;
    }

    @JsonProperty("cardGroupSetId")
    public String getCardGroupSetId() {
        return cardGroupSetId;
    }

    @JsonProperty("cardGroupSetId")
    public void setCardGroupSetId(String cardGroupSetId) {
        this.cardGroupSetId = cardGroupSetId;
    }

    @JsonProperty("moduleCode")
    public String getModuleCode() {
        return moduleCode;
    }

    @JsonProperty("moduleCode")
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
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
