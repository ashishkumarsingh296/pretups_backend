package restassuredapi.pojo;

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
@JsonPropertyOrder({ "service", "referenceId", "status", "messageCode", "message", "errorMap", "successList",
		"domainList" })
@Generated("jsonschema2pojo")

public class GetCategoryListFromDomainCodePOJO {

	@JsonProperty("service")
	private Object service;
	@JsonProperty("referenceId")
	private Object referenceId;
	@JsonProperty("status")
	private long status;
	@JsonProperty("messageCode")
	private String messageCode;
	@JsonProperty("message")
	private String message;
	@JsonProperty("errorMap")
	private Object errorMap;
	@JsonProperty("successList")
	private List<Object> successList = null;
	@JsonProperty("domainList")
	private List<Object> domainList = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("service")
	public Object getService() {
		return service;
	}

	@JsonProperty("service")
	public void setService(Object service) {
		this.service = service;
	}

	@JsonProperty("referenceId")
	public Object getReferenceId() {
		return referenceId;
	}

	@JsonProperty("referenceId")
	public void setReferenceId(Object referenceId) {
		this.referenceId = referenceId;
	}

	@JsonProperty("status")
	public long getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(long status) {
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

	@JsonProperty("successList")
	public List<Object> getSuccessList() {
		return successList;
	}

	@JsonProperty("successList")
	public void setSuccessList(List<Object> successList) {
		this.successList = successList;
	}

	@JsonProperty("domainList")
	public List<Object> getDomainList() {
		return domainList;
	}

	@JsonProperty("domainList")
	public void setDomainList(List<Object> domainList) {
		this.domainList = domainList;
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
