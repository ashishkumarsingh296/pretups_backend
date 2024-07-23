
package restassuredapi.pojo.parentandownerprofileinforesponsepojo;

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
    "address",
    "emailID",
    "erpCode",
    "errorMap",
    "grade",
    "message",
    "messageCode",
    "msisdn",
    "ownerCategoryName",
    "ownerMobileNumber",
    "ownerName",
    "parentCategoryName",
    "parentMobileNumber",
    "parentName",
    "parentUserID",
    "referenceId",
    "service",
    "status",
    "successList",
    "userName"
})
@Generated("jsonschema2pojo")
public class ParentandOwnerProfileInfoResponsePojo {

    @JsonProperty("address")
    private String address;
    @JsonProperty("emailID")
    private String emailID;
    @JsonProperty("erpCode")
    private String erpCode;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("grade")
    private String grade;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("ownerCategoryName")
    private String ownerCategoryName;
    @JsonProperty("ownerMobileNumber")
    private String ownerMobileNumber;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("parentCategoryName")
    private String parentCategoryName;
    @JsonProperty("parentMobileNumber")
    private String parentMobileNumber;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("parentUserID")
    private String parentUserID;
    @JsonProperty("referenceId")
    private Integer referenceId;
    @JsonProperty("service")
    private String service;
    @JsonProperty("status")
    private String status;
    @JsonProperty("successList")
    private List<Success> successList = null;
    @JsonProperty("userName")
    private String userName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("emailID")
    public String getEmailID() {
        return emailID;
    }

    @JsonProperty("emailID")
    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    @JsonProperty("erpCode")
    public String getErpCode() {
        return erpCode;
    }

    @JsonProperty("erpCode")
    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    @JsonProperty("errorMap")
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("grade")
    public String getGrade() {
        return grade;
    }

    @JsonProperty("grade")
    public void setGrade(String grade) {
        this.grade = grade;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("ownerCategoryName")
    public String getOwnerCategoryName() {
        return ownerCategoryName;
    }

    @JsonProperty("ownerCategoryName")
    public void setOwnerCategoryName(String ownerCategoryName) {
        this.ownerCategoryName = ownerCategoryName;
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

    @JsonProperty("parentCategoryName")
    public String getParentCategoryName() {
        return parentCategoryName;
    }

    @JsonProperty("parentCategoryName")
    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
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

    @JsonProperty("parentUserID")
    public String getParentUserID() {
        return parentUserID;
    }

    @JsonProperty("parentUserID")
    public void setParentUserID(String parentUserID) {
        this.parentUserID = parentUserID;
    }

    @JsonProperty("referenceId")
    public Integer getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("successList")
    public List<Success> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<Success> successList) {
        this.successList = successList;
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
