
package restassuredapi.pojo.redislookupcacheresponsepojo;

import java.util.HashMap;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "errorMap",
    "map",
    "message",
    "messageCode",
    "status",
    "transactionId"
})
@Generated("jsonschema2pojo")
public class LookupCacheResponsePojo {

    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("map")
    private restassuredapi.pojo.redislookupcacheresponsepojo.Map map;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonIgnore
    private java.util.Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("errorMap")
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("map")
    public restassuredapi.pojo.redislookupcacheresponsepojo.Map getMap() {
        return map;
    }

    @JsonProperty("map")
    public void setMap(restassuredapi.pojo.redislookupcacheresponsepojo.Map map) {
        this.map = map;
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
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
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

    @JsonAnyGetter
    public java.util.Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
