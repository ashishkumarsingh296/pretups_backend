package restassuredapi.pojo.barunbarchanneluserrequestpojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"bar",
"module",
"msisdn",
"userName",
"userType"
})
@Generated("jsonschema2pojo")
	public class BarUnbarChannelUserRequestPojo {
	
	@JsonProperty("bar")
	private List<Bar> bar = null;
	@JsonProperty("module")
	private String module;
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("userType")
	private String userType;
	
	@JsonProperty("bar")
	public List<Bar> getBar() {
	return bar;
	}
	
	@JsonProperty("bar")
	public void setBar(List<Bar> bar) {
	this.bar = bar;
	}
	
	public BarUnbarChannelUserRequestPojo withBar(List<Bar> bar) {
	this.bar = bar;
	return this;
	}
	
	@JsonProperty("module")
	public String getModule() {
	return module;
	}
	
	@JsonProperty("module")
	public void setModule(String module) {
	this.module = module;
	}
	
	public BarUnbarChannelUserRequestPojo withModule(String module) {
	this.module = module;
	return this;
	}
	
	@JsonProperty("msisdn")
	public String getMsisdn() {
	return msisdn;
	}
	
	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
	}
	
	@JsonProperty("userName")
	public String getUserName() {
	return userName;
	}
	
	@JsonProperty("userName")
	public void setUserName(String userName) {
	this.userName = userName;
	}
	
	public BarUnbarChannelUserRequestPojo withUserName(String userName) {
	this.userName = userName;
	return this;
	}
	
	@JsonProperty("userType")
	public String getUserType() {
	return userType;
	}
	
	@JsonProperty("userType")
	public void setUserType(String userType) {
	this.userType = userType;
	}
	
	public BarUnbarChannelUserRequestPojo withUserType(String userType) {
	this.userType = userType;
	return this;
	}
	
}