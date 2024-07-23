package restassuredapi.pojo.transactionaldataresponsepojo;

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
"InTransactionData",
"OutTransactionData",
"message",
"messageCode",
"status",
"transactionId"
})
public class TransactionalDataResponsePojo {

@JsonProperty("InTransactionData")
private InTransactionData inTransactionData;
@JsonProperty("OutTransactionData")
private OutTransactionData outTransactionData;
@JsonProperty("message")
private String message;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("status")
private Integer status;
@JsonProperty("transactionId")
private String transactionId;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("InTransactionData")
public InTransactionData getInTransactionData() {
return inTransactionData;
}

@JsonProperty("InTransactionData")
public void setInTransactionData(InTransactionData inTransactionData) {
this.inTransactionData = inTransactionData;
}

@JsonProperty("OutTransactionData")
public OutTransactionData getOutTransactionData() {
return outTransactionData;
}

@JsonProperty("OutTransactionData")
public void setOutTransactionData(OutTransactionData outTransactionData) {
this.outTransactionData = outTransactionData;
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