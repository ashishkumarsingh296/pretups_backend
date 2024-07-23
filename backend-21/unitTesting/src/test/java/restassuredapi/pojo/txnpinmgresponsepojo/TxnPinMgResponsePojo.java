package restassuredapi.pojo.txnpinmgresponsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	  "status",
	  "messageCode",
	  "message",
	  "errorMap",
	  "pinChangeRequired"
})
public class TxnPinMgResponsePojo {

	@JsonProperty("status")
	private Integer status;
	
	@JsonProperty("messageCode")
	private String messageCode;
	
	@JsonProperty("message")
	private String  message;
	
	@JsonProperty("errorMap")
	private String  errorMap;
	
	@JsonProperty("pinChangeRequired")
	private boolean pinChangeRequired;
	
	
	
	public TxnPinMgResponsePojo() {
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
	public String getErrorMap() {
		return errorMap;
	}
	@JsonProperty("errorMap")
	public void setErrorMap(String errorMap) {
		this.errorMap = errorMap;
	}
	@JsonProperty("pinChangeRequired")
	public boolean isPinChangeRequired() {
		return pinChangeRequired;
	}
	@JsonProperty("pinChangeRequired")
	public void setPinChangeRequired(boolean pinChangeRequired) {
		this.pinChangeRequired = pinChangeRequired;
	}
	
	
	
	
}
