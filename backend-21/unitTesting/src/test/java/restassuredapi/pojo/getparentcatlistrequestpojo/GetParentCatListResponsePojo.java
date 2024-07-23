package restassuredapi.pojo.getparentcatlistrequestpojo;

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
"parentCategoryList",
"notApplicable",
"status",
"messageCode",
"message",
"errorMap"
})
@Generated("jsonschema2pojo")
public class GetParentCatListResponsePojo {

@JsonProperty("parentCategoryList")
private List<ParentCategory> parentCategoryList = null;
@JsonProperty("notApplicable")
private Boolean notApplicable;
@JsonProperty("status")
private String status;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("message")
private String message;
@JsonProperty("errorMap")
private Object errorMap;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("parentCategoryList")
public List<ParentCategory> getParentCategoryList() {
return parentCategoryList;
}

@JsonProperty("parentCategoryList")
public void setParentCategoryList(List<ParentCategory> parentCategoryList) {
this.parentCategoryList = parentCategoryList;
}

@JsonProperty("notApplicable")
public Boolean getNotApplicable() {
return notApplicable;
}

@JsonProperty("notApplicable")
public void setNotApplicable(Boolean notApplicable) {
this.notApplicable = notApplicable;
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

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}


