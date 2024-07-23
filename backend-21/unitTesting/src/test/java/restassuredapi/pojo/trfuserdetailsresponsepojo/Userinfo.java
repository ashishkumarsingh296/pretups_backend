
package restassuredapi.pojo.trfuserdetailsresponsepojo;

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
    "senderUserName",
    "receiverUserName",
    "senderMsisdn",
    "receiverMsisdn",
    "senderCategoryID",
    "receiverCategoryID",
    "senderCategoryName",
    "receiverCategoryName",
    "senderCommissionProfileID",
    "receiverCommissionProfileID",
    "senderCommissionProfileName",
    "receiverCommissionProfileName",
    "senderCommissionProfileSetVersion",
    "receiverCommissionProfileSetVersion",
    "senderUserGradeCode",
    "receiverUserGradeCode",
    "senderUserGradeName",
    "receiverUserGradeName",
    "senderTransferProfileID",
    "receiverTransferProfileID",
    "senderTransferProfileName",
    "receiverTransferProfileName",
    "geographyCode",
    "geographyName",
    "domainCode",
    "domainName",
    "userproductdetails"
})
public class Userinfo {

    @JsonProperty("senderUserName")
    private String senderUserName;
    @JsonProperty("receiverUserName")
    private String receiverUserName;
    @JsonProperty("senderMsisdn")
    private String senderMsisdn;
    @JsonProperty("receiverMsisdn")
    private String receiverMsisdn;
    @JsonProperty("senderCategoryID")
    private String senderCategoryID;
    @JsonProperty("receiverCategoryID")
    private String receiverCategoryID;
    @JsonProperty("senderCategoryName")
    private String senderCategoryName;
    @JsonProperty("receiverCategoryName")
    private String receiverCategoryName;
    @JsonProperty("senderCommissionProfileID")
    private String senderCommissionProfileID;
    @JsonProperty("receiverCommissionProfileID")
    private String receiverCommissionProfileID;
    @JsonProperty("senderCommissionProfileName")
    private String senderCommissionProfileName;
    @JsonProperty("receiverCommissionProfileName")
    private String receiverCommissionProfileName;
    @JsonProperty("senderCommissionProfileSetVersion")
    private String senderCommissionProfileSetVersion;
    @JsonProperty("receiverCommissionProfileSetVersion")
    private String receiverCommissionProfileSetVersion;
    @JsonProperty("senderUserGradeCode")
    private String senderUserGradeCode;
    @JsonProperty("receiverUserGradeCode")
    private String receiverUserGradeCode;
    @JsonProperty("senderUserGradeName")
    private Object senderUserGradeName;
    @JsonProperty("receiverUserGradeName")
    private String receiverUserGradeName;
    @JsonProperty("senderTransferProfileID")
    private String senderTransferProfileID;
    @JsonProperty("receiverTransferProfileID")
    private String receiverTransferProfileID;
    @JsonProperty("senderTransferProfileName")
    private String senderTransferProfileName;
    @JsonProperty("receiverTransferProfileName")
    private String receiverTransferProfileName;
    @JsonProperty("geographyCode")
    private String geographyCode;
    @JsonProperty("geographyName")
    private String geographyName;
    @JsonProperty("domainCode")
    private String domainCode;
    @JsonProperty("domainName")
    private String domainName;
    @JsonProperty("userproductdetails")
    private List<Userproductdetail> userproductdetails = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("senderUserName")
    public String getSenderUserName() {
        return senderUserName;
    }

    @JsonProperty("senderUserName")
    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    @JsonProperty("receiverUserName")
    public String getReceiverUserName() {
        return receiverUserName;
    }

    @JsonProperty("receiverUserName")
    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    @JsonProperty("senderMsisdn")
    public String getSenderMsisdn() {
        return senderMsisdn;
    }

    @JsonProperty("senderMsisdn")
    public void setSenderMsisdn(String senderMsisdn) {
        this.senderMsisdn = senderMsisdn;
    }

    @JsonProperty("receiverMsisdn")
    public String getReceiverMsisdn() {
        return receiverMsisdn;
    }

    @JsonProperty("receiverMsisdn")
    public void setReceiverMsisdn(String receiverMsisdn) {
        this.receiverMsisdn = receiverMsisdn;
    }

    @JsonProperty("senderCategoryID")
    public String getSenderCategoryID() {
        return senderCategoryID;
    }

    @JsonProperty("senderCategoryID")
    public void setSenderCategoryID(String senderCategoryID) {
        this.senderCategoryID = senderCategoryID;
    }

    @JsonProperty("receiverCategoryID")
    public String getReceiverCategoryID() {
        return receiverCategoryID;
    }

    @JsonProperty("receiverCategoryID")
    public void setReceiverCategoryID(String receiverCategoryID) {
        this.receiverCategoryID = receiverCategoryID;
    }

    @JsonProperty("senderCategoryName")
    public String getSenderCategoryName() {
        return senderCategoryName;
    }

    @JsonProperty("senderCategoryName")
    public void setSenderCategoryName(String senderCategoryName) {
        this.senderCategoryName = senderCategoryName;
    }

    @JsonProperty("receiverCategoryName")
    public String getReceiverCategoryName() {
        return receiverCategoryName;
    }

    @JsonProperty("receiverCategoryName")
    public void setReceiverCategoryName(String receiverCategoryName) {
        this.receiverCategoryName = receiverCategoryName;
    }

    @JsonProperty("senderCommissionProfileID")
    public String getSenderCommissionProfileID() {
        return senderCommissionProfileID;
    }

