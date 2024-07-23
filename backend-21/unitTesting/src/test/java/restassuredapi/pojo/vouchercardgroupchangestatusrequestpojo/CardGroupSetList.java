
package restassuredapi.pojo.vouchercardgroupchangestatusrequestpojo;

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
    "cardGroupSetName",
    "serviceTypeDesc",
    "subServiceTypeDescription",
    "modifiedBy",
    "language1Message",
    "language2Message",
    "status"
})
public class CardGroupSetList {

    @JsonProperty("cardGroupSetName")
    private String cardGroupSetName;
    @JsonProperty("serviceTypeDesc")
    private String serviceTypeDesc;
    @JsonProperty("subServiceTypeDescription")
    private String subServiceTypeDescription;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("language1Message")
    private String language1Message;
    @JsonProperty("language2Message")
    private String language2Message;
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cardGroupSetName")
    public String getCardGroupSetName() {
        return cardGroupSetName;
    }

    @JsonProperty("cardGroupSetName")
    public void setCardGroupSetName(String cardGroupSetName) {
        this.cardGroupSetName = cardGroupSetName;
    }

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

    @JsonProperty("modifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("language1Message")
    public String getLanguage1Message() {
        return language1Message;
    }

    @JsonProperty("language1Message")
    public void setLanguage1Message(String language1Message) {
        this.language1Message = language1Message;
    }

    @JsonProperty("language2Message")
    public String getLanguage2Message() {
        return language2Message;
    }

    @JsonProperty("language2Message")
    public void setLanguage2Message(String language2Message) {
        this.language2Message = language2Message;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
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
