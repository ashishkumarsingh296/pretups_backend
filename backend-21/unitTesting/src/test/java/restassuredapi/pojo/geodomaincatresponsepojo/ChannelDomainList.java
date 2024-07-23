
package restassuredapi.pojo.geodomaincatresponsepojo;

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
    "label",
    "value",
    "codeName",
    "labelWithValue",
    "idvalue",
    "otherInfo2",
    "statusType",
    "singleStep",
    "typeName",
    "otherInfo",
    "status",
    "type"
})
public class ChannelDomainList {

    @JsonProperty("label")
    private String label;
    @JsonProperty("value")
    private String value;
    @JsonProperty("codeName")
    private String codeName;
    @JsonProperty("labelWithValue")
    private String labelWithValue;
    @JsonProperty("idvalue")
    private Object idvalue;
    @JsonProperty("otherInfo2")
    private Object otherInfo2;
    @JsonProperty("statusType")
    private String statusType;
    @JsonProperty("singleStep")
    private Object singleStep;
    @JsonProperty("typeName")
    private Object typeName;
    @JsonProperty("otherInfo")
    private Object otherInfo;
    @JsonProperty("status")
    private Object status;
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public ChannelDomainList() {
    }

    /**
     * 
     * @param singleStep
     * @param otherInfo
     * @param statusType
     * @param codeName
     * @param labelWithValue
     * @param typeName
     * @param idvalue
     * @param label
     * @param type
     * @param value
     * @param otherInfo2
     * @param status
     */
    public ChannelDomainList(String label, String value, String codeName, String labelWithValue, Object idvalue, Object otherInfo2, String statusType, Object singleStep, Object typeName, Object otherInfo, Object status, String type) {
        super();
        this.label = label;
        this.value = value;
        this.codeName = codeName;
        this.labelWithValue = labelWithValue;
        this.idvalue = idvalue;
        this.otherInfo2 = otherInfo2;
        this.statusType = statusType;
        this.singleStep = singleStep;
        this.typeName = typeName;
        this.otherInfo = otherInfo;
        this.status = status;
        this.type = type;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("codeName")
    public String getCodeName() {
        return codeName;
    }

    @JsonProperty("codeName")
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @JsonProperty("labelWithValue")
    public String getLabelWithValue() {
        return labelWithValue;
    }

    @JsonProperty("labelWithValue")
    public void setLabelWithValue(String labelWithValue) {
        this.labelWithValue = labelWithValue;
    }

    @JsonProperty("idvalue")
    public Object getIdvalue() {
        return idvalue;
    }

    @JsonProperty("idvalue")
    public void setIdvalue(Object idvalue) {
        this.idvalue = idvalue;
    }

    @JsonProperty("otherInfo2")
    public Object getOtherInfo2() {
        return otherInfo2;
    }

    @JsonProperty("otherInfo2")
    public void setOtherInfo2(Object otherInfo2) {
        this.otherInfo2 = otherInfo2;
    }

    @JsonProperty("statusType")
    public String getStatusType() {
        return statusType;
    }

    @JsonProperty("statusType")
    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    @JsonProperty("singleStep")
    public Object getSingleStep() {
        return singleStep;
    }

    @JsonProperty("singleStep")
    public void setSingleStep(Object singleStep) {
        this.singleStep = singleStep;
    }

    @JsonProperty("typeName")
    public Object getTypeName() {
        return typeName;
    }

    @JsonProperty("typeName")
    public void setTypeName(Object typeName) {
        this.typeName = typeName;
    }

    @JsonProperty("otherInfo")
    public Object getOtherInfo() {
        return otherInfo;
    }

    @JsonProperty("otherInfo")
    public void setOtherInfo(Object otherInfo) {
        this.otherInfo = otherInfo;
    }

    @JsonProperty("status")
    public Object getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Object status) {
        this.status = status;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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