    @JsonProperty("senderCommissionProfileID")
    public void setSenderCommissionProfileID(String senderCommissionProfileID) {
        this.senderCommissionProfileID = senderCommissionProfileID;
    }

    @JsonProperty("receiverCommissionProfileID")
    public String getReceiverCommissionProfileID() {
        return receiverCommissionProfileID;
    }

    @JsonProperty("receiverCommissionProfileID")
    public void setReceiverCommissionProfileID(String receiverCommissionProfileID) {
        this.receiverCommissionProfileID = receiverCommissionProfileID;
    }

    @JsonProperty("senderCommissionProfileName")
    public String getSenderCommissionProfileName() {
        return senderCommissionProfileName;
    }

    @JsonProperty("senderCommissionProfileName")
    public void setSenderCommissionProfileName(String senderCommissionProfileName) {
        this.senderCommissionProfileName = senderCommissionProfileName;
    }

    @JsonProperty("receiverCommissionProfileName")
    public String getReceiverCommissionProfileName() {
        return receiverCommissionProfileName;
    }

    @JsonProperty("receiverCommissionProfileName")
    public void setReceiverCommissionProfileName(String receiverCommissionProfileName) {
        this.receiverCommissionProfileName = receiverCommissionProfileName;
    }

    @JsonProperty("senderCommissionProfileSetVersion")
    public String getSenderCommissionProfileSetVersion() {
        return senderCommissionProfileSetVersion;
    }

    @JsonProperty("senderCommissionProfileSetVersion")
    public void setSenderCommissionProfileSetVersion(String senderCommissionProfileSetVersion) {
        this.senderCommissionProfileSetVersion = senderCommissionProfileSetVersion;
    }

    @JsonProperty("receiverCommissionProfileSetVersion")
    public String getReceiverCommissionProfileSetVersion() {
        return receiverCommissionProfileSetVersion;
    }

    @JsonProperty("receiverCommissionProfileSetVersion")
    public void setReceiverCommissionProfileSetVersion(String receiverCommissionProfileSetVersion) {
        this.receiverCommissionProfileSetVersion = receiverCommissionProfileSetVersion;
    }

    @JsonProperty("senderUserGradeCode")
    public String getSenderUserGradeCode() {
        return senderUserGradeCode;
    }

    @JsonProperty("senderUserGradeCode")
    public void setSenderUserGradeCode(String senderUserGradeCode) {
        this.senderUserGradeCode = senderUserGradeCode;
    }

    @JsonProperty("receiverUserGradeCode")
    public String getReceiverUserGradeCode() {
        return receiverUserGradeCode;
    }

    @JsonProperty("receiverUserGradeCode")
    public void setReceiverUserGradeCode(String receiverUserGradeCode) {
        this.receiverUserGradeCode = receiverUserGradeCode;
    }

    @JsonProperty("senderUserGradeName")
    public Object getSenderUserGradeName() {
        return senderUserGradeName;
    }

    @JsonProperty("senderUserGradeName")
    public void setSenderUserGradeName(Object senderUserGradeName) {
        this.senderUserGradeName = senderUserGradeName;
    }

    @JsonProperty("receiverUserGradeName")
    public String getReceiverUserGradeName() {
        return receiverUserGradeName;
    }

    @JsonProperty("receiverUserGradeName")
    public void setReceiverUserGradeName(String receiverUserGradeName) {
        this.receiverUserGradeName = receiverUserGradeName;
    }

    @JsonProperty("senderTransferProfileID")
    public String getSenderTransferProfileID() {
        return senderTransferProfileID;
    }

    @JsonProperty("senderTransferProfileID")
    public void setSenderTransferProfileID(String senderTransferProfileID) {
        this.senderTransferProfileID = senderTransferProfileID;
    }

    @JsonProperty("receiverTransferProfileID")
    public String getReceiverTransferProfileID() {
        return receiverTransferProfileID;
    }

    @JsonProperty("receiverTransferProfileID")
    public void setReceiverTransferProfileID(String receiverTransferProfileID) {
        this.receiverTransferProfileID = receiverTransferProfileID;
    }

    @JsonProperty("senderTransferProfileName")
    public String getSenderTransferProfileName() {
        return senderTransferProfileName;
    }

    @JsonProperty("senderTransferProfileName")
    public void setSenderTransferProfileName(String senderTransferProfileName) {
        this.senderTransferProfileName = senderTransferProfileName;
    }

    @JsonProperty("receiverTransferProfileName")
    public String getReceiverTransferProfileName() {
        return receiverTransferProfileName;
    }

    @JsonProperty("receiverTransferProfileName")
    public void setReceiverTransferProfileName(String receiverTransferProfileName) {
        this.receiverTransferProfileName = receiverTransferProfileName;
    }

    @JsonProperty("geographyCode")
    public String getGeographyCode() {
        return geographyCode;
    }

    @JsonProperty("geographyCode")
    public void setGeographyCode(String geographyCode) {
        this.geographyCode = geographyCode;
    }

    @JsonProperty("geographyName")
    public String getGeographyName() {
        return geographyName;
    }

    @JsonProperty("geographyName")
    public void setGeographyName(String geographyName) {
        this.geographyName = geographyName;
    }

    @JsonProperty("domainCode")
    public String getDomainCode() {
        return domainCode;
    }

    @JsonProperty("domainCode")
    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    @JsonProperty("domainName")
    public String getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("userproductdetails")
    public List<Userproductdetail> getUserproductdetails() {
        return userproductdetails;
    }

    @JsonProperty("userproductdetails")
    public void setUserproductdetails(List<Userproductdetail> userproductdetails) {
        this.userproductdetails = userproductdetails;
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
