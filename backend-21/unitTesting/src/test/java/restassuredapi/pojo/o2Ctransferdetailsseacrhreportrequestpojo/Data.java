
package restassuredapi.pojo.o2Ctransferdetailsseacrhreportrequestpojo;

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
    "categoryCode",
    "distributionType",
    "domain",
    "extnwcode",
    "fromDate",
    "geography",
    "toDate",
    "transferSubType",
    "transferCategory",
    "user"
})
@Generated("jsonschema2pojo")
public class Data {

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
    public void setTransferCategory(String transferCategory) {
        this.transferCategory = transferCategory;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
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
