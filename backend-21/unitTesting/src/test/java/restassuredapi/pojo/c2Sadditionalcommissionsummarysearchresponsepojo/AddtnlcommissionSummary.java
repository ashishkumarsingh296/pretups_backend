
package restassuredapi.pojo.c2Sadditionalcommissionsummarysearchresponsepojo;

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
    "differentialCommission",
    "loginID",
    "ownerCategory",
    "ownerGeography",
    "ownerMobileNumber",
    "ownerName",
    "parentCategory",
    "parentGeography",
    "parentMobileNumber",
    "parentName",
    "service",
    "subService",
    "transactionCount",
    "transferDateOrMonth",
    "userCategory",
    "userGeography",
    "userMobileNumber",
    "userName"
})
@Generated("jsonschema2pojo")
public class AddtnlcommissionSummary {

    @JsonProperty("differentialCommission")
    private String differentialCommission;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("ownerCategory")
    private String ownerCategory;
    @JsonProperty("ownerGeography")
    private String ownerGeography;
    @JsonProperty("ownerMobileNumber")
    private String ownerMobileNumber;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("parentCategory")
    private String parentCategory;
    @JsonProperty("parentGeography")
    private String parentGeography;
    @JsonProperty("parentMobileNumber")
    private String parentMobileNumber;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("service")
    private String service;
    @JsonProperty("subService")
    private String subService;
    @JsonProperty("transactionCount")
    private String transactionCount;
    @JsonProperty("transferDateOrMonth")
    private String transferDateOrMonth;
    @JsonProperty("userCategory")
    private String userCategory;
    @JsonProperty("userGeography")
    private String userGeography;
    @JsonProperty("userMobileNumber")
    private String userMobileNumber;
    @JsonProperty("userName")
    private String userName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("differentialCommission")
    public String getDifferentialCommission() {
        return differentialCommission;
    }

    @JsonProperty("differentialCommission")
    public void setDifferentialCommission(String differentialCommission) {
        this.differentialCommission = differentialCommission;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("ownerCategory")
    public String getOwnerCategory() {
        return ownerCategory;
    }

    @JsonProperty("ownerCategory")
    public void setOwnerCategory(String ownerCategory) {
        this.ownerCategory = ownerCategory;
    }

    @JsonProperty("ownerGeography")
    public String getOwnerGeography() {
        return ownerGeography;
    }

    @JsonProperty("ownerGeography")
    public void setOwnerGeography(String ownerGeography) {
        this.ownerGeography = ownerGeography;
    }

    @JsonProperty("ownerMobileNumber")
    public String getOwnerMobileNumber() {
        return ownerMobileNumber;
    }

    @JsonProperty("ownerMobileNumber")
    public void setOwnerMobileNumber(String ownerMobileNumber) {
        this.ownerMobileNumber = ownerMobileNumber;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("parentCategory")
    public String getParentCategory() {
        return parentCategory;
    }

    @JsonProperty("parentCategory")
    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    @JsonProperty("parentGeography")
    public String getParentGeography() {
        return parentGeography;
    }

    @JsonProperty("parentGeography")
    public void setParentGeography(String parentGeography) {
        this.parentGeography = parentGeography;
    }

    @JsonProperty("parentMobileNumber")
    public String getParentMobileNumber() {
        return parentMobileNumber;
    }

    @JsonProperty("parentMobileNumber")
    public void setParentMobileNumber(String parentMobileNumber) {
        this.parentMobileNumber = parentMobileNumber;
    }

    @JsonProperty("parentName")
    public String getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("subService")
    public String getSubService() {
        return subService;
    }

    @JsonProperty("subService")
    public void setSubService(String subService) {
        this.subService = subService;
    }

    @JsonProperty("transactionCount")
    public String getTransactionCount() {
        return transactionCount;
    }

    @JsonProperty("transactionCount")
    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }

    @JsonProperty("transferDateOrMonth")
    public String getTransferDateOrMonth() {
        return transferDateOrMonth;
    }

    @JsonProperty("transferDateOrMonth")
    public void setTransferDateOrMonth(String transferDateOrMonth) {
        this.transferDateOrMonth = transferDateOrMonth;
    }

    @JsonProperty("userCategory")
    public String getUserCategory() {
        return userCategory;
    }

    @JsonProperty("userCategory")
    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    @JsonProperty("userGeography")
    public String getUserGeography() {
        return userGeography;
    }

    @JsonProperty("userGeography")
    public void setUserGeography(String userGeography) {
        this.userGeography = userGeography;
    }

    @JsonProperty("userMobileNumber")
    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    @JsonProperty("userMobileNumber")
    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
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
