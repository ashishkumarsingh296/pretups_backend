package restassuredapi.pojo;

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
"name",
"status",
"serviceType"
})
@Generated("jsonschema2pojo")
public class ServicesListEntriesPOJO {
	@JsonProperty("name")
	private String name;
	@JsonProperty("status")
	private String status;
	@JsonProperty("serviceType")
	private String serviceType;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("name")
	public String getName() {
	return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
	this.name = name;
	}

	@JsonProperty("status")
	public String getStatus() {
	return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
	this.status = status;
	}

	@JsonProperty("serviceType")
	public String getServiceType() {
	return serviceType;
	}

	@JsonProperty("serviceType")
	public void setServiceType(String serviceType) {
	this.serviceType = serviceType;
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
