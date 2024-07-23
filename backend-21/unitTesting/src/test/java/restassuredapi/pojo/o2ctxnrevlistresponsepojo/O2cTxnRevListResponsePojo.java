
package restassuredapi.pojo.o2ctxnrevlistresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    "message",
    "messageCode",
    "o2CTxnReversalList",
    "o2CTxnReversalListSize",
    "referenceId",
    "service",
    "status",
    "successList"
})
@Generated("jsonschema2pojo")
public class O2cTxnRevListResponsePojo {

    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("o2CTxnReversalList")
    private List<O2CTxnReversal> o2CTxnReversalList = null;
    @JsonProperty("o2CTxnReversalListSize")
    private Integer o2CTxnReversalListSize;
    @JsonProperty("referenceId")
    private Integer referenceId;
    @JsonProperty("service")
    private String service;
    @JsonProperty("status")
    private String status;
    @JsonProperty("successList")
    private List<Success> successList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    @JsonProperty("o2CTxnReversalList")
    public List<O2CTxnReversal> getO2CTxnReversalList() {
        return o2CTxnReversalList;
    }

    @JsonProperty("o2CTxnReversalList")
    public void setO2CTxnReversalList(List<O2CTxnReversal> o2CTxnReversalList) {
        this.o2CTxnReversalList = o2CTxnReversalList;
    }

    @JsonProperty("o2CTxnReversalListSize")
    public Integer getO2CTxnReversalListSize() {
        return o2CTxnReversalListSize;
    }

    @JsonProperty("o2CTxnReversalListSize")
    public void setO2CTxnReversalListSize(Integer o2CTxnReversalListSize) {
        this.o2CTxnReversalListSize = o2CTxnReversalListSize;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
