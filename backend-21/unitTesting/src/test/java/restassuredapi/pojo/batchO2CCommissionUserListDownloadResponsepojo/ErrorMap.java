
package restassuredapi.pojo.batchO2CCommissionUserListDownloadResponsepojo;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "masterErrorList",
    "rowErrorMsgLists"
})
@Generated("jsonschema2pojo")
public class ErrorMap {

    @JsonProperty("masterErrorList")
    private List<MasterError> masterErrorList = null;
    @JsonProperty("rowErrorMsgLists")
    private List<RowErrorMsgList> rowErrorMsgLists = null;

    @JsonProperty("masterErrorList")
    public List<MasterError> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterError> masterErrorList) {
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

}
