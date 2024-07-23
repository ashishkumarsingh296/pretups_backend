
package restassuredapi.pojo.getuserinfoapiresponsepojo;

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
    "service",
    "referenceId",
    "status",
    "messageCode",
    "message",
    "errorMap",
    "successList",
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
    "senderDualCommission",
    "receiverDualCommission",
    "userProductDetails",
    "userBalanceDetails"
})
public class GetUserInfoApiResponsePojo {

    @JsonProperty("service")
    private Object service;
    @JsonProperty("referenceId")
    private Object referenceId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorMap")
    private Object errorMap;
    @JsonProperty("successList")
    private List<Object> successList = null;
    @JsonProperty("senderUserName")
    private Object senderUserName;
    @JsonProperty("receiverUserName")
    private Object receiverUserName;
    @JsonProperty("senderMsisdn")
    private Object senderMsisdn;
    @JsonProperty("receiverMsisdn")
    private Object receiverMsisdn;
    @JsonProperty("senderCategoryID")
    private Object senderCategoryID;
    @JsonProperty("receiverCategoryID")
    private Object receiverCategoryID;
    @JsonProperty("senderCategoryName")
    private Object senderCategoryName;
    @JsonProperty("receiverCategoryName")
    private Object receiverCategoryName;
    @JsonProperty("senderCommissionProfileID")
    private Object senderCommissionProfileID;
    @JsonProperty("receiverCommissionProfileID")
    private Object receiverCommissionProfileID;
    @JsonProperty("senderCommissionProfileName")
    private Object senderCommissionProfileName;
    @JsonProperty("receiverCommissionProfileName")
    private Object receiverCommissionProfileName;
    @JsonProperty("senderCommissionProfileSetVersion")
    private Object senderCommissionProfileSetVersion;
    @JsonProperty("receiverCommissionProfileSetVersion")
    private Object receiverCommissionProfileSetVersion;
    @JsonProperty("senderUserGradeCode")
    private Object senderUserGradeCode;
    @JsonProperty("receiverUserGradeCode")
    private Object receiverUserGradeCode;
    @JsonProperty("senderUserGradeName")
    private Object senderUserGradeName;
    @JsonProperty("receiverUserGradeName")
    private Object receiverUserGradeName;
    @JsonProperty("senderTransferProfileID")
    private Object senderTransferProfileID;
    @JsonProperty("receiverTransferProfileID")
    private Object receiverTransferProfileID;
    @JsonProperty("senderTransferProfileName")
    private Object senderTransferProfileName;
    @JsonProperty("receiverTransferProfileName")
    private Object receiverTransferProfileName;
    @JsonProperty("geographyCode")
    private Object geographyCode;
    @JsonProperty("geographyName")
    private Object geographyName;
    @JsonProperty("domainCode")
    private Object domainCode;
    @JsonProperty("domainName")
    private Object domainName;
    @JsonProperty("senderDualCommission")
    private Object senderDualCommission;
    @JsonProperty("receiverDualCommission")
    private Object receiverDualCommission;
    @JsonProperty("userProductDetails")
    private Object userProductDetails;
    @JsonProperty("userBalanceDetails")
    private Object userBalanceDetails;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("service")
    public Object getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(Object service) {
        this.service = service;
    }

    @JsonProperty("referenceId")
    public Object getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("errorMap")
    public Object getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(Object errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("successList")
    public List<Object> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<Object> successList) {
        this.successList = successList;
    }

    @JsonProperty("senderUserName")
    public Object getSenderUserName() {
        return senderUserName;
    }

    @JsonProperty("senderUserName")
    public void setSenderUserName(Object senderUserName) {
        this.senderUserName = senderUserName;
    }

    @JsonProperty("receiverUserName")
    public Object getReceiverUserName() {
        return receiverUserName;
    }

