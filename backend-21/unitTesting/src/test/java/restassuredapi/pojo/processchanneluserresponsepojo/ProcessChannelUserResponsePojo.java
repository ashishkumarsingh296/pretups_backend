
package restassuredapi.pojo.processchanneluserresponsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "statusCode",
    "status",
    "dataObject"
})
public class ProcessChannelUserResponsePojo {

    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("dataObject")
    private DataObject dataObject;

    @JsonProperty("statusCode")
    public Integer getStatusCode() {
        return statusCode;
    }

    @JsonProperty("statusCode")
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("status")
    public Boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @JsonProperty("dataObject")
    public DataObject getDataObject() {
        return dataObject;
    }

    @JsonProperty("dataObject")
    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

}
