
package restassuredapi.pojo.c2CFileUploadApiResponsepojo;

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
    "batchID",
    "errorMap",
    "fileAttachment",
    "fileName",
    "fileValidationErrorList",
    "message",
    "messageCode",
    "referenceId",
    "service",
    "status",
    "successList"
})
public class C2CFileUploadApiResponsePojo {

    @JsonProperty("batchID")
    private String batchID;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("fileAttachment")
    private String fileAttachment;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileValidationErrorList")
    private List<String> fileValidationErrorList = null;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("referenceId")
    private String referenceId;
    @JsonProperty("service")
    private String service;
    @JsonProperty("status")
    private String status;
    @JsonProperty("successList")
    private List<SuccessList> successList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("batchID")
    public String getBatchID() {
        return batchID;
    }

    @JsonProperty("batchID")
    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    @JsonProperty("errorMap")
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("fileAttachment")
    public String getFileAttachment() {
        return fileAttachment;
    }

    @JsonProperty("fileAttachment")
    public void setFileAttachment(String fileAttachment) {
        this.fileAttachment = fileAttachment;
    }

    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("fileValidationErrorList")
    public List<String> getFileValidationErrorList() {
        return fileValidationErrorList;
    }

    @JsonProperty("fileValidationErrorList")
    public void setFileValidationErrorList(List<String> fileValidationErrorList) {
        this.fileValidationErrorList = fileValidationErrorList;
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
    public String getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(String referenceId) {
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
