
package restassuredapi.pojo.batchO2CCommissionUserListDownloadResponsepojo;

import java.util.List;
import javax.annotation.Generated;
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
@Generated("jsonschema2pojo")
public class RowErrorMsgList {

    @JsonProperty("masterErrorList")
    private List<MasterError__1> masterErrorList = null;
    @JsonProperty("rowErrorMsgList")
    private List<RowErrorMsg> rowErrorMsgList = null;
    @JsonProperty("rowName")
    private String rowName;
    @JsonProperty("rowValue")
    private String rowValue;

    @JsonProperty("masterErrorList")
    public List<MasterError__1> getMasterErrorList() {
        return masterErrorList;
    }

    @JsonProperty("masterErrorList")
    public void setMasterErrorList(List<MasterError__1> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    @JsonProperty("rowErrorMsgList")
    public List<RowErrorMsg> getRowErrorMsgList() {
        return rowErrorMsgList;
    }

    @JsonProperty("rowErrorMsgList")
    public void setRowErrorMsgList(List<RowErrorMsg> rowErrorMsgList) {
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

}
