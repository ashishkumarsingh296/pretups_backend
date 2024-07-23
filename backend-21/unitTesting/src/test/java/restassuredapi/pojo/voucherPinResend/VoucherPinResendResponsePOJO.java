package restassuredapi.pojo.voucherPinResend;

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
"transList",
"status",
"messageCode",
"message",
"errorMap"
})
@Generated("jsonschema2pojo")
public class VoucherPinResendResponsePOJO {

@JsonProperty("transList")
private Object transList;
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

@JsonProperty("transList")
public Object getTransList() {
return transList;
}

@JsonProperty("transList")
public void setTransList(Object transList) {
this.transList = transList;
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