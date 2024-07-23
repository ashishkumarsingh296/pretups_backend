
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelTransferDetailsRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
    @JsonProperty("reqGatewayLoginId")
    private String reqGatewayLoginId;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
    @JsonProperty("reqGatewayPassword")
    private String reqGatewayPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
    @JsonProperty("reqGatewayCode")
    private String reqGatewayCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
    @JsonProperty("reqGatewayType")
    private String reqGatewayType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
    @JsonProperty("servicePort")
    private String servicePort;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
    @JsonProperty("sourceType")
    private String sourceType;
    
    @JsonProperty("data")
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true)
    ChannelTransferDetailsData data;
     
    /*@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();*/

    @JsonProperty("reqGatewayLoginId")
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false/* , defaultValue = "" */, description = "Request Gateway Login Id")
    public String getReqGatewayLoginId() {
        return reqGatewayLoginId;
    }

    @JsonProperty("reqGatewayLoginId")
    public void setReqGatewayLoginId(String reqGatewayLoginId) {
        this.reqGatewayLoginId = reqGatewayLoginId;
    }

    @JsonProperty("reqGatewayPassword")
    public String getReqGatewayPassword() {
        return reqGatewayPassword;
    }

    @JsonProperty("reqGatewayPassword")
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */, description = "Request Gateway Password")
    public void setReqGatewayPassword(String reqGatewayPassword) {
        this.reqGatewayPassword = reqGatewayPassword;
    }

    @JsonProperty("reqGatewayCode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */, description = "Request Gateway Code")
    public String getReqGatewayCode() {
        return reqGatewayCode;
    }

    @JsonProperty("reqGatewayCode")
    public void setReqGatewayCode(String reqGatewayCode) {
        this.reqGatewayCode = reqGatewayCode;
    }

    @JsonProperty("reqGatewayType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */, description = "Request Gateway Type")
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
    }

    @JsonProperty("servicePort")
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required = false/* , defaultValue = "" */,description = "Service Port")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    @JsonProperty("sourceType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = false/* , defaultValue = "" */, description = "Source Type")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("sourceType")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }


    @JsonProperty("data")
    public void setData(ChannelTransferDetailsData data) {
        this.data = data;
    }
    
    @JsonProperty("data")
    public ChannelTransferDetailsData getData() {
		return data;
	}

	/*@JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/

	@Override
	public String toString() {
		return "ChannelTransferDetailsRequestVO [reqGatewayLoginId=" + reqGatewayLoginId + ", reqGatewayPassword="
				+ reqGatewayPassword + ", reqGatewayCode=" + reqGatewayCode + ", reqGatewayType=" + reqGatewayType
				+ ", servicePort=" + servicePort + ", sourceType=" + sourceType + ", data=" + data
				+ "]";
	}

   
}
 



