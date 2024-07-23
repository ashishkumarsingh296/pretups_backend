
package restassuredapi.pojo.dvdapiresponsepojo;

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
    "successList",
    "txnBatchId",
    "txnDetailsList"
})
public class DvdApiResponsePojo {

    @JsonProperty("service")
    private String service;
    @JsonProperty("referenceId")
    private Object referenceId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("messageCode")
    private Object messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("successList")
    private List<SuccessList> successList = null;
    @JsonProperty("txnBatchId")
    private String txnBatchId;
    @JsonProperty("txnDetailsList")
    private List<TxnDetailsList> txnDetailsList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
    public Object getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(Object messageCode) {
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

    @JsonProperty("txnBatchId")
    public String getTxnBatchId() {
        return txnBatchId;
    }

    @JsonProperty("txnBatchId")
    public void setTxnBatchId(String txnBatchId) {
        this.txnBatchId = txnBatchId;
    }

    @JsonProperty("txnDetailsList")
    public List<TxnDetailsList> getTxnDetailsList() {
        return txnDetailsList;
    }

    @JsonProperty("txnDetailsList")
    public void setTxnDetailsList(List<TxnDetailsList> txnDetailsList) {
        this.txnDetailsList = txnDetailsList;
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
