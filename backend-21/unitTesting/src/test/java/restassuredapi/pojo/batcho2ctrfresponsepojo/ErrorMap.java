
package restassuredapi.pojo.batcho2ctrfresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import restassuredapi.pojo.c2cmultiplestocktransferresponsepojo.MasterErrorList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "masterErrorList",
    "rowErrorMsgLists"
})
public class ErrorMap {

    @JsonProperty("masterErrorList")
    private List<MasterErrorList> masterErrorList;
    @JsonProperty("rowErrorMsgLists")
    private List<RowErrorMsgList> rowErrorMsgLists = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("masterErrorList")
    public List<MasterErrorList> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterErrorList> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    @JsonProperty("rowErrorMsgLists")
    public List<RowErrorMsgList> getRowErrorMsgLists() {
        return rowErrorMsgLists;
    }

    @JsonProperty("rowErrorMsgLists")
    public void setRowErrorMsgLists(List<RowErrorMsgList> rowErrorMsgLists) {
        this.rowErrorMsgLists = rowErrorMsgLists;
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
