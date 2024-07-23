
package restassuredapi.pojo.c2cbulkapprovallistresponsepojo;

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
public class C2cBulkApprovalListResponsePojo {

    @JsonProperty("service")
    private Object service;
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
    private List<Object> successList = null;
    @JsonProperty("c2cBatchTransferList")
    private List<Object> c2cBatchTransferList = null;
    @JsonProperty("c2cBatchWithdrawalList")
    private List<Object> c2cBatchWithdrawalList = null;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("service")
    public Object getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(Object service) {
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
    public List<Object> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<Object> successList) {
        this.successList = successList;
    }
    
    @JsonProperty("c2cBatchTransferList")
    public List<Object> getC2cBatchTransferList() {
        return c2cBatchTransferList;
    }

    @JsonProperty("c2cBatchTransferList")
    public void setC2cBatchTransferList(List<Object> c2cBatchTransferList) {
        this.c2cBatchTransferList = c2cBatchTransferList;
    }

    @JsonProperty("c2cBatchWithdrawalList")
    public List<Object> getC2cBatchWithdrawalList() {
        return c2cBatchWithdrawalList;
    }

    @JsonProperty("c2cBatchWithdrawalList")
    public void setC2cBatchWithdrawalList(List<Object> c2cBatchWithdrawalList) {
        this.c2cBatchWithdrawalList = c2cBatchWithdrawalList;
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
