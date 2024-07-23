
package restassuredapi.pojo.cuchanneluserlistresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "balanceList",
    "category",
    "commissionProfile",
    "contactPerson",
    "domain",
    "geography",
    "grade",
    "lastModifiedBy",
    "lastModifiedDateTime",
    "lastTxnDatTime",
    "loginID",
    "msisdn",
    "ownerName",
    "parentName",
    "registeredDateTime",
    "status",
    "transactionProfile",
    "userName"
})
public class ChannelUsersList {

    @JsonProperty("balanceList")
    private List<BalanceList> balanceList = null;
    @JsonProperty("category")
    private String category;
    @JsonProperty("commissionProfile")
    private String commissionProfile;
    @JsonProperty("contactPerson")
    private String contactPerson;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("grade")
    private String grade;
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;
    @JsonProperty("lastModifiedDateTime")
    private String lastModifiedDateTime;
    @JsonProperty("lastTxnDatTime")
    private String lastTxnDatTime;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("registeredDateTime")
    private String registeredDateTime;
    @JsonProperty("status")
    private String status;
    @JsonProperty("transactionProfile")
    private String transactionProfile;
    @JsonProperty("userName")
    private String userName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("balanceList")
    public List<BalanceList> getBalanceList() {
        return balanceList;
    }

    @JsonProperty("balanceList")
    public void setBalanceList(List<BalanceList> balanceList) {
        this.balanceList = balanceList;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("commissionProfile")
    public String getCommissionProfile() {
        return commissionProfile;
    }

    @JsonProperty("commissionProfile")
    public void setCommissionProfile(String commissionProfile) {
        this.commissionProfile = commissionProfile;
    }

    @JsonProperty("contactPerson")
    public String getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonProperty("geography")
    public String getGeography() {
        return geography;
    }

    @JsonProperty("geography")
    public void setGeography(String geography) {
        this.geography = geography;
    }

    @JsonProperty("grade")
    public String getGrade() {
        return grade;
    }

    @JsonProperty("grade")
    public void setGrade(String grade) {
        this.grade = grade;
    }

    @JsonProperty("lastModifiedBy")
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @JsonProperty("lastModifiedBy")
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @JsonProperty("lastModifiedDateTime")
    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    @JsonProperty("lastModifiedDateTime")
    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    @JsonProperty("lastTxnDatTime")
    public String getLastTxnDatTime() {
        return lastTxnDatTime;
    }

    @JsonProperty("lastTxnDatTime")
    public void setLastTxnDatTime(String lastTxnDatTime) {
        this.lastTxnDatTime = lastTxnDatTime;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("parentName")
    public String getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("registeredDateTime")
    public String getRegisteredDateTime() {
        return registeredDateTime;
    }

    @JsonProperty("registeredDateTime")
    public void setRegisteredDateTime(String registeredDateTime) {
        this.registeredDateTime = registeredDateTime;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("transactionProfile")
    public String getTransactionProfile() {
        return transactionProfile;
    }

    @JsonProperty("transactionProfile")
    public void setTransactionProfile(String transactionProfile) {
        this.transactionProfile = transactionProfile;
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
