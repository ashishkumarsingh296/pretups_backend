
package restassuredapi.pojo.deletevouchercardgrouprequestpojo;

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
    "subServiceTypeDesc",
    "cardGroupSetName",
    "version",
    "modifiedBy",
    "moduleCode",
    "networkCode"
    
})
public class Data {

    @JsonProperty("serviceTypeDesc")
    private String serviceTypeDesc;
    @JsonProperty("subServiceTypeDesc")
    private String subServiceTypeDesc;
    @JsonProperty("cardGroupSetName")
    private String cardGroupSetName;
    @JsonProperty("version")
    private String version;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("moduleCode")
    private String moduleCode;
    @JsonProperty("networkCode")
    private String networkCode;
    
    
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

    @JsonProperty("subServiceTypeDesc")
    public String getSubServiceTypeDesc() {
        return subServiceTypeDesc;
    }

    @JsonProperty("subServiceTypeDesc")
    public void setSubServiceTypeDesc(String subServiceTypeDesc) {
        this.subServiceTypeDesc = subServiceTypeDesc;
    }

    @JsonProperty("cardGroupSetName")
    public String getCardGroupSetName() {
        return cardGroupSetName;
    }

    @JsonProperty("cardGroupSetName")
    public void setCardGroupSetName(String cardGroupSetName) {
        this.cardGroupSetName = cardGroupSetName;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("modifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
