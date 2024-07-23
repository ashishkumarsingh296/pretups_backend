
package restassuredapi.pojo.modifyvouchercardgrouprequestpojo;

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
    "moduleCode",
    "modifiedBy",
    "serviceTypeDesc",
    "networkCode",
    "cardGroupSetName",
    "cardGroupSetID",
    "subServiceTypeDescription",
    "applicableFromDate",
    "applicableFromHour",
    "version"
})
public class CardGroupDetails {

    @JsonProperty("moduleCode")
    private String moduleCode;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("serviceTypeDesc")
    private String serviceTypeDesc;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("cardGroupSetName")
    private String cardGroupSetName;
    @JsonProperty("cardGroupSetID")
    private String cardGroupSetID;
    @JsonProperty("subServiceTypeDescription")
    private String subServiceTypeDescription;
    @JsonProperty("applicableFromDate")
    private String applicableFromDate;
    @JsonProperty("applicableFromHour")
    private String applicableFromHour;
    @JsonProperty("version")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("moduleCode")
    public String getModuleCode() {
        return moduleCode;
    }

    @JsonProperty("moduleCode")
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    @JsonProperty("modifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("serviceTypeDesc")
    public String getServiceTypeDesc() {
        return serviceTypeDesc;
    }

    @JsonProperty("serviceTypeDesc")
    public void setServiceTypeDesc(String serviceTypeDesc) {
        this.serviceTypeDesc = serviceTypeDesc;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("cardGroupSetName")
    public String getCardGroupSetName() {
        return cardGroupSetName;
    }

    @JsonProperty("cardGroupSetName")
    public void setCardGroupSetName(String cardGroupSetName) {
        this.cardGroupSetName = cardGroupSetName;
    }

    @JsonProperty("cardGroupSetID")
    public String getCardGroupSetID() {
        return cardGroupSetID;
    }

    @JsonProperty("cardGroupSetID")
    public void setCardGroupSetID(String cardGroupSetID) {
        this.cardGroupSetID = cardGroupSetID;
    }

    @JsonProperty("subServiceTypeDescription")
    public String getSubServiceTypeDescription() {
        return subServiceTypeDescription;
    }

    @JsonProperty("subServiceTypeDescription")
    public void setSubServiceTypeDescription(String subServiceTypeDescription) {
        this.subServiceTypeDescription = subServiceTypeDescription;
    }

    @JsonProperty("applicableFromDate")
    public String getApplicableFromDate() {
        return applicableFromDate;
    }

    @JsonProperty("applicableFromDate")
    public void setApplicableFromDate(String applicableFromDate) {
        this.applicableFromDate = applicableFromDate;
    }

    @JsonProperty("applicableFromHour")
    public String getApplicableFromHour() {
        return applicableFromHour;
    }

    @JsonProperty("applicableFromHour")
    public void setApplicableFromHour(String applicableFromHour) {
        this.applicableFromHour = applicableFromHour;
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
