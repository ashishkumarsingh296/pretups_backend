package restassuredapi.pojo.commissionProfile;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "geoList", "gradeList", "status", "messageCode", "message", "errorMap" })
public class GeoGradePOJO {
	@JsonProperty("geoList")
	private Object geoList;
	@JsonProperty("gradeList")
	private Object gradeList;
	@JsonProperty("status")
	private Integer status;
	@JsonProperty("messageCode")
	private String messageCode;
	@JsonProperty("message")
	private String message;
	@JsonProperty("errorMap")
	private Object errorMap;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	@JsonProperty("geoList")
	public Object getGeoList() {
		return geoList;
	}

	@JsonProperty("geoList")
	public void setGeoList(Object geoList) {
		this.geoList = geoList;
	}

	@JsonProperty("gradeList")
	public Object getGradeList() {
		return gradeList;
	}

	@JsonProperty("gradeList")
	public void setGradeList(Object gradeList) {
		this.gradeList = gradeList;
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
	public Object getErrorMap() {
		return errorMap;
	}

	@JsonProperty("errorMap")
	public void setErrorMap(Object errorMap) {
		this.errorMap = errorMap;
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
