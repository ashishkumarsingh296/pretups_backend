package restassuredapi.pojo.suspendResumerequestpojo;

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
	"loginid",
	"msisdn",
	"remarks",
	"reqType",
	"userType"
	})


public class Data1 {
	@JsonProperty("loginid")
	private String loginid;
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("remarks")
	private String remarks;
	@JsonProperty("reqType")
	private String reqType;
	@JsonProperty("userType")
	private String userType;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("loginid")
	public String getLoginid() {
	return loginid;
	}

	@JsonProperty("loginid")
	public void setLoginid(String loginid) {
	this.loginid = loginid;
	}

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

	@JsonProperty("reqType")
	public String getReqType() {
	return reqType;
	}

	@JsonProperty("reqType")
	public void setReqType(String reqType) {
	this.reqType = reqType;
	}

	@JsonProperty("userType")
	public String getUserType() {
	return userType;
	}

	@JsonProperty("userType")
	public void setUserType(String userType) {
	this.userType = userType;
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



