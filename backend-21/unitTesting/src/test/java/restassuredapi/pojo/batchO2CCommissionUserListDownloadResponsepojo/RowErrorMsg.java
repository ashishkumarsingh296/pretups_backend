
package restassuredapi.pojo.batchO2CCommissionUserListDownloadResponsepojo;

import java.util.List;
import javax.annotation.Generated;
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

    @JsonProperty("rowErrorMsgLists")
    public List<Object> getRowErrorMsgLists() {
        return rowErrorMsgLists;
    }

    @JsonProperty("rowErrorMsgLists")
    public void setRowErrorMsgLists(List<Object> rowErrorMsgLists) {
        this.rowErrorMsgLists = rowErrorMsgLists;
    }

}
