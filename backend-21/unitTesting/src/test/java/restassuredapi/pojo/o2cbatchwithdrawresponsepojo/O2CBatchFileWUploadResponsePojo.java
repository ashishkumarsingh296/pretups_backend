package restassuredapi.pojo.o2cbatchwithdrawresponsepojo;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import restassuredapi.pojo.bulkgiftrechargeresponsepojo.ErrorMap;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "service",
    "referenceId",
    "status",
    "messageCode",
    "message",
    "errorMap",
    "successList",
    "fileAttachment",
    "batchID",
    "fileName"
})
public class O2CBatchFileWUploadResponsePojo {
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
    private List<Object> successList = null;
    @JsonProperty("fileAttachment")
    private String fileAttachment;
    @JsonProperty("batchID")
    private String batchID;
    @JsonProperty("fileName")
    private String fileName;
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
    public List<Object> getSuccessList() {
        return successList;
    }
    @JsonProperty("successList")
    public void setSuccessList(List<Object> successList) {
        this.successList = successList;
    }
    @JsonProperty("fileAttachment")
    public String getFileAttachment() {
        return fileAttachment;
    }
    @JsonProperty("fileAttachment")
    public void setFileAttachment(String fileAttachment) {
        this.fileAttachment = fileAttachment;
    }
    @JsonProperty("batchID")
    public String getBatchID() {
        return batchID;
    }
    @JsonProperty("batchID")
    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }
    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }
    @JsonProperty("fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
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