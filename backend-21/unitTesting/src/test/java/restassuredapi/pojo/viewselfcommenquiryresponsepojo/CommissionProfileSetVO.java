
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
    "dualCommissionType",
    "networkCode",
    "categoryName",
    "gatewayCode",
    "language1Message",
    "language2Message",
    "commProfileSetId",
    "modifiedBy",
    "modifiedOn",
    "createdBy",
    "createdOn",
    "applicableFrom",
    "lastModifiedOn",
    "categoryCode",
    "networkName",
    "shortCode",
    "status",
    "setVersion",
    "commLastVersion",
    "commProfileSetName",
    "defaultProfile",
    "gradeName",
    "grphDomainName",
    "commProfileVersion",
    "grphDomainCode",
    "additionalCommissionTimeSlab",
    "batch_ID",
    "gradeCode",
    "combinedKey"
})
public class CommissionProfileSetVO {

    @JsonProperty("dualCommissionType")
    private Object dualCommissionType;
    @JsonProperty("networkCode")
    private Object networkCode;
    @JsonProperty("categoryName")
    private Object categoryName;
    @JsonProperty("gatewayCode")
    private Object gatewayCode;
    @JsonProperty("language1Message")
    private Object language1Message;
    @JsonProperty("language2Message")
    private Object language2Message;
    @JsonProperty("commProfileSetId")
    private String commProfileSetId;
    @JsonProperty("modifiedBy")
    private Object modifiedBy;
    @JsonProperty("modifiedOn")
    private Object modifiedOn;
    @JsonProperty("createdBy")
    private Object createdBy;
    @JsonProperty("createdOn")
    private Object createdOn;
    @JsonProperty("applicableFrom")
    private Object applicableFrom;
    @JsonProperty("lastModifiedOn")
    private Long lastModifiedOn;
    @JsonProperty("categoryCode")
    private Object categoryCode;
    @JsonProperty("networkName")
    private Object networkName;
    @JsonProperty("shortCode")
    private String shortCode;
    @JsonProperty("status")
    private Object status;
    @JsonProperty("setVersion")
    private Object setVersion;
    @JsonProperty("commLastVersion")
    private Object commLastVersion;
    @JsonProperty("commProfileSetName")
    private String commProfileSetName;
    @JsonProperty("defaultProfile")
    private Object defaultProfile;
    @JsonProperty("gradeName")
    private Object gradeName;
    @JsonProperty("grphDomainName")
    private Object grphDomainName;
    @JsonProperty("commProfileVersion")
    private Object commProfileVersion;
    @JsonProperty("grphDomainCode")
    private Object grphDomainCode;
    @JsonProperty("additionalCommissionTimeSlab")
    private Object additionalCommissionTimeSlab;
    @JsonProperty("batch_ID")
    private Object batchID;
    @JsonProperty("gradeCode")
    private Object gradeCode;
    @JsonProperty("combinedKey")
    private String combinedKey;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dualCommissionType")
    public Object getDualCommissionType() {
        return dualCommissionType;
    }

    @JsonProperty("dualCommissionType")
    public void setDualCommissionType(Object dualCommissionType) {
        this.dualCommissionType = dualCommissionType;
    }

