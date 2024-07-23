
package restassuredapi.pojo.o2CVoucherApprovalResponsePojo;

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
    "rowErrorMsgList"
})
public class RowErrorMsgList__ {

    @JsonProperty("masterErrorList")
    private List<MasterErrorList__> masterErrorList = null;
    @JsonProperty("rowErrorMsgList")
    private List<Object> rowErrorMsgList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("masterErrorList")
    public List<MasterErrorList__> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterErrorList__> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    @JsonProperty("rowErrorMsgList")
    public List<Object> getRowErrorMsgList() {
        return rowErrorMsgList;
    }

    @JsonProperty("rowErrorMsgList")
    public void setRowErrorMsgList(List<Object> rowErrorMsgList) {
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
