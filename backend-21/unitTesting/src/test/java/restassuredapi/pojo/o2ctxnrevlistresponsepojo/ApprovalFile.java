
package restassuredapi.pojo.o2ctxnrevlistresponsepojo;

import java.util.HashMap;
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
    "absolute",
    "absolutePath",
    "canonicalPath",
    "directory",
    "file",
    "freeSpace",
    "hidden",
    "name",
    "parent",
    "path",
    "totalSpace",
    "usableSpace"
})
@Generated("jsonschema2pojo")
public class ApprovalFile {

    @JsonProperty("absolute")
    private Boolean absolute;
    @JsonProperty("absolutePath")
    private String absolutePath;
    @JsonProperty("canonicalPath")
    private String canonicalPath;
    @JsonProperty("directory")
    private Boolean directory;
    @JsonProperty("file")
    private Boolean file;
    @JsonProperty("freeSpace")
    private Integer freeSpace;
    @JsonProperty("hidden")
    private Boolean hidden;
    @JsonProperty("name")
    private String name;
    @JsonProperty("parent")
    private String parent;
    @JsonProperty("path")
    private String path;
    @JsonProperty("totalSpace")
    private Integer totalSpace;
    @JsonProperty("usableSpace")
    private Integer usableSpace;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("absolute")
    public Boolean getAbsolute() {
        return absolute;
    }

    @JsonProperty("absolute")
    public void setAbsolute(Boolean absolute) {
        this.absolute = absolute;
    }

    @JsonProperty("absolutePath")
    public String getAbsolutePath() {
        return absolutePath;
    }

    @JsonProperty("absolutePath")
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    @JsonProperty("canonicalPath")
    public String getCanonicalPath() {
        return canonicalPath;
    }

    @JsonProperty("canonicalPath")
    public void setCanonicalPath(String canonicalPath) {
        this.canonicalPath = canonicalPath;
    }

    @JsonProperty("directory")
    public Boolean getDirectory() {
        return directory;
    }

    @JsonProperty("directory")
    public void setDirectory(Boolean directory) {
        this.directory = directory;
    }

    @JsonProperty("file")
    public Boolean getFile() {
        return file;
    }

    @JsonProperty("file")
    public void setFile(Boolean file) {
        this.file = file;
    }

    @JsonProperty("freeSpace")
    public Integer getFreeSpace() {
        return freeSpace;
    }

    @JsonProperty("freeSpace")
    public void setFreeSpace(Integer freeSpace) {
        this.freeSpace = freeSpace;
    }

    @JsonProperty("hidden")
    public Boolean getHidden() {
        return hidden;
    }

    @JsonProperty("hidden")
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("parent")
    public String getParent() {
        return parent;
    }

    @JsonProperty("parent")
    public void setParent(String parent) {
        this.parent = parent;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty("totalSpace")
    public Integer getTotalSpace() {
        return totalSpace;
    }

    @JsonProperty("totalSpace")
    public void setTotalSpace(Integer totalSpace) {
        this.totalSpace = totalSpace;
    }

    @JsonProperty("usableSpace")
    public Integer getUsableSpace() {
        return usableSpace;
    }

    @JsonProperty("usableSpace")
    public void setUsableSpace(Integer usableSpace) {
        this.usableSpace = usableSpace;
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
