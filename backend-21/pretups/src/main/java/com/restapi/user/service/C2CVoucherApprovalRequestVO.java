package com.restapi.user.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reqGatewayLoginId",
    "data",
    "sourceType",
    "reqGatewayType",
    "reqGatewayPassword",
    "servicePort",
    "reqGatewayCode"
})

public class C2CVoucherApprovalRequestVO {
	public C2CVoucherApprovalRequestVO(){
		DataVcApp data = new DataVcApp();
		this.setData(data);
		
	}
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    @JsonProperty("reqGatewayLoginId")
    private String reqGatewayLoginId;
	
    @JsonProperty("data")
    private DataVcApp data;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    @JsonProperty("sourceType")
    private String sourceType;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    @JsonProperty("reqGatewayType")
    private String reqGatewayType;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    @JsonProperty("reqGatewayPassword")
    private String reqGatewayPassword;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    @JsonProperty("servicePort")
    private String servicePort;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    @JsonProperty("reqGatewayCode")
    private String reqGatewayCode;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayLoginId")
    public String getReqGatewayLoginId() {
        return reqGatewayLoginId;
    }

    @JsonProperty("reqGatewayLoginId")
    public void setReqGatewayLoginId(String reqGatewayLoginId) {
        this.reqGatewayLoginId = reqGatewayLoginId;
    }

    @JsonProperty("data")
    public DataVcApp getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(DataVcApp data) {
        this.data = data;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = false/* , defaultValue = "" */)
    @JsonProperty("sourceType")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("sourceType")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayType")
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayPassword")
    public String getReqGatewayPassword() {
        return reqGatewayPassword;
    }

    @JsonProperty("reqGatewayPassword")
    public void setReqGatewayPassword(String reqGatewayPassword) {
        this.reqGatewayPassword = reqGatewayPassword;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required = false/* , defaultValue = "" */)
    @JsonProperty("servicePort")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayCode")
    public String getReqGatewayCode() {
        return reqGatewayCode;
    }

    @JsonProperty("reqGatewayCode")
    public void setReqGatewayCode(String reqGatewayCode) {
        this.reqGatewayCode = reqGatewayCode;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("reqGatewayLoginId = ").append(reqGatewayLoginId)
        		.append("data = ").append( data).append("sourceType").append( sourceType)
        		.append("reqGatewayType = ").append( reqGatewayType)
        		.append("reqGatewayPassword = ").append(reqGatewayPassword)
        		.append("servicePort = ").append(servicePort)
        		.append("reqGatewayCode = ").append(reqGatewayCode)).toString();
    }

}


class VoucherDetail {

    @JsonProperty("fromSerialNum")
    private String fromSerialNum;
    @JsonProperty("toSerialNum")
    private String toSerialNum;
   
    @io.swagger.v3.oas.annotations.media.Schema(example = "9995120000000134", required = true/* , defaultValue = "" */)
    @JsonProperty("fromSerialNum")
    public String getFromSerialNum() {
        return fromSerialNum;
    }

    @JsonProperty("fromSerialNum")
    public void setFromSerialNum(String fromSerialNum) {
        this.fromSerialNum = fromSerialNum;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "9995120000000135", required = true/* , defaultValue = "" */)
    @JsonProperty("toSerialNum")
    public String getToSerialNum() {
        return toSerialNum;
    }

    @JsonProperty("toSerialNum")
    public void setToSerialNum(String toSerialNum) {
        this.toSerialNum = toSerialNum;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("fromSerialNum = ").append(fromSerialNum)
        		.append("toSerialNum = ").append(toSerialNum)).toString();
        		
    }

}


