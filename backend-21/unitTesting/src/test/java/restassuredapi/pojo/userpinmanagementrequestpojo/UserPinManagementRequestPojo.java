package restassuredapi.pojo.userpinmanagementrequestpojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPinManagementRequestPojo {

	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("remarks")
	private String remarks;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@JsonProperty("remarks")
	public String getRemarks() {
		return remarks;
	}

	@JsonProperty("remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
