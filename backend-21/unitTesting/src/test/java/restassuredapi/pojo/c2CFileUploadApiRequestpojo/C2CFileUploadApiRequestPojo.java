
package restassuredapi.pojo.c2CFileUploadApiRequestpojo;

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
    "batchName",
    "fileAttachment",
    "fileName",
    "fileType",
    "language1",
    "language2",
    "pin"
})
public class C2CFileUploadApiRequestPojo {

    @JsonProperty("batchName")
    private String batchName;
    @JsonProperty("fileAttachment")
    private String fileAttachment;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("pin")
    private String pin;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("batchName")
    public String getBatchName() {
        return batchName;
    }

    @JsonProperty("batchName")
    public void setBatchName(String batchName) {
        this.batchName = batchName;
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

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
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
