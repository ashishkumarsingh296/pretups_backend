
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
    "otfValue",
    "otfType",
    "otfRate",
    "otfProfileID",
    "otfDetailID",
    "otfCountOrAmount",
    "otfApplicableFrom",
    "otfApplicableTo",
    "otfApplicableFromStr",
    "otfApplicableToStr",
    "origOtfApplicableTo",
    "otfTimeSlab",
    "otfTypePctOrAMt",
    "_domainCode",
    "commissionProfileType",
    "batchId",
    "otfValueLong",
    "productCode",
    "commProfileSetId",
    "otfRateDouble",
    "categoryCode",
    "shortCode",
    "setVersion",
    "commProfileSetName",
    "grphDomainCode",
    "gradeCode"
})
public class SlabsList_ {

    @JsonProperty("otfValue")
    private String otfValue;
    @JsonProperty("otfType")
    private String otfType;
    @JsonProperty("otfRate")
    private String otfRate;
    @JsonProperty("otfProfileID")
    private String otfProfileID;
    @JsonProperty("otfDetailID")
    private String otfDetailID;
    @JsonProperty("otfCountOrAmount")
    private Object otfCountOrAmount;
    @JsonProperty("otfApplicableFrom")
    private Object otfApplicableFrom;
    @JsonProperty("otfApplicableTo")
    private Object otfApplicableTo;
    @JsonProperty("otfApplicableFromStr")
    private Object otfApplicableFromStr;
    @JsonProperty("otfApplicableToStr")
    private Object otfApplicableToStr;
    @JsonProperty("origOtfApplicableTo")
    private Object origOtfApplicableTo;
    @JsonProperty("otfTimeSlab")
    private Object otfTimeSlab;
    @JsonProperty("otfTypePctOrAMt")
    private Object otfTypePctOrAMt;
    @JsonProperty("_domainCode")
    private Object domainCode;
    @JsonProperty("commissionProfileType")
    private Object commissionProfileType;
    @JsonProperty("batchId")
    private Object batchId;
    @JsonProperty("otfValueLong")
    private Long otfValueLong;
    @JsonProperty("productCode")
    private Object productCode;
    @JsonProperty("commProfileSetId")
    private Object commProfileSetId;
    @JsonProperty("otfRateDouble")
    private Long otfRateDouble;
    @JsonProperty("categoryCode")
    private Object categoryCode;
    @JsonProperty("shortCode")
    private Object shortCode;
    @JsonProperty("setVersion")
    private Object setVersion;
    @JsonProperty("commProfileSetName")
    private Object commProfileSetName;
    @JsonProperty("grphDomainCode")
    private Object grphDomainCode;
    @JsonProperty("gradeCode")
    private Object gradeCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("otfValue")
    public String getOtfValue() {
        return otfValue;
    }

    @JsonProperty("otfValue")
    public void setOtfValue(String otfValue) {
        this.otfValue = otfValue;
    }

    @JsonProperty("otfType")
    public String getOtfType() {
        return otfType;
    }

    @JsonProperty("otfType")
    public void setOtfType(String otfType) {
        this.otfType = otfType;
    }

    @JsonProperty("otfRate")
    public String getOtfRate() {
        return otfRate;
    }

    @JsonProperty("otfRate")
    public void setOtfRate(String otfRate) {
        this.otfRate = otfRate;
    }

    @JsonProperty("otfProfileID")
    public String getOtfProfileID() {
        return otfProfileID;
    }

    @JsonProperty("otfProfileID")
    public void setOtfProfileID(String otfProfileID) {
        this.otfProfileID = otfProfileID;
    }

    @JsonProperty("otfDetailID")
    public String getOtfDetailID() {
        return otfDetailID;
    }

    @JsonProperty("otfDetailID")
    public void setOtfDetailID(String otfDetailID) {
        this.otfDetailID = otfDetailID;
    }

    @JsonProperty("otfCountOrAmount")
    public Object getOtfCountOrAmount() {
        return otfCountOrAmount;
    }

    @JsonProperty("otfCountOrAmount")
    public void setOtfCountOrAmount(Object otfCountOrAmount) {
        this.otfCountOrAmount = otfCountOrAmount;
    }

    @JsonProperty("otfApplicableFrom")
    public Object getOtfApplicableFrom() {
        return otfApplicableFrom;
    }

    @JsonProperty("otfApplicableFrom")
    public void setOtfApplicableFrom(Object otfApplicableFrom) {
        this.otfApplicableFrom = otfApplicableFrom;
    }

    @JsonProperty("otfApplicableTo")
    public Object getOtfApplicableTo() {
        return otfApplicableTo;
    }

