
package restassuredapi.pojo.pinpasswordhistorydownloadrequestpojo;

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
    "categoryCode",
    "dispHeaderColumnList",
    "domain",
    "extnwcode",
    "fileType",
    "fromDate",
    "reqType",
    "toDate",
    "userType"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("categoryCode")
    private String categoryCode;
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
    @JsonProperty("reqType")
    private String reqType;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("userType")
    private String userType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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

    @JsonProperty("reqType")
    public String getReqType() {
        return reqType;
    }

    @JsonProperty("reqType")
    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    @JsonProperty("toDate")
    public String getToDate() {
        return toDate;
    }

    @JsonProperty("toDate")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @JsonProperty("userType")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("userType")
    public void setUserType(String userType) {
        this.userType = userType;
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
