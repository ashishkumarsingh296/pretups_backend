
package restassuredapi.pojo.vouchervalidateinforesponsepojo;

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
"voucherCount",
"status",
"messageCode",
"message"
})
public class VoucherValidateInfoResponsePojo {

@JsonProperty("voucherCount")
private String voucherCount;
@JsonProperty("status")
private String status;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("message")
private String message;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("voucherCount")
public String getVoucherCount() {
return voucherCount;
}

@JsonProperty("voucherCount")
public void setVoucherCount(String voucherCount) {
this.voucherCount = voucherCount;
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

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}