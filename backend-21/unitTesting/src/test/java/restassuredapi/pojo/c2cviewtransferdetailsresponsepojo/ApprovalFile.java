
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
public class ApprovalFile {

    @JsonProperty("absolute")
    private boolean absolute;
    @JsonProperty("absolutePath")
    private String absolutePath;
    @JsonProperty("canonicalPath")
    private String canonicalPath;
    @JsonProperty("directory")
    private boolean directory;
    @JsonProperty("file")
    private boolean file;
    @JsonProperty("freeSpace")
    private int freeSpace;
    @JsonProperty("hidden")
    private boolean hidden;
    @JsonProperty("name")
    private String name;
    @JsonProperty("parent")
    private String parent;
    @JsonProperty("path")
    private String path;
    @JsonProperty("totalSpace")
    private int totalSpace;
    @JsonProperty("usableSpace")
    private int usableSpace;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("absolute")
    public boolean isAbsolute() {
        return absolute;
    }

    @JsonProperty("absolute")
    public void setAbsolute(boolean absolute) {
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
    public boolean isDirectory() {
        return directory;
    }

    @JsonProperty("directory")
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @JsonProperty("file")
    public boolean isFile() {
        return file;
    }

    @JsonProperty("file")
    public void setFile(boolean file) {
        this.file = file;
    }

    @JsonProperty("freeSpace")
    public int getFreeSpace() {
        return freeSpace;
    }

    @JsonProperty("freeSpace")
    public void setFreeSpace(int freeSpace) {
        this.freeSpace = freeSpace;
    }

    @JsonProperty("hidden")
    public boolean isHidden() {
        return hidden;
    }

    @JsonProperty("hidden")
    public void setHidden(boolean hidden) {
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
    public int getTotalSpace() {
        return totalSpace;
    }

    @JsonProperty("totalSpace")
    public void setTotalSpace(int totalSpace) {
        this.totalSpace = totalSpace;
    }

    @JsonProperty("usableSpace")
    public int getUsableSpace() {
        return usableSpace;
    }

    @JsonProperty("usableSpace")
    public void setUsableSpace(int usableSpace) {
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
