
package restassuredapi.pojo.o2cvouchertransferresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "rowValue",
    "rowName",
    "masterErrorList",
    "rowErrorMsgList"
})
public class RowErrorMsgList {

    @JsonProperty("rowValue")
    private String rowValue;
    @JsonProperty("rowName")
    private String rowName;
    @JsonProperty("masterErrorList")
    private List<MasterErrorList> masterErrorList = null;
    @JsonProperty("rowErrorMsgList")
    private Object rowErrorMsgList;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("rowValue")
    public String getRowValue() {
        return rowValue;
    }

    @JsonProperty("rowValue")
    public void setRowValue(String rowValue) {
        this.rowValue = rowValue;
    }

    @JsonProperty("rowName")
    public String getRowName() {
        return rowName;
    }

    @JsonProperty("rowName")
    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    @JsonProperty("masterErrorList")
    public List<MasterErrorList> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterErrorList> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    @JsonProperty("rowErrorMsgList")
    public Object getRowErrorMsgList() {
        return rowErrorMsgList;
    }

    @JsonProperty("rowErrorMsgList")
    public void setRowErrorMsgList(Object rowErrorMsgList) {
        this.rowErrorMsgList = rowErrorMsgList;
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
