
package restassuredapi.pojo.c2Sadditionalcommissionsummarysearchresponsepojo;

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
    "addtnlcommissionSummaryList",
    "errorMap",
    "message",
    "messageCode",
    "referenceId",
    "service",
    "status",
    "successList",
    "totalDiffAmount",
    "totalTransactionCount"
})
@Generated("jsonschema2pojo")
public class C2SAdditionalCommissionSummarySearchResponsePojo {

    @JsonProperty("addtnlcommissionSummaryList")
    private List<AddtnlcommissionSummary> addtnlcommissionSummaryList = null;
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
    @JsonProperty("status")
    private String status;
    @JsonProperty("successList")
    private List<Success> successList = null;
    @JsonProperty("totalDiffAmount")
    private String totalDiffAmount;
    @JsonProperty("totalTransactionCount")
    private String totalTransactionCount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("addtnlcommissionSummaryList")
    public List<AddtnlcommissionSummary> getAddtnlcommissionSummaryList() {
        return addtnlcommissionSummaryList;
    }

    @JsonProperty("addtnlcommissionSummaryList")
    public void setAddtnlcommissionSummaryList(List<AddtnlcommissionSummary> addtnlcommissionSummaryList) {
        this.addtnlcommissionSummaryList = addtnlcommissionSummaryList;
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

    @JsonProperty("totalDiffAmount")
    public String getTotalDiffAmount() {
        return totalDiffAmount;
    }

    @JsonProperty("totalDiffAmount")
    public void setTotalDiffAmount(String totalDiffAmount) {
        this.totalDiffAmount = totalDiffAmount;
    }

    @JsonProperty("totalTransactionCount")
    public String getTotalTransactionCount() {
        return totalTransactionCount;
    }

    @JsonProperty("totalTransactionCount")
    public void setTotalTransactionCount(String totalTransactionCount) {
        this.totalTransactionCount = totalTransactionCount;
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