    @JsonProperty("otfApplicableTo")
    public void setOtfApplicableTo(Object otfApplicableTo) {
        this.otfApplicableTo = otfApplicableTo;
    }

    @JsonProperty("otfApplicableFromStr")
    public Object getOtfApplicableFromStr() {
        return otfApplicableFromStr;
    }

    @JsonProperty("otfApplicableFromStr")
    public void setOtfApplicableFromStr(Object otfApplicableFromStr) {
        this.otfApplicableFromStr = otfApplicableFromStr;
    }

    @JsonProperty("otfApplicableToStr")
    public Object getOtfApplicableToStr() {
        return otfApplicableToStr;
    }

    @JsonProperty("otfApplicableToStr")
    public void setOtfApplicableToStr(Object otfApplicableToStr) {
        this.otfApplicableToStr = otfApplicableToStr;
    }

    @JsonProperty("origOtfApplicableTo")
    public Object getOrigOtfApplicableTo() {
        return origOtfApplicableTo;
    }

    @JsonProperty("origOtfApplicableTo")
    public void setOrigOtfApplicableTo(Object origOtfApplicableTo) {
        this.origOtfApplicableTo = origOtfApplicableTo;
    }

    @JsonProperty("otfTimeSlab")
    public Object getOtfTimeSlab() {
        return otfTimeSlab;
    }

    @JsonProperty("otfTimeSlab")
    public void setOtfTimeSlab(Object otfTimeSlab) {
        this.otfTimeSlab = otfTimeSlab;
    }

    @JsonProperty("otfTypePctOrAMt")
    public Object getOtfTypePctOrAMt() {
        return otfTypePctOrAMt;
    }

    @JsonProperty("otfTypePctOrAMt")
    public void setOtfTypePctOrAMt(Object otfTypePctOrAMt) {
        this.otfTypePctOrAMt = otfTypePctOrAMt;
    }

    @JsonProperty("_domainCode")
    public Object getDomainCode() {
        return domainCode;
    }

    @JsonProperty("_domainCode")
    public void setDomainCode(Object domainCode) {
        this.domainCode = domainCode;
    }

    @JsonProperty("commissionProfileType")
    public Object getCommissionProfileType() {
        return commissionProfileType;
    }

    @JsonProperty("commissionProfileType")
    public void setCommissionProfileType(Object commissionProfileType) {
        this.commissionProfileType = commissionProfileType;
    }

    @JsonProperty("batchId")
    public Object getBatchId() {
        return batchId;
    }

    @JsonProperty("batchId")
    public void setBatchId(Object batchId) {
        this.batchId = batchId;
    }

    @JsonProperty("otfValueLong")
    public Long getOtfValueLong() {
        return otfValueLong;
    }

    @JsonProperty("otfValueLong")
    public void setOtfValueLong(Long otfValueLong) {
        this.otfValueLong = otfValueLong;
    }

    @JsonProperty("productCode")
    public Object getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(Object productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("commProfileSetId")
    public Object getCommProfileSetId() {
        return commProfileSetId;
    }

    @JsonProperty("commProfileSetId")
    public void setCommProfileSetId(Object commProfileSetId) {
        this.commProfileSetId = commProfileSetId;
    }

    @JsonProperty("otfRateDouble")
    public Long getOtfRateDouble() {
        return otfRateDouble;
    }

    @JsonProperty("otfRateDouble")
    public void setOtfRateDouble(Long otfRateDouble) {
        this.otfRateDouble = otfRateDouble;
    }

    @JsonProperty("categoryCode")
    public Object getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(Object categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("shortCode")
    public Object getShortCode() {
        return shortCode;
    }

    @JsonProperty("shortCode")
    public void setShortCode(Object shortCode) {
        this.shortCode = shortCode;
    }

    @JsonProperty("setVersion")
    public Object getSetVersion() {
        return setVersion;
    }

    @JsonProperty("setVersion")
    public void setSetVersion(Object setVersion) {
        this.setVersion = setVersion;
    }

    @JsonProperty("commProfileSetName")
    public Object getCommProfileSetName() {
        return commProfileSetName;
    }

    @JsonProperty("commProfileSetName")
    public void setCommProfileSetName(Object commProfileSetName) {
        this.commProfileSetName = commProfileSetName;
    }

    @JsonProperty("grphDomainCode")
    public Object getGrphDomainCode() {
        return grphDomainCode;
    }

    @JsonProperty("grphDomainCode")
    public void setGrphDomainCode(Object grphDomainCode) {
        this.grphDomainCode = grphDomainCode;
    }

    @JsonProperty("gradeCode")
    public Object getGradeCode() {
        return gradeCode;
    }

    @JsonProperty("gradeCode")
    public void setGradeCode(Object gradeCode) {
        this.gradeCode = gradeCode;
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
