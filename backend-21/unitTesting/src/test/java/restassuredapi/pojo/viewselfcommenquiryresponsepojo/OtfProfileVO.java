
package restassuredapi.pojo.viewselfcommenquiryresponsepojo;

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
    "otfDetails",
    "otfDetailsSize",
    "addtnlComStatus",
    "addtnlComStatusName",
    "productCode",
    "otfApplicableFromDate",
    "otfApplicableToDate",
    "productCodeDesc",
    "otfApplicableFrom",
    "otfApplicableTo",
    "otfTimeSlab",
    "commProfileSetID",
    "commProfileSetVersion",
    "commProfileOtfID"
})
public class OtfProfileVO {

    @JsonProperty("otfDetails")
    private Object otfDetails;
    @JsonProperty("otfDetailsSize")
    private String otfDetailsSize;
    @JsonProperty("addtnlComStatus")
    private Object addtnlComStatus;
    @JsonProperty("addtnlComStatusName")
    private Object addtnlComStatusName;
    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("otfApplicableFromDate")
    private Long otfApplicableFromDate;
    @JsonProperty("otfApplicableToDate")
    private Long otfApplicableToDate;
    @JsonProperty("productCodeDesc")
    private String productCodeDesc;
    @JsonProperty("otfApplicableFrom")
    private String otfApplicableFrom;
    @JsonProperty("otfApplicableTo")
    private String otfApplicableTo;
    @JsonProperty("otfTimeSlab")
    private String otfTimeSlab;
    @JsonProperty("commProfileSetID")
    private String commProfileSetID;
    @JsonProperty("commProfileSetVersion")
    private String commProfileSetVersion;
    @JsonProperty("commProfileOtfID")
    private String commProfileOtfID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("otfDetails")
    public Object getOtfDetails() {
        return otfDetails;
    }

    @JsonProperty("otfDetails")
    public void setOtfDetails(Object otfDetails) {
        this.otfDetails = otfDetails;
    }

    @JsonProperty("otfDetailsSize")
    public String getOtfDetailsSize() {
        return otfDetailsSize;
    }

    @JsonProperty("otfDetailsSize")
    public void setOtfDetailsSize(String otfDetailsSize) {
        this.otfDetailsSize = otfDetailsSize;
    }

    @JsonProperty("addtnlComStatus")
    public Object getAddtnlComStatus() {
        return addtnlComStatus;
    }

    @JsonProperty("addtnlComStatus")
    public void setAddtnlComStatus(Object addtnlComStatus) {
        this.addtnlComStatus = addtnlComStatus;
    }

    @JsonProperty("addtnlComStatusName")
    public Object getAddtnlComStatusName() {
        return addtnlComStatusName;
    }

    @JsonProperty("addtnlComStatusName")
    public void setAddtnlComStatusName(Object addtnlComStatusName) {
        this.addtnlComStatusName = addtnlComStatusName;
    }

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("otfApplicableFromDate")
    public Long getOtfApplicableFromDate() {
        return otfApplicableFromDate;
    }

    @JsonProperty("otfApplicableFromDate")
    public void setOtfApplicableFromDate(Long otfApplicableFromDate) {
        this.otfApplicableFromDate = otfApplicableFromDate;
    }

    @JsonProperty("otfApplicableToDate")
    public Long getOtfApplicableToDate() {
        return otfApplicableToDate;
    }

    @JsonProperty("otfApplicableToDate")
    public void setOtfApplicableToDate(Long otfApplicableToDate) {
        this.otfApplicableToDate = otfApplicableToDate;
    }

    @JsonProperty("productCodeDesc")
    public String getProductCodeDesc() {
        return productCodeDesc;
    }

    @JsonProperty("productCodeDesc")
    public void setProductCodeDesc(String productCodeDesc) {
        this.productCodeDesc = productCodeDesc;
    }

    @JsonProperty("otfApplicableFrom")
    public String getOtfApplicableFrom() {
        return otfApplicableFrom;
    }

    @JsonProperty("otfApplicableFrom")
    public void setOtfApplicableFrom(String otfApplicableFrom) {
        this.otfApplicableFrom = otfApplicableFrom;
    }

    @JsonProperty("otfApplicableTo")
    public String getOtfApplicableTo() {
        return otfApplicableTo;
    }

    @JsonProperty("otfApplicableTo")
    public void setOtfApplicableTo(String otfApplicableTo) {
        this.otfApplicableTo = otfApplicableTo;
    }

    @JsonProperty("otfTimeSlab")
    public String getOtfTimeSlab() {
        return otfTimeSlab;
    }

    @JsonProperty("otfTimeSlab")
    public void setOtfTimeSlab(String otfTimeSlab) {
        this.otfTimeSlab = otfTimeSlab;
    }

    @JsonProperty("commProfileSetID")
    public String getCommProfileSetID() {
        return commProfileSetID;
    }

    @JsonProperty("commProfileSetID")
    public void setCommProfileSetID(String commProfileSetID) {
        this.commProfileSetID = commProfileSetID;
    }

    @JsonProperty("commProfileSetVersion")
    public String getCommProfileSetVersion() {
        return commProfileSetVersion;
    }

    @JsonProperty("commProfileSetVersion")
    public void setCommProfileSetVersion(String commProfileSetVersion) {
        this.commProfileSetVersion = commProfileSetVersion;
    }

    @JsonProperty("commProfileOtfID")
    public String getCommProfileOtfID() {
        return commProfileOtfID;
    }

    @JsonProperty("commProfileOtfID")
    public void setCommProfileOtfID(String commProfileOtfID) {
        this.commProfileOtfID = commProfileOtfID;
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
