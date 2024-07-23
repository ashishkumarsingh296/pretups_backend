
package restassuredapi.pojo.BatchO2CApprovalDetailResponsepojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Batch Approval Details",
    "fileAttachment",
    "fileName",
    "fileType",
    "message",
    "messageCode",
    "status",
    "transactionId"
})
public class BatchO2CApprovalDetailResponsepojo {

    @JsonProperty("Batch Approval Details")
    private BatchApprovalDetails batchApprovalDetails;
    @JsonProperty("fileAttachment")
    private String fileAttachment;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Batch Approval Details")
    public BatchApprovalDetails getBatchApprovalDetails() {
        return batchApprovalDetails;
    }

    @JsonProperty("Batch Approval Details")
    public void setBatchApprovalDetails(BatchApprovalDetails batchApprovalDetails) {
        this.batchApprovalDetails = batchApprovalDetails;
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

    @JsonProperty("fileType")
    public String getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(String fileType) {
        this.fileType = fileType;
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
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
