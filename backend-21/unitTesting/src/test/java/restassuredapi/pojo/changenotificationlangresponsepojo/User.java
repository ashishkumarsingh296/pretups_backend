
package restassuredapi.pojo.changenotificationlangresponsepojo;

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
public class User {

    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("userList")
    private Object userList;
    @JsonProperty("userListSize")
    private Integer userListSize;
    @JsonProperty("languageList")
    private Object languageList;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("messageCode")
    private Object messageCode;
    @JsonProperty("message")
    private Object message;
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
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("userList")
    public Object getUserList() {
        return userList;
    }

    @JsonProperty("userList")
    public void setUserList(Object userList) {
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
    public Object getLanguageList() {
        return languageList;
    }

    @JsonProperty("languageList")
    public void setLanguageList(Object languageList) {
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
    public Object getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(Object messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public Object getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Object message) {
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
