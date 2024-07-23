
package restassuredapi.pojo.viewvouchercardgrouprequestpojo;

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
    "serviceTypeDesc",
    "subServiceTypeDescription",
    "cardGroupSetName",
    "moduleCode",
    "networkCode",
    "version"
})
public class Data {

    @JsonProperty("serviceTypeDesc")
    private String serviceTypeDesc;
    @JsonProperty("subServiceTypeDescription")
    private String subServiceTypeDescription;
    @JsonProperty("cardGroupSetName")
    private String cardGroupSetName;
    @JsonProperty("moduleCode")
    private String moduleCode;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("version")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("serviceTypeDesc")
    public String getServiceTypeDesc() {
        return serviceTypeDesc;
    }

    @JsonProperty("serviceTypeDesc")
    public void setServiceTypeDesc(String serviceTypeDesc) {
        this.serviceTypeDesc = serviceTypeDesc;
    }

    @JsonProperty("subServiceTypeDescription")
    public String getSubServiceTypeDescription() {
        return subServiceTypeDescription;
    }

    @JsonProperty("subServiceTypeDescription")
    public void setSubServiceTypeDescription(String subServiceTypeDescription) {
        this.subServiceTypeDescription = subServiceTypeDescription;
    }

    @JsonProperty("cardGroupSetName")
    public String getCardGroupSetName() {
        return cardGroupSetName;
    }

    @JsonProperty("cardGroupSetName")
    public void setCardGroupSetName(String cardGroupSetName) {
        this.cardGroupSetName = cardGroupSetName;
    }

    @JsonProperty("moduleCode")
    public String getModuleCode() {
        return moduleCode;
    }

    @JsonProperty("moduleCode")
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
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
