package restassuredapi.pojo.userVerificationResponsePojo;


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
"listLoginIdNew",
"status",
"messageCode",
"message",
"errorMap",
"loginIdExist"
})
@Generated("jsonschema2pojo")
public class userVerificationResponsePojo {

@JsonProperty("listLoginIdNew")
private List<String> listLoginIdNew;
@JsonProperty("status")
private long status;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("message")
private String message;
@JsonProperty("errorMap")
private Object errorMap;
@JsonProperty("loginIdExist")
private Boolean loginIdExist;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("listLoginIdNew")
public List<String> getListLoginIdNew() {
return listLoginIdNew;
}

@JsonProperty("listLoginIdNew")
public void setListLoginIdNew(List<String> listLoginIdNew) {
this.listLoginIdNew = listLoginIdNew;
}

@JsonProperty("status")
public long getStatus() {
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

@JsonProperty("loginIdExist")
public Boolean getLoginIdExist() {
return loginIdExist;
}

@JsonProperty("loginIdExist")
public void setLoginIdExist(Boolean loginIdExist) {
this.loginIdExist = loginIdExist;
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