
package restassuredapi.pojo.ownerUserListResponsepojo;

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
    "codeName",
    "idvalue",
    "label",
    "labelWithValue",
    "otherInfo",
    "otherInfo2",
    "singleStep",
    "status",
    "statusType",
    "type",
    "typeName",
    "value"
})
public class DepartmentList {

    @JsonProperty("codeName")
    private String codeName;
    @JsonProperty("idvalue")
    private String idvalue;
    @JsonProperty("label")
    private String label;
    @JsonProperty("labelWithValue")
    private String labelWithValue;
    @JsonProperty("otherInfo")
    private String otherInfo;
    @JsonProperty("otherInfo2")
    private String otherInfo2;
    @JsonProperty("singleStep")
    private String singleStep;
    @JsonProperty("status")
    private String status;
    @JsonProperty("statusType")
    private String statusType;
    @JsonProperty("type")
    private String type;
    @JsonProperty("typeName")
    private String typeName;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("codeName")
    public String getCodeName() {
        return codeName;
    }

    @JsonProperty("codeName")
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @JsonProperty("idvalue")
    public String getIdvalue() {
        return idvalue;
    }

    @JsonProperty("idvalue")
    public void setIdvalue(String idvalue) {
        this.idvalue = idvalue;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("labelWithValue")
    public String getLabelWithValue() {
        return labelWithValue;
    }

    @JsonProperty("labelWithValue")
    public void setLabelWithValue(String labelWithValue) {
        this.labelWithValue = labelWithValue;
    }

    @JsonProperty("otherInfo")
    public String getOtherInfo() {
        return otherInfo;
    }

    @JsonProperty("otherInfo")
    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    @JsonProperty("otherInfo2")
    public String getOtherInfo2() {
        return otherInfo2;
    }

    @JsonProperty("otherInfo2")
    public void setOtherInfo2(String otherInfo2) {
        this.otherInfo2 = otherInfo2;
    }

    @JsonProperty("singleStep")
    public String getSingleStep() {
        return singleStep;
    }

    @JsonProperty("singleStep")
    public void setSingleStep(String singleStep) {
        this.singleStep = singleStep;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("statusType")
    public String getStatusType() {
        return statusType;
    }

    @JsonProperty("statusType")
    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("typeName")
    public String getTypeName() {
        return typeName;
    }

    @JsonProperty("typeName")
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
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
