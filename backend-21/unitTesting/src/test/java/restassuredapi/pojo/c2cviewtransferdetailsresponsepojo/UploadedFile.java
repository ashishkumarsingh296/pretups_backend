
package restassuredapi.pojo.c2cviewtransferdetailsresponsepojo;

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
    "contentType",
    "fileData",
    "fileName",
    "fileSize",
    "inputStream"
})
public class UploadedFile {

    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("fileData")
    private String fileData;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileSize")
    private int fileSize;
    @JsonProperty("inputStream")
    private InputStream inputStream;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("fileData")
    public String getFileData() {
        return fileData;
    }

    @JsonProperty("fileData")
    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("fileSize")
    public int getFileSize() {
        return fileSize;
    }

    @JsonProperty("fileSize")
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    @JsonProperty("inputStream")
    public InputStream getInputStream() {
        return inputStream;
    }

    @JsonProperty("inputStream")
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
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
