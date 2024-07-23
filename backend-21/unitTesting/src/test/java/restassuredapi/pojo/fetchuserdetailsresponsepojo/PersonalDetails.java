
package restassuredapi.pojo.fetchuserdetailsresponsepojo;

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
    "addressLine1",
    "addressLine2",
    "appointmentDate",
    "categoryCode",
    "categoryCodeDesc",
    "categoryVO",
    "city",
    "company",
    "contactPerson",
    "country",
    "creationType",
    "creationTypeDesc",
    "documentNo",
    "documentType",
    "domainCode",
    "domainCodeDesc",
    "emailId",
    "externalCode",
    "fax",
    "firstName",
    "geography",
    "language",
    "lastName",
    "latitude",
    "longitude",
    "msisdn",
    "namePrefix",
    "ownerName",
    "parentCategory",
    "parentName",
    "postpaidBalance",
    "prepaidBalance",
    "shortName",
    "ssn",
    "state",
    "status",
    "statusDesc",
    "subscriberCode",
    "userBalanceList",
    "userId",
    "userLanguage",
    "userLanguageDesc",
    "userLanguageList",
    "userOtherBalance"
})
public class PersonalDetails {

    @JsonProperty("addressLine1")
    private String addressLine1;
    @JsonProperty("addressLine2")
    private String addressLine2;
    @JsonProperty("appointmentDate")
    private String appointmentDate;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("categoryCodeDesc")
    private String categoryCodeDesc;
    @JsonProperty("categoryVO")
    private CategoryVO categoryVO;
    @JsonProperty("city")
    private String city;
    @JsonProperty("company")
    private String company;
    @JsonProperty("contactPerson")
    private String contactPerson;
    @JsonProperty("country")
    private String country;
    @JsonProperty("creationType")
    private String creationType;
    @JsonProperty("creationTypeDesc")
    private String creationTypeDesc;
    @JsonProperty("documentNo")
    private String documentNo;
    @JsonProperty("documentType")
    private String documentType;
    @JsonProperty("domainCode")
    private String domainCode;
    @JsonProperty("domainCodeDesc")
    private String domainCodeDesc;
    @JsonProperty("emailId")
    private String emailId;
    @JsonProperty("externalCode")
    private String externalCode;
    @JsonProperty("fax")
    private String fax;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("language")
    private String language;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("namePrefix")
    private String namePrefix;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("parentCategory")
    private String parentCategory;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("postpaidBalance")
    private String postpaidBalance;
    @JsonProperty("prepaidBalance")
    private String prepaidBalance;
    @JsonProperty("shortName")
    private String shortName;
    @JsonProperty("ssn")
    private String ssn;
    @JsonProperty("state")
    private String state;
    @JsonProperty("status")
    private String status;
    @JsonProperty("statusDesc")
    private String statusDesc;
    @JsonProperty("subscriberCode")
    private String subscriberCode;
    @JsonProperty("userBalanceList")
    private List<UserBalanceList> userBalanceList = null;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("userLanguage")
    private String userLanguage;
    @JsonProperty("userLanguageDesc")
    private String userLanguageDesc;
    @JsonProperty("userLanguageList")
    private List<UserLanguageList> userLanguageList = null;
    @JsonProperty("userOtherBalance")
    private String userOtherBalance;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("addressLine1")
    public String getAddressLine1() {
        return addressLine1;
    }

    @JsonProperty("addressLine1")
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @JsonProperty("addressLine2")
    public String getAddressLine2() {
        return addressLine2;
    }

    @JsonProperty("addressLine2")
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @JsonProperty("appointmentDate")
    public String getAppointmentDate() {
        return appointmentDate;
    }

    @JsonProperty("appointmentDate")
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("categoryCodeDesc")
    public String getCategoryCodeDesc() {
        return categoryCodeDesc;
    }

    @JsonProperty("categoryCodeDesc")
    public void setCategoryCodeDesc(String categoryCodeDesc) {
        this.categoryCodeDesc = categoryCodeDesc;
    }

    @JsonProperty("categoryVO")
    public CategoryVO getCategoryVO() {
        return categoryVO;
    }

