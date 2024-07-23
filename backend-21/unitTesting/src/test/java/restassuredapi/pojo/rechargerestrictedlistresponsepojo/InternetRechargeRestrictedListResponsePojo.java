
package restassuredapi.pojo.rechargerestrictedlistresponsepojo;

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
    "fileType",
    "fileName",
    "fileattachment",
    "status",
    "messageCode",
    "message"
})
public class InternetRechargeRestrictedListResponsePojo {

    @JsonProperty("fileType")
    private Object fileType;
    @JsonProperty("fileName")
    private Object fileName;
    @JsonProperty("fileattachment")
    private Object fileattachment;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("fileType")
    public Object getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(Object fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("fileName")
    public Object getFileName() {
        return fileName;
    }

    @JsonProperty("fileName")
    public void setFileName(Object fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("fileattachment")
    public Object getFileattachment() {
        return fileattachment;
    }

    @JsonProperty("fileattachment")
    public void setFileattachment(Object fileattachment) {
        this.fileattachment = fileattachment;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
