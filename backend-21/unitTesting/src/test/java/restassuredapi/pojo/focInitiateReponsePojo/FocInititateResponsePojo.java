
package restassuredapi.pojo.focInitiateReponsePojo;

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
    "service",
    "referenceId",
    "status",
    "messageCode",
    "message",
    "errorMap",
    "successList"
})
public class FocInititateResponsePojo {

    @JsonProperty("service")
    private String service;
    @JsonProperty("referenceId")
    private Object referenceId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("successList")
    private List<SuccessList> successList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public FocInititateResponsePojo() {
    }

    /**
     * 
     * @param errorMap
     * @param successList
     * @param service
     * @param messageCode
     * @param message
     * @param referenceId
     * @param status
     */
    public FocInititateResponsePojo(String service, Object referenceId, String status, String messageCode, String message, ErrorMap errorMap, List<SuccessList> successList) {
        super();
        this.service = service;
        this.referenceId = referenceId;
        this.status = status;
        this.messageCode = messageCode;
        this.message = message;
        this.errorMap = errorMap;
        this.successList = successList;
    }

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("referenceId")
    public Object getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
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

    @JsonProperty("errorMap")
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("successList")
    public List<SuccessList> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<SuccessList> successList) {
        this.successList = successList;
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
