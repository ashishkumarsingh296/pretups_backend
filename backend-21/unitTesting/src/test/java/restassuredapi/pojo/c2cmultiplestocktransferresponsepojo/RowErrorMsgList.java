
package restassuredapi.pojo.c2cmultiplestocktransferresponsepojo;

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
    "masterErrorList",
    "rowErrorMsgList",
    "rowName",
    "rowValue"
})
public class RowErrorMsgList {

    @JsonProperty("masterErrorList")
    private List<MasterErrorList_> masterErrorList = null;
    @JsonProperty("rowErrorMsgList")
    private List<RowErrorMsgList_> rowErrorMsgList = null;
    @JsonProperty("rowName")
    private String rowName;
    @JsonProperty("rowValue")
    private String rowValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("masterErrorList")
    public List<MasterErrorList_> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterErrorList_> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    @JsonProperty("rowErrorMsgList")
    public List<RowErrorMsgList_> getRowErrorMsgList() {
        return rowErrorMsgList;
    }

    @JsonProperty("rowErrorMsgList")
    public void setRowErrorMsgList(List<RowErrorMsgList_> rowErrorMsgList) {
        this.rowErrorMsgList = rowErrorMsgList;
    }

    @JsonProperty("rowName")
    public String getRowName() {
        return rowName;
    }

    @JsonProperty("rowName")
    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    @JsonProperty("rowValue")
    public String getRowValue() {
        return rowValue;
    }

    @JsonProperty("rowValue")
    public void setRowValue(String rowValue) {
        this.rowValue = rowValue;
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
