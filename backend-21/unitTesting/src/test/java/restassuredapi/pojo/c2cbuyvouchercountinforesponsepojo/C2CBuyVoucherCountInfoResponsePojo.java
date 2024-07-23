
package restassuredapi.pojo.c2cbuyvouchercountinforesponsepojo;

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
"channelUsersList",
"message",
"messageCode",
"status",
"transactionId"
})
public class C2CBuyVoucherCountInfoResponsePojo {


@JsonProperty("channelUsersList")
private List<C2CBuyVoucherCountInfoResponse> channelUsersList = null;
@JsonProperty("message")
private String message;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("status")
private boolean status;
@JsonProperty("transactionId")
private String transactionId;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();


@JsonProperty("channelUsersList")
public List<C2CBuyVoucherCountInfoResponse> getChannelUsersList() {
return channelUsersList;
}

@JsonProperty("channelUsersList")
public void setChannelUsersList(List<C2CBuyVoucherCountInfoResponse> channelUsersList) {
this.channelUsersList = channelUsersList;
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
public boolean getStatus() {
return status;
}

@JsonProperty("status")
public void setStatus(boolean status) {
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