
package restassuredapi.pojo.lowthresholddownloadrequestpojo;

import java.util.HashMap;
import java.util.List;
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
    "category",
    "dispHeaderColumnList",
    "domain",
    "extnwcode",
    "fileType",
    "fromDate",
    "geography",
    "threshhold",
    "toDate"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("category")
    private String category;
    @JsonProperty("dispHeaderColumnList")
    private List<DispHeaderColumn> dispHeaderColumnList = null;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("threshhold")
    private String threshhold;
    @JsonProperty("toDate")
    private String toDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("dispHeaderColumnList")
    public List<DispHeaderColumn> getDispHeaderColumnList() {
        return dispHeaderColumnList;
    }

    @JsonProperty("dispHeaderColumnList")
    public void setDispHeaderColumnList(List<DispHeaderColumn> dispHeaderColumnList) {
        this.dispHeaderColumnList = dispHeaderColumnList;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("fileType")
    public String getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("fromDate")
    public String getFromDate() {
        return fromDate;
    }

    @JsonProperty("fromDate")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @JsonProperty("geography")
    public String getGeography() {
        return geography;
    }

    @JsonProperty("geography")
    public void setGeography(String geography) {
        this.geography = geography;
    }

    @JsonProperty("threshhold")
    public String getThreshhold() {
        return threshhold;
    }

    @JsonProperty("threshhold")
    public void setThreshhold(String threshhold) {
        this.threshhold = threshhold;
    }

    @JsonProperty("toDate")
    public String getToDate() {
        return toDate;
    }

    @JsonProperty("toDate")
    public void setToDate(String toDate) {
        this.toDate = toDate;
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
