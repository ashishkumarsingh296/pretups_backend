package restassuredapi.pojo.viewpassbookresponsepojo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"errorMap",
"fromDate",
"message",
"messageCode",
"passbook",
"referenceId",
"service",
"status",
"successList",
"toDate"
})
public class ViewPassBookResponsePojo {

@JsonProperty("errorMap")
private ErrorMap errorMap;
@JsonProperty("fromDate")
private String fromDate;
@JsonProperty("message")
private String message;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("referenceId")
private String referenceId;
@JsonProperty("service")
private String service;
@JsonProperty("status")
private String status;
@JsonProperty("successList")
private List<SuccessList> successList = null;
@JsonProperty("toDate")
private String toDate;
@JsonProperty("passbook")
LinkedHashMap<String, PassbookDetailsVO> passbook = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("errorMap")
public ErrorMap getErrorMap() {
return errorMap;
}

@JsonProperty("errorMap")
public void setErrorMap(ErrorMap errorMap) {
this.errorMap = errorMap;
}

@JsonProperty("fromDate")
public String getFromDate() {
return fromDate;
}

@JsonProperty("fromDate")
public void setFromDate(String fromDate) {
this.fromDate = fromDate;
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

@JsonProperty("referenceId")
public String getReferenceId() {
return referenceId;
}

@JsonProperty("referenceId")
public void setReferenceId(String referenceId) {
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
public List<SuccessList> getSuccessList() {
return successList;
}

@JsonProperty("successList")
public void setSuccessList(List<SuccessList> successList) {
this.successList = successList;
}

@JsonProperty("toDate")
public String getToDate() {
return toDate;
}

@JsonProperty("toDate")
public void setToDate(String toDate) {
this.toDate = toDate;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonProperty("passbook")
public LinkedHashMap<String, PassbookDetailsVO> getPassbook() {
	return passbook;
}

@JsonProperty("passbook")
public void setPassbook(LinkedHashMap<String, PassbookDetailsVO> passbook) {
	this.passbook = passbook;
}

public void setAdditionalProperties(Map<String, Object> additionalProperties) {
	this.additionalProperties = additionalProperties;
}

}