
package restassuredapi.pojo.pinpasswordhistorydownloadresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "masterErrorList",
    "rowErrorMsgLists"
})
@Generated("jsonschema2pojo")
public class ErrorMap__1 {

    @JsonProperty("masterErrorList")
    private List<MasterError__2> masterErrorList = null;
    @JsonProperty("rowErrorMsgLists")
    private List<RowErrorMsgList__1> rowErrorMsgLists = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("masterErrorList")
    public List<MasterError__2> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterError__2> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    @JsonProperty("rowErrorMsgLists")
    public List<RowErrorMsgList__1> getRowErrorMsgLists() {
        return rowErrorMsgLists;
    }

    @JsonProperty("rowErrorMsgLists")
    public void setRowErrorMsgLists(List<RowErrorMsgList__1> rowErrorMsgLists) {
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
