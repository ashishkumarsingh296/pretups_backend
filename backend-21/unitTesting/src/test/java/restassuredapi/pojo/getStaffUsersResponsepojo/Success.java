
package restassuredapi.pojo.getStaffUsersResponsepojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "errorMap",
    "message",
    "messageCode",
    "status",
    "transactionId"
})
@Generated("jsonschema2pojo")
public class Success {

    @JsonProperty("errorMap")
    private ErrorMap__1 errorMap;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("errorMap")
    public ErrorMap__1 getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap__1 errorMap) {
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

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
