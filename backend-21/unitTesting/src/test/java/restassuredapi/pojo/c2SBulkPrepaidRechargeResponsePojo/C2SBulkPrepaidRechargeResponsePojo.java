
package restassuredapi.pojo.c2SBulkPrepaidRechargeResponsePojo;

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
    "errorMap",
    "fileAttachment",
    "fileName",
    "message",
    "messageCode",
    "numberOfRecords",
    "scheduleBatchId",
    "status"
})
public class C2SBulkPrepaidRechargeResponsePojo {

    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("fileAttachment")
    private String fileAttachment;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("numberOfRecords")
    private int numberOfRecords;
    @JsonProperty("scheduleBatchId")
    private String scheduleBatchId;
    @JsonProperty("status")
    private String status;
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

    @JsonProperty("numberOfRecords")
    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    @JsonProperty("numberOfRecords")
    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    @JsonProperty("scheduleBatchId")
    public String getScheduleBatchId() {
        return scheduleBatchId;
    }

    @JsonProperty("scheduleBatchId")
    public void setScheduleBatchId(String scheduleBatchId) {
        this.scheduleBatchId = scheduleBatchId;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
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
