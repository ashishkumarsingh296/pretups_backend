package restassuredapi.pojo.barunbarchanneluserrequestpojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"barringReason",
"barringType"
})
@Generated("jsonschema2pojo")
	public class Bar {
	
	@JsonProperty("barringReason")
	private String barringReason;
	@JsonProperty("barringType")
	private String barringType;
	
	@JsonProperty("barringReason")
	public String getBarringReason() {
	return barringReason;
	}
	
	@JsonProperty("barringReason")
	public void setBarringReason(String barringReason) {
	this.barringReason = barringReason;
	}
	
	public Bar withBarringReason(String barringReason) {
	this.barringReason = barringReason;
	return this;
	}
	
	@JsonProperty("barringType")
	public String getBarringType() {
	return barringType;
	}
	
	@JsonProperty("barringType")
	public void setBarringType(String barringType) {
	this.barringType = barringType;
	}
	
	public Bar withBarringType(String barringType) {
	this.barringType = barringType;
	return this;
	}
	
}