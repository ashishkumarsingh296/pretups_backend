package restassuredapi.pojo.barunbarchanneluserresponsepojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Generated;

import restassuredapi.pojo.o2CBatchStockTransferResponsepojo.ErrorMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"barredList",
"service",
"status",
"messageCode",
"message",
"successList",
"errormap"
})
@Generated("jsonschema2pojo")
public class BarUnbarChannelUserResponsePojo {

//@JsonProperty("barredList")
//private HashMap<String,ArrayList<BarredUserVo>> barredList;
@JsonProperty("service")
private String service;
@JsonProperty("status")
private String status;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("message")
private String message;
@JsonProperty("errorMap")
private ErrorMap errorMap;
@JsonProperty("successList")
private List<Object> successList = null;
@JsonProperty("referenceId")
private String referenceId;
@JsonProperty("referenceId")
public String getReferenceId() {
	return referenceId;
}
@JsonProperty("referenceId")
public void setReferenceId(String referenceId) {
	this.referenceId = referenceId;
}

//@JsonProperty("barredList")
//public HashMap<String,ArrayList<BarredUserVo>> getBarredList() {
//return barredList;
//}
//
//@JsonProperty("barredList")
//public void setBarredList(HashMap<String,ArrayList<BarredUserVo>> barredList) {
//this.barredList = barredList;
//}


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

@JsonProperty("successList")
public List<Object> getSuccessList() {
return successList;
}

@JsonProperty("successList")
public void setSuccessList(List<Object> successList) {
this.successList = successList;
}

@JsonProperty("errorMap")
public ErrorMap getErrorMap() {
	return errorMap;
}

@JsonProperty("errorMap")
public void setErrorMap(ErrorMap errorMap) {
	this.errorMap = errorMap;
}

}
