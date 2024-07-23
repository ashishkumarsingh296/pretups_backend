
package restassuredapi.pojo.batchO2CCommissionUserListDownloadResponsepojo;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "batchID",
    "errorMap",
    "fileType",
    "fileattachment",
    "fileName",
    "message",
    "messageCode",
    "referenceId",
    "service",
    "status",
    "successList"
})
@Generated("jsonschema2pojo")
public class BatchO2CCommissionUserListDownloadResponsepojo {

    @JsonProperty("batchID")
    private String batchID;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("fileattachment")
    private String fileattachment;
    @JsonProperty("fileName")
    private String fileName;
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

    @JsonProperty("fileType")
    public String getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("fileattachment")
    public String getFileattachment() {
        return fileattachment;
    }

    @JsonProperty("fileattachment")
    public void setFileattachment(String fileattachment) {
        this.fileattachment = fileattachment;
    }

    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
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

}
