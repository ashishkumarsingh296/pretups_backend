
package restassuredapi.pojo.addvouchercardgrouprequestpojo;

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
    "status",
    "createdBy",
    "moduleCode",
    "modifiedBy",
    "serviceTypeDesc",
    "networkCode",
    "cardGroupSetName",
    "subServiceTypeDescription",
    "defaultCardGroup",
    "setTypeName",
    "applicableFromDate",
    "applicableFromHour"
})
public class CardGroupDetails {

    @JsonProperty("status")
    private String status;
    @JsonProperty("createdBy")
    private String createdBy;
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
    @JsonProperty("subServiceTypeDescription")
    private String subServiceTypeDescription;
    @JsonProperty("defaultCardGroup")
    private String defaultCardGroup;
    @JsonProperty("setTypeName")
    private String setTypeName;
    @JsonProperty("applicableFromDate")
    private String applicableFromDate;
    @JsonProperty("applicableFromHour")
    private String applicableFromHour;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

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

    @JsonProperty("subServiceTypeDescription")
    public String getSubServiceTypeDescription() {
        return subServiceTypeDescription;
    }

    @JsonProperty("subServiceTypeDescription")
    public void setSubServiceTypeDescription(String subServiceTypeDescription) {
        this.subServiceTypeDescription = subServiceTypeDescription;
    }

    @JsonProperty("defaultCardGroup")
    public String getDefaultCardGroup() {
        return defaultCardGroup;
    }

    @JsonProperty("defaultCardGroup")
    public void setDefaultCardGroup(String defaultCardGroup) {
        this.defaultCardGroup = defaultCardGroup;
    }

    @JsonProperty("setTypeName")
    public String getSetTypeName() {
        return setTypeName;
    }

    @JsonProperty("setTypeName")
    public void setSetTypeName(String setTypeName) {
        this.setTypeName = setTypeName;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