    @JsonProperty("receiverUserName")
    public void setReceiverUserName(Object receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    @JsonProperty("senderMsisdn")
    public Object getSenderMsisdn() {
        return senderMsisdn;
    }

    @JsonProperty("senderMsisdn")
    public void setSenderMsisdn(Object senderMsisdn) {
        this.senderMsisdn = senderMsisdn;
    }

    @JsonProperty("receiverMsisdn")
    public Object getReceiverMsisdn() {
        return receiverMsisdn;
    }

    @JsonProperty("receiverMsisdn")
    public void setReceiverMsisdn(Object receiverMsisdn) {
        this.receiverMsisdn = receiverMsisdn;
    }

    @JsonProperty("senderCategoryID")
    public Object getSenderCategoryID() {
        return senderCategoryID;
    }

    @JsonProperty("senderCategoryID")
    public void setSenderCategoryID(Object senderCategoryID) {
        this.senderCategoryID = senderCategoryID;
    }

    @JsonProperty("receiverCategoryID")
    public Object getReceiverCategoryID() {
        return receiverCategoryID;
    }

    @JsonProperty("receiverCategoryID")
    public void setReceiverCategoryID(Object receiverCategoryID) {
        this.receiverCategoryID = receiverCategoryID;
    }

    @JsonProperty("senderCategoryName")
    public Object getSenderCategoryName() {
        return senderCategoryName;
    }

    @JsonProperty("senderCategoryName")
    public void setSenderCategoryName(Object senderCategoryName) {
        this.senderCategoryName = senderCategoryName;
    }

    @JsonProperty("receiverCategoryName")
    public Object getReceiverCategoryName() {
        return receiverCategoryName;
    }

    @JsonProperty("receiverCategoryName")
    public void setReceiverCategoryName(Object receiverCategoryName) {
        this.receiverCategoryName = receiverCategoryName;
    }

    @JsonProperty("senderCommissionProfileID")
    public Object getSenderCommissionProfileID() {
        return senderCommissionProfileID;
    }

    @JsonProperty("senderCommissionProfileID")
    public void setSenderCommissionProfileID(Object senderCommissionProfileID) {
        this.senderCommissionProfileID = senderCommissionProfileID;
    }

    @JsonProperty("receiverCommissionProfileID")
    public Object getReceiverCommissionProfileID() {
        return receiverCommissionProfileID;
    }

    @JsonProperty("receiverCommissionProfileID")
    public void setReceiverCommissionProfileID(Object receiverCommissionProfileID) {
        this.receiverCommissionProfileID = receiverCommissionProfileID;
    }

    @JsonProperty("senderCommissionProfileName")
    public Object getSenderCommissionProfileName() {
        return senderCommissionProfileName;
    }

    @JsonProperty("senderCommissionProfileName")
    public void setSenderCommissionProfileName(Object senderCommissionProfileName) {
        this.senderCommissionProfileName = senderCommissionProfileName;
    }

    @JsonProperty("receiverCommissionProfileName")
    public Object getReceiverCommissionProfileName() {
        return receiverCommissionProfileName;
    }

    @JsonProperty("receiverCommissionProfileName")
    public void setReceiverCommissionProfileName(Object receiverCommissionProfileName) {
        this.receiverCommissionProfileName = receiverCommissionProfileName;
    }

    @JsonProperty("senderCommissionProfileSetVersion")
    public Object getSenderCommissionProfileSetVersion() {
        return senderCommissionProfileSetVersion;
    }

    @JsonProperty("senderCommissionProfileSetVersion")
    public void setSenderCommissionProfileSetVersion(Object senderCommissionProfileSetVersion) {
        this.senderCommissionProfileSetVersion = senderCommissionProfileSetVersion;
    }

    @JsonProperty("receiverCommissionProfileSetVersion")
    public Object getReceiverCommissionProfileSetVersion() {
        return receiverCommissionProfileSetVersion;
    }

    @JsonProperty("receiverCommissionProfileSetVersion")
    public void setReceiverCommissionProfileSetVersion(Object receiverCommissionProfileSetVersion) {
        this.receiverCommissionProfileSetVersion = receiverCommissionProfileSetVersion;
    }

    @JsonProperty("senderUserGradeCode")
    public Object getSenderUserGradeCode() {
        return senderUserGradeCode;
    }

    @JsonProperty("senderUserGradeCode")
    public void setSenderUserGradeCode(Object senderUserGradeCode) {
        this.senderUserGradeCode = senderUserGradeCode;
    }

    @JsonProperty("receiverUserGradeCode")
    public Object getReceiverUserGradeCode() {
        return receiverUserGradeCode;
    }

    @JsonProperty("receiverUserGradeCode")
    public void setReceiverUserGradeCode(Object receiverUserGradeCode) {
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
    public Object getReceiverUserGradeName() {
        return receiverUserGradeName;
    }

    @JsonProperty("receiverUserGradeName")
    public void setReceiverUserGradeName(Object receiverUserGradeName) {
        this.receiverUserGradeName = receiverUserGradeName;
    }

    @JsonProperty("senderTransferProfileID")
    public Object getSenderTransferProfileID() {
        return senderTransferProfileID;
    }

    @JsonProperty("senderTransferProfileID")
    public void setSenderTransferProfileID(Object senderTransferProfileID) {
        this.senderTransferProfileID = senderTransferProfileID;
    }

    @JsonProperty("receiverTransferProfileID")
    public Object getReceiverTransferProfileID() {
        return receiverTransferProfileID;
    }

    @JsonProperty("receiverTransferProfileID")
    public void setReceiverTransferProfileID(Object receiverTransferProfileID) {
        this.receiverTransferProfileID = receiverTransferProfileID;
    }

    @JsonProperty("senderTransferProfileName")
    public Object getSenderTransferProfileName() {
        return senderTransferProfileName;
    }

    @JsonProperty("senderTransferProfileName")
    public void setSenderTransferProfileName(Object senderTransferProfileName) {
        this.senderTransferProfileName = senderTransferProfileName;
    }

    @JsonProperty("receiverTransferProfileName")
    public Object getReceiverTransferProfileName() {
        return receiverTransferProfileName;
    }

    @JsonProperty("receiverTransferProfileName")
    public void setReceiverTransferProfileName(Object receiverTransferProfileName) {
        this.receiverTransferProfileName = receiverTransferProfileName;
    }

    @JsonProperty("geographyCode")
    public Object getGeographyCode() {
        return geographyCode;
    }

    @JsonProperty("geographyCode")
    public void setGeographyCode(Object geographyCode) {
        this.geographyCode = geographyCode;
    }

    @JsonProperty("geographyName")
    public Object getGeographyName() {
        return geographyName;
    }

    @JsonProperty("geographyName")
    public void setGeographyName(Object geographyName) {
        this.geographyName = geographyName;
    }

    @JsonProperty("domainCode")
    public Object getDomainCode() {
        return domainCode;
    }

    @JsonProperty("domainCode")
    public void setDomainCode(Object domainCode) {
        this.domainCode = domainCode;
    }

    @JsonProperty("domainName")
    public Object getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(Object domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("senderDualCommission")
    public Object getSenderDualCommission() {
        return senderDualCommission;
    }

    @JsonProperty("senderDualCommission")
    public void setSenderDualCommission(Object senderDualCommission) {
        this.senderDualCommission = senderDualCommission;
    }

    @JsonProperty("receiverDualCommission")
    public Object getReceiverDualCommission() {
        return receiverDualCommission;
    }

    @JsonProperty("receiverDualCommission")
    public void setReceiverDualCommission(Object receiverDualCommission) {
        this.receiverDualCommission = receiverDualCommission;
    }

    @JsonProperty("userProductDetails")
    public Object getUserProductDetails() {
        return userProductDetails;
    }

    @JsonProperty("userProductDetails")
    public void setUserProductDetails(Object userProductDetails) {
        this.userProductDetails = userProductDetails;
    }

    @JsonProperty("userBalanceDetails")
    public Object getUserBalanceDetails() {
        return userBalanceDetails;
    }

    @JsonProperty("userBalanceDetails")
    public void setUserBalanceDetails(Object userBalanceDetails) {
        this.userBalanceDetails = userBalanceDetails;
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
