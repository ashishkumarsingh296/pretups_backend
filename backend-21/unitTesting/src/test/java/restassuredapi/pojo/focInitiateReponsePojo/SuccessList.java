
package restassuredapi.pojo.focInitiateReponsePojo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "messageCode",
    "message",
    "transactionId"
})
public class SuccessList {

    @JsonProperty("status")
    private int status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public SuccessList() {
    }

    /**
     * 
     * @param messageCode
     * @param message
     * @param transactionId
     * @param status
     */
    public SuccessList(int status, String messageCode, String message, String transactionId) {
        super();
        this.status = status;
        this.messageCode = messageCode;
        this.message = message;
        this.transactionId = transactionId;
    }

    @JsonProperty("status")
    public int getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("messageCode", messageCode).append("message", message).append("transactionId", transactionId).append("additionalProperties", additionalProperties).toString();
    }

}
