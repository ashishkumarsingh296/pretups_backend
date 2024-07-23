package restassuredapi.pojo.c2sgettransactiondetailresponsepojo;

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
"countPercentage",
"errorMap",
"fromDate",
"lastMonthCount",
"lastMonthValue",
"message",
"messageCode",
"previousFromDate",
"previousToDate",
"referenceId",
"service",
"status",
"successList",
"toDate",
"totalCount",
"totalPercentage",
"totalValue",
"transferList"
})
public class C2SGetTransactionDetailsResponsePojo {

@JsonProperty("countPercentage")
private String countPercentage;
@JsonProperty("errorMap")
private ErrorMap errorMap;
@JsonProperty("fromDate")
private String fromDate;
@JsonProperty("lastMonthCount")
private String lastMonthCount;
@JsonProperty("lastMonthValue")
private String lastMonthValue;
@JsonProperty("message")
private String message;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("previousFromDate")
private String previousFromDate;
@JsonProperty("previousToDate")
private String previousToDate;
@JsonProperty("referenceId")
private Integer referenceId;
@JsonProperty("service")
private String service;
@JsonProperty("status")
private String status;
@JsonProperty("successList")
private List<SuccessList> successList = null;
@JsonProperty("toDate")
private String toDate;
@JsonProperty("totalCount")
private String totalCount;
@JsonProperty("totalPercentage")
private String totalPercentage;
@JsonProperty("totalValue")
private String totalValue;
@JsonProperty("transferList")
private List<TransferList> transferList = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("countPercentage")
public String getCountPercentage() {
return countPercentage;
}

@JsonProperty("countPercentage")
public void setCountPercentage(String countPercentage) {
this.countPercentage = countPercentage;
}

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

@JsonProperty("lastMonthCount")
public String getLastMonthCount() {
return lastMonthCount;
}

@JsonProperty("lastMonthCount")
public void setLastMonthCount(String lastMonthCount) {
this.lastMonthCount = lastMonthCount;
}

@JsonProperty("lastMonthValue")
public String getLastMonthValue() {
return lastMonthValue;
}

@JsonProperty("lastMonthValue")
public void setLastMonthValue(String lastMonthValue) {
this.lastMonthValue = lastMonthValue;
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

@JsonProperty("previousFromDate")
public String getPreviousFromDate() {
return previousFromDate;
}

@JsonProperty("previousFromDate")
public void setPreviousFromDate(String previousFromDate) {
this.previousFromDate = previousFromDate;
}

@JsonProperty("previousToDate")
public String getPreviousToDate() {
return previousToDate;
}

@JsonProperty("previousToDate")
public void setPreviousToDate(String previousToDate) {
this.previousToDate = previousToDate;
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

@JsonProperty("totalCount")
public String getTotalCount() {
return totalCount;
}

@JsonProperty("totalCount")
public void setTotalCount(String totalCount) {
this.totalCount = totalCount;
}

@JsonProperty("totalPercentage")
public String getTotalPercentage() {
return totalPercentage;
}

@JsonProperty("totalPercentage")
public void setTotalPercentage(String totalPercentage) {
this.totalPercentage = totalPercentage;
}

@JsonProperty("totalValue")
public String getTotalValue() {
return totalValue;
}

@JsonProperty("totalValue")
public void setTotalValue(String totalValue) {
this.totalValue = totalValue;
}

@JsonProperty("transferList")
public List<TransferList> getTransferList() {
return transferList;
}

@JsonProperty("transferList")
public void setTransferList(List<TransferList> transferList) {
this.transferList = transferList;
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