    @JsonProperty("networkCode")
    public Object getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(Object networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("categoryName")
    public Object getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(Object categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("gatewayCode")
    public Object getGatewayCode() {
        return gatewayCode;
    }

    @JsonProperty("gatewayCode")
    public void setGatewayCode(Object gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    @JsonProperty("language1Message")
    public Object getLanguage1Message() {
        return language1Message;
    }

    @JsonProperty("language1Message")
    public void setLanguage1Message(Object language1Message) {
        this.language1Message = language1Message;
    }

    @JsonProperty("language2Message")
    public Object getLanguage2Message() {
        return language2Message;
    }

    @JsonProperty("language2Message")
    public void setLanguage2Message(Object language2Message) {
        this.language2Message = language2Message;
    }

    @JsonProperty("commProfileSetId")
    public String getCommProfileSetId() {
        return commProfileSetId;
    }

    @JsonProperty("commProfileSetId")
    public void setCommProfileSetId(String commProfileSetId) {
        this.commProfileSetId = commProfileSetId;
    }

    @JsonProperty("modifiedBy")
    public Object getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(Object modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("modifiedOn")
    public Object getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(Object modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("createdBy")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("createdOn")
    public Object getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("applicableFrom")
    public Object getApplicableFrom() {
        return applicableFrom;
    }

    @JsonProperty("applicableFrom")
    public void setApplicableFrom(Object applicableFrom) {
        this.applicableFrom = applicableFrom;
    }

    @JsonProperty("lastModifiedOn")
    public Long getLastModifiedOn() {
        return lastModifiedOn;
    }

    @JsonProperty("lastModifiedOn")
    public void setLastModifiedOn(Long lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    @JsonProperty("categoryCode")
    public Object getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(Object categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("networkName")
    public Object getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(Object networkName) {
        this.networkName = networkName;
    }

    @JsonProperty("shortCode")
    public String getShortCode() {
        return shortCode;
    }

    @JsonProperty("shortCode")
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    @JsonProperty("status")
    public Object getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Object status) {
        this.status = status;
    }

    @JsonProperty("setVersion")
    public Object getSetVersion() {
        return setVersion;
    }

    @JsonProperty("setVersion")
    public void setSetVersion(Object setVersion) {
        this.setVersion = setVersion;
    }

    @JsonProperty("commLastVersion")
    public Object getCommLastVersion() {
        return commLastVersion;
    }

    @JsonProperty("commLastVersion")
    public void setCommLastVersion(Object commLastVersion) {
        this.commLastVersion = commLastVersion;
    }

    @JsonProperty("commProfileSetName")
    public String getCommProfileSetName() {
        return commProfileSetName;
    }

    @JsonProperty("commProfileSetName")
    public void setCommProfileSetName(String commProfileSetName) {
        this.commProfileSetName = commProfileSetName;
    }

    @JsonProperty("defaultProfile")
    public Object getDefaultProfile() {
        return defaultProfile;
    }

    @JsonProperty("defaultProfile")
    public void setDefaultProfile(Object defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    @JsonProperty("gradeName")
    public Object getGradeName() {
        return gradeName;
    }

    @JsonProperty("gradeName")
    public void setGradeName(Object gradeName) {
        this.gradeName = gradeName;
    }

    @JsonProperty("grphDomainName")
    public Object getGrphDomainName() {
        return grphDomainName;
    }

    @JsonProperty("grphDomainName")
    public void setGrphDomainName(Object grphDomainName) {
        this.grphDomainName = grphDomainName;
    }

    @JsonProperty("commProfileVersion")
    public Object getCommProfileVersion() {
        return commProfileVersion;
    }

    @JsonProperty("commProfileVersion")
    public void setCommProfileVersion(Object commProfileVersion) {
        this.commProfileVersion = commProfileVersion;
    }

    @JsonProperty("grphDomainCode")
    public Object getGrphDomainCode() {
        return grphDomainCode;
    }

    @JsonProperty("grphDomainCode")
    public void setGrphDomainCode(Object grphDomainCode) {
        this.grphDomainCode = grphDomainCode;
    }

    @JsonProperty("additionalCommissionTimeSlab")
    public Object getAdditionalCommissionTimeSlab() {
        return additionalCommissionTimeSlab;
    }

    @JsonProperty("additionalCommissionTimeSlab")
    public void setAdditionalCommissionTimeSlab(Object additionalCommissionTimeSlab) {
        this.additionalCommissionTimeSlab = additionalCommissionTimeSlab;
    }

    @JsonProperty("batch_ID")
    public Object getBatchID() {
        return batchID;
    }

    @JsonProperty("batch_ID")
    public void setBatchID(Object batchID) {
        this.batchID = batchID;
    }

    @JsonProperty("gradeCode")
    public Object getGradeCode() {
        return gradeCode;
    }

    @JsonProperty("gradeCode")
    public void setGradeCode(Object gradeCode) {
        this.gradeCode = gradeCode;
    }

    @JsonProperty("combinedKey")
    public String getCombinedKey() {
        return combinedKey;
    }

    @JsonProperty("combinedKey")
    public void setCombinedKey(String combinedKey) {
        this.combinedKey = combinedKey;
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
