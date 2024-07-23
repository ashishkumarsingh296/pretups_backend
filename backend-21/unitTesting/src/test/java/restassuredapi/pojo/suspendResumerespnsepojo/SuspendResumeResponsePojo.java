package restassuredapi.pojo.suspendResumerespnsepojo;

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
"status",
"service",
"messageCode",
"message"
})
public class SuspendResumeResponsePojo {
	@JsonProperty("status")
	private Long status;
	@JsonProperty("service")
	private String service;
	@JsonProperty("messageCode")
	private String messageCode;
	@JsonProperty("message")
	private String message;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("status")
	public Long getStatus() {
	return status;
	}

	@JsonProperty("status")
	public void setStatus(Long status) {
	this.status = status;
	}

	@JsonProperty("service")
	public String getService() {
	return service;
	}

	@JsonProperty("service")
	public void setService(String service) {
	this.service = service;
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
