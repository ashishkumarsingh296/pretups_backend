
package restassuredapi.pojo.fetchuserdetailsresponsepojo;

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
    "barredUserDetails",
    "groupedUserRoles",
    "loginDetails",
    "message",
    "messageCode",
    "paymentAndServiceDetails",
    "personalDetails",
    "profileDetails",
    "service",
    "status",
    "transactionId"
})
public class FetchUserDetailsResponsePojo {

    @JsonProperty("barredUserDetails")
    private BarredUserDetails barredUserDetails;
    @JsonProperty("groupedUserRoles")
    private GroupedUserRoles groupedUserRoles;
    @JsonProperty("loginDetails")
    private LoginDetails loginDetails;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("paymentAndServiceDetails")
    private PaymentAndServiceDetails paymentAndServiceDetails;
    @JsonProperty("personalDetails")
    private PersonalDetails personalDetails;
    @JsonProperty("profileDetails")
    private ProfileDetails profileDetails;
    @JsonProperty("service")
    private String service;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("barredUserDetails")
    public BarredUserDetails getBarredUserDetails() {
        return barredUserDetails;
    }

    @JsonProperty("barredUserDetails")
    public void setBarredUserDetails(BarredUserDetails barredUserDetails) {
        this.barredUserDetails = barredUserDetails;
    }

    @JsonProperty("groupedUserRoles")
    public GroupedUserRoles getGroupedUserRoles() {
        return groupedUserRoles;
    }

    @JsonProperty("groupedUserRoles")
    public void setGroupedUserRoles(GroupedUserRoles groupedUserRoles) {
        this.groupedUserRoles = groupedUserRoles;
    }

    @JsonProperty("loginDetails")
    public LoginDetails getLoginDetails() {
        return loginDetails;
    }

    @JsonProperty("loginDetails")
    public void setLoginDetails(LoginDetails loginDetails) {
        this.loginDetails = loginDetails;
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

    @JsonProperty("paymentAndServiceDetails")
    public PaymentAndServiceDetails getPaymentAndServiceDetails() {
        return paymentAndServiceDetails;
    }

    @JsonProperty("paymentAndServiceDetails")
    public void setPaymentAndServiceDetails(PaymentAndServiceDetails paymentAndServiceDetails) {
        this.paymentAndServiceDetails = paymentAndServiceDetails;
    }

    @JsonProperty("personalDetails")
    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    @JsonProperty("personalDetails")
    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    @JsonProperty("profileDetails")
    public ProfileDetails getProfileDetails() {
        return profileDetails;
    }

    @JsonProperty("profileDetails")
    public void setProfileDetails(ProfileDetails profileDetails) {
        this.profileDetails = profileDetails;
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
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
