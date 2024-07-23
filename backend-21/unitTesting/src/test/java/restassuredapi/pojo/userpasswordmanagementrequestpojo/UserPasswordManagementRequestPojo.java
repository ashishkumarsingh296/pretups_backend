
package restassuredapi.pojo.userpasswordmanagementrequestpojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "childLoginId",
    "childMsisdn",
    "operationID",
    "remarks"
})
@Generated("jsonschema2pojo")
public class UserPasswordManagementRequestPojo {

    @JsonProperty("childLoginId")
    private String childLoginId;
    @JsonProperty("childMsisdn")
    private String childMsisdn;
    @JsonProperty("operationID")
    private String operationID;
    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("childLoginId")
    public String getChildLoginId() {
        return childLoginId;
    }

    @JsonProperty("childLoginId")
    public void setChildLoginId(String childLoginId) {
        this.childLoginId = childLoginId;
    }

    @JsonProperty("childMsisdn")
    public String getChildMsisdn() {
        return childMsisdn;
    }

    @JsonProperty("childMsisdn")
    public void setChildMsisdn(String childMsisdn) {
        this.childMsisdn = childMsisdn;
    }

    @JsonProperty("operationID")
    public String getOperationID() {
        return operationID;
    }

    @JsonProperty("operationID")
    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
