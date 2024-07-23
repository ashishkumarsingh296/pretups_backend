package restassuredapi.pojo.userpinmanagementresponsepojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class UserPinManagementResponsePojo {

	@JsonProperty("message")
	private String message;
	@JsonProperty("messageCode")
	private String messageCode;
	@JsonProperty("status")
	private int status;
	@JsonProperty("transactionId")
	private String transactionId;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
	public int getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(int status) {
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
