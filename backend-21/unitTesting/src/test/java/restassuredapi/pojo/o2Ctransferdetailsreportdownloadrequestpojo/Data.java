
package restassuredapi.pojo.o2Ctransferdetailsreportdownloadrequestpojo;

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
    "dispHeaderColumnList",
    "categoryCode",
    "distributionType",
    "domain",
    "extnwcode",
    "fromDate",
    "geography",
    "toDate",
    "transferSubType",
    "transferCategory",
    "user",
    "fileType"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("dispHeaderColumnList")
    private List<DispHeaderColumn> dispHeaderColumnList = null;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("distributionType")
    private String distributionType;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("transferSubType")
    private String transferSubType;
    @JsonProperty("transferCategory")
    private String transferCategory;
    @JsonProperty("user")
    private String user;
    @JsonProperty("fileType")
    private String fileType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dispHeaderColumnList")
    public List<DispHeaderColumn> getDispHeaderColumnList() {
        return dispHeaderColumnList;
    }

    @JsonProperty("dispHeaderColumnList")
    public void setDispHeaderColumnList(List<DispHeaderColumn> dispHeaderColumnList) {
        this.dispHeaderColumnList = dispHeaderColumnList;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    @JsonProperty("distributionType")
    public String getDistributionType() {
        return distributionType;
    }

    @JsonProperty("distributionType")
    public void setDistributionType(String distributionType) {
        this.distributionType = distributionType;
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

    @JsonProperty("toDate")
    public String getToDate() {
        return toDate;
    }

    @JsonProperty("toDate")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @JsonProperty("transferSubType")
    public String getTransferSubType() {
        return transferSubType;
    }

    @JsonProperty("transferSubType")
    public void setTransferSubType(String transferSubType) {
        this.transferSubType = transferSubType;
    }

    @JsonProperty("transferCategory")
    public String getTransferCategory() {
        return transferCategory;
    }

    @JsonProperty("transferCategory")
    public void setTransferCategory(String transferUserCategory) {
        this.transferCategory = transferUserCategory;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }
    
    @JsonProperty("fileType")
    public String getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(String fileType) {
        this.fileType = fileType;
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
