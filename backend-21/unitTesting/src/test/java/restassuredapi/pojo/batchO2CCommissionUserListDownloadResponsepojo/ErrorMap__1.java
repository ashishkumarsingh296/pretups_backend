
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
public class ErrorMap__1 {

    @JsonProperty("masterErrorList")
    private List<MasterError__2> masterErrorList = null;
    @JsonProperty("rowErrorMsgLists")
    private List<RowErrorMsgList__1> rowErrorMsgLists = null;

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

}
