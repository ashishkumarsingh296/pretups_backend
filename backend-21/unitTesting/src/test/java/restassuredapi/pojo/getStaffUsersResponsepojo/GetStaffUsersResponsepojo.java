
package restassuredapi.pojo.getStaffUsersResponsepojo;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "channelUsersList",
    "errorMap",
    "message",
    "messageCode",
    "referenceId",
    "service",
    "staffUserList",
    "status",
    "successList"
})
@Generated("jsonschema2pojo")
public class GetStaffUsersResponsepojo {

    @JsonProperty("channelUsersList")
    private List<ChannelUsers> channelUsersList = null;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("referenceId")
    private Integer referenceId;
    @JsonProperty("service")
    private String service;
    @JsonProperty("staffUserList")
    private List<StaffUser> staffUserList = null;
    @JsonProperty("status")
    private String status;
    @JsonProperty("successList")
    private List<Success> successList = null;

    @JsonProperty("channelUsersList")
    public List<ChannelUsers> getChannelUsersList() {
        return channelUsersList;
    }

    @JsonProperty("channelUsersList")
    public void setChannelUsersList(List<ChannelUsers> channelUsersList) {
        this.channelUsersList = channelUsersList;
    }

    @JsonProperty("errorMap")
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("referenceId")
    public Integer getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("staffUserList")
    public List<StaffUser> getStaffUserList() {
        return staffUserList;
    }

    @JsonProperty("staffUserList")
    public void setStaffUserList(List<StaffUser> staffUserList) {
        this.staffUserList = staffUserList;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("successList")
    public List<Success> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<Success> successList) {
        this.successList = successList;
    }

}
