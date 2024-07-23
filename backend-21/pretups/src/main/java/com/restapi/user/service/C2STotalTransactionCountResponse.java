package com.restapi.user.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"totalTrnxCount",
"message",
"date",
"errorcode"
})

public class C2STotalTransactionCountResponse extends BaseResponseMultiple {


@JsonProperty("date")
private String date;
@JsonProperty("message")
private String message;
@JsonProperty("totlaTrnxCount")
private Long totalTrnxCount;
@JsonProperty("errorcode")
private String errorcode;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("date")
public String getDate() {
return date;
}

@JsonProperty("date")
public void setDate(String date) {
this.date = date;
}

@JsonProperty("message")
public String getMessage() {
return message;
}

@JsonProperty("message")
public void setMessage(String message) {
this.message = message;
}

@JsonProperty("totlaTrnxCount")
public Long getTotlaTrnxCount() {
return totalTrnxCount;
}

@JsonProperty("totlaTrnxCount")
public void setTotlaTrnxCount(long recentC2sRes) {
this.totalTrnxCount = recentC2sRes;
}

@JsonProperty("errorcode")
public String getErrorcode() {
return errorcode;
}

@JsonProperty("errorcode")
public void setErrorcode(String errorcode) {
this.errorcode = errorcode;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("C2STotalTransactionCountResponse [date=").append(date).append(", message=").append(message)
			.append(", totlaTrnxCount=").append(totalTrnxCount).append(", errorcode=").append(errorcode)
			.append(", additionalProperties=").append(additionalProperties).append("]");
	return builder.toString();
}

	
	
	
	
	
	
}
