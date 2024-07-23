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
"errorcode"
})

public class PendingTxnListResponseVO extends BaseResponseMultiple {

@JsonProperty("message")
private String message;
@JsonProperty("totlaTrnxCount")
private Long totalTrnxCount;
@JsonProperty("errorcode")
private String errorcode;
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
public void setTotlaTrnxCount(long res) {
this.totalTrnxCount = res;
}

@JsonProperty("errorcode")
public String getErrorcode() {
return errorcode;
}

@JsonProperty("errorcode")
public void setErrorcode(String errorcode) {
this.errorcode = errorcode;
}

@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("PendingTxnListResponseVO [message=").append(message)
			.append(", totlaTrnxCount=").append(totalTrnxCount).append(", errorcode=").append(errorcode)
			.append("]");
	return builder.toString();
}

	
	
	
	
	
	
}
