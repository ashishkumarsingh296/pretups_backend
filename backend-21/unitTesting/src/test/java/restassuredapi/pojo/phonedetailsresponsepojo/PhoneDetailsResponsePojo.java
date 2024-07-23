
package restassuredapi.pojo.phonedetailsresponsepojo;

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
    "msisdn",
    "loginID",
    "userName",
    "userID",
    "userList",
    "userListSize",
    "languageList",
    "status",
    "messageCode",
    "message",
    "errorMap"
})
@Generated("jsonschema2pojo")
public class PhoneDetailsResponsePojo {

    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("userName")
    private Object userName;
    @JsonProperty("userID")
    private Object userID;
    @JsonProperty("userList")
    private List<User> userList = null;
    @JsonProperty("userListSize")
    private Integer userListSize;
    @JsonProperty("languageList")
    private List<Language> languageList = null;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorMap")
    private Object errorMap;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("userName")
    public Object getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(Object userName) {
        this.userName = userName;
    }

    @JsonProperty("userID")
    public Object getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(Object userID) {
        this.userID = userID;
    }

    @JsonProperty("userList")
    public List<User> getUserList() {
        return userList;
    }

    @JsonProperty("userList")
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @JsonProperty("userListSize")
    public Integer getUserListSize() {
        return userListSize;
    }

    @JsonProperty("userListSize")
    public void setUserListSize(Integer userListSize) {
        this.userListSize = userListSize;
    }

    @JsonProperty("languageList")
    public List<Language> getLanguageList() {
        return languageList;
    }

    @JsonProperty("languageList")
    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
