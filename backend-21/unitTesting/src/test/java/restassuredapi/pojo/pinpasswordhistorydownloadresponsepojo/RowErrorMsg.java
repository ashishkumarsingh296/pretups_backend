
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
    "rowErrorMsgLists"
})
@Generated("jsonschema2pojo")
public class RowErrorMsg {

    @JsonProperty("rowErrorMsgLists")
    private List<Object> rowErrorMsgLists = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("rowErrorMsgLists")
    public List<Object> getRowErrorMsgLists() {
        return rowErrorMsgLists;
    }

    @JsonProperty("rowErrorMsgLists")
    public void setRowErrorMsgLists(List<Object> rowErrorMsgLists) {
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