    @JsonProperty("categoryVO")
    public void setCategoryVO(CategoryVO categoryVO) {
        this.categoryVO = categoryVO;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("company")
    public String getCompany() {
        return company;
    }

    @JsonProperty("company")
    public void setCompany(String company) {
        this.company = company;
    }

    @JsonProperty("contactPerson")
    public String getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("creationType")
    public String getCreationType() {
        return creationType;
    }

    @JsonProperty("creationType")
    public void setCreationType(String creationType) {
        this.creationType = creationType;
    }

    @JsonProperty("creationTypeDesc")
    public String getCreationTypeDesc() {
        return creationTypeDesc;
    }

    @JsonProperty("creationTypeDesc")
    public void setCreationTypeDesc(String creationTypeDesc) {
        this.creationTypeDesc = creationTypeDesc;
    }

    @JsonProperty("documentNo")
    public String getDocumentNo() {
        return documentNo;
    }

    @JsonProperty("documentNo")
    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    @JsonProperty("documentType")
    public String getDocumentType() {
        return documentType;
    }

    @JsonProperty("documentType")
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @JsonProperty("domainCode")
    public String getDomainCode() {
        return domainCode;
    }

    @JsonProperty("domainCode")
    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    @JsonProperty("domainCodeDesc")
    public String getDomainCodeDesc() {
        return domainCodeDesc;
    }

    @JsonProperty("domainCodeDesc")
    public void setDomainCodeDesc(String domainCodeDesc) {
        this.domainCodeDesc = domainCodeDesc;
    }

    @JsonProperty("emailId")
    public String getEmailId() {
        return emailId;
    }

    @JsonProperty("emailId")
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @JsonProperty("externalCode")
    public String getExternalCode() {
        return externalCode;
    }

    @JsonProperty("externalCode")
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    @JsonProperty("fax")
    public String getFax() {
        return fax;
    }

    @JsonProperty("fax")
    public void setFax(String fax) {
        this.fax = fax;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("geography")
    public String getGeography() {
        return geography;
    }

    @JsonProperty("geography")
    public void setGeography(String geography) {
        this.geography = geography;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("namePrefix")
    public String getNamePrefix() {
        return namePrefix;
    }

    @JsonProperty("namePrefix")
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
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

    @JsonProperty("parentName")
    public String getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("postpaidBalance")
    public String getPostpaidBalance() {
        return postpaidBalance;
    }

    @JsonProperty("postpaidBalance")
    public void setPostpaidBalance(String postpaidBalance) {
        this.postpaidBalance = postpaidBalance;
    }

    @JsonProperty("prepaidBalance")
    public String getPrepaidBalance() {
        return prepaidBalance;
    }

    @JsonProperty("prepaidBalance")
    public void setPrepaidBalance(String prepaidBalance) {
        this.prepaidBalance = prepaidBalance;
    }

    @JsonProperty("shortName")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("ssn")
    public String getSsn() {
        return ssn;
    }

    @JsonProperty("ssn")
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("statusDesc")
    public String getStatusDesc() {
        return statusDesc;
    }

    @JsonProperty("statusDesc")
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @JsonProperty("subscriberCode")
    public String getSubscriberCode() {
        return subscriberCode;
    }

    @JsonProperty("subscriberCode")
    public void setSubscriberCode(String subscriberCode) {
        this.subscriberCode = subscriberCode;
    }

    @JsonProperty("userBalanceList")
    public List<UserBalanceList> getUserBalanceList() {
        return userBalanceList;
    }

    @JsonProperty("userBalanceList")
    public void setUserBalanceList(List<UserBalanceList> userBalanceList) {
        this.userBalanceList = userBalanceList;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("userLanguage")
    public String getUserLanguage() {
        return userLanguage;
    }

    @JsonProperty("userLanguage")
    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    @JsonProperty("userLanguageDesc")
    public String getUserLanguageDesc() {
        return userLanguageDesc;
    }

    @JsonProperty("userLanguageDesc")
    public void setUserLanguageDesc(String userLanguageDesc) {
        this.userLanguageDesc = userLanguageDesc;
    }

    @JsonProperty("userLanguageList")
    public List<UserLanguageList> getUserLanguageList() {
        return userLanguageList;
    }

    @JsonProperty("userLanguageList")
    public void setUserLanguageList(List<UserLanguageList> userLanguageList) {
        this.userLanguageList = userLanguageList;
    }

    @JsonProperty("userOtherBalance")
    public String getUserOtherBalance() {
        return userOtherBalance;
    }

    @JsonProperty("userOtherBalance")
    public void setUserOtherBalance(String userOtherBalance) {
        this.userOtherBalance = userOtherBalance;
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
