
package restassuredapi.pojo.networkstockcreationresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "statusCode",
    "status",
    "successMsg",
    "parameters",
    "messageCode",
    "messageKey"
})
public class NetworkStockCreationResponsePojo {

    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("successMsg")
    private String successMsg;
    @JsonProperty("parameters")
    private List<String> parameters = null;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("messageKey")
    private String messageKey;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    @JsonProperty("successMsg")
    public String getSuccessMsg() {
        return successMsg;
    }

    @JsonProperty("successMsg")
    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }

    @JsonProperty("parameters")
    public List<String> getParameters() {
        return parameters;
    }

    @JsonProperty("parameters")
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("messageKey")
    public String getMessageKey() {
        return messageKey;
    }

    @JsonProperty("messageKey")
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
