package com.btsl.pretups.channel.transfer.requesthandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;



@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reqGatewayLoginId",
    "sourceType",
    "reqGatewayType",
    "reqGatewayPassword",
    "servicePort",
    "reqGatewayCode",
    "data"
})
public class C2CTransferApprovalListVO {
	
	C2CTransferApprovalListVO(){
		DataTrfApp data = new DataTrfApp();
		this.setData(data);
		
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
	@JsonProperty("reqGatewayLoginId")
    private String reqGatewayLoginId;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("sourceType")
    private String sourceType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayType")
    private String reqGatewayType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayPassword")
    private String reqGatewayPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("servicePort")
    private String servicePort;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayCode")
    private String reqGatewayCode;
	
    @JsonProperty("data")
    @io.swagger.v3.oas.annotations.media.Schema(required =true)
    private DataTrfApp data;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false/* , defaultValue = "" */, description = "Request Gateway Login Id")
    @JsonProperty("reqGatewayLoginId")
    public String getReqGatewayLoginId() {
        return reqGatewayLoginId;
    }

    @JsonProperty("reqGatewayLoginId")
    public void setReqGatewayLoginId(String reqGatewayLoginId) {
        this.reqGatewayLoginId = reqGatewayLoginId;
    }
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = false/* , defaultValue = "" */, description = "Source Type")
    @JsonProperty("sourceType")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("sourceType")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */, description = "Request Gateway Type")
    @JsonProperty("reqGatewayType")
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */, description = "Request Gateway Code")
    @JsonProperty("reqGatewayPassword")
    public String getReqGatewayPassword() {
        return reqGatewayPassword;
    }

    @JsonProperty("reqGatewayPassword")
    public void setReqGatewayPassword(String reqGatewayPassword) {
        this.reqGatewayPassword = reqGatewayPassword;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required = false/* , defaultValue = "" */, description = "Service Port")
    @JsonProperty("servicePort")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */, description = "Request Gateway Code")
    @JsonProperty("reqGatewayCode")
    public String getReqGatewayCode() {
        return reqGatewayCode;
    }

    @JsonProperty("reqGatewayCode")
    public void setReqGatewayCode(String reqGatewayCode) {
        this.reqGatewayCode = reqGatewayCode;
    }
    @JsonProperty("data")
    public DataTrfApp getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(DataTrfApp data) {
        this.data = data;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("reqGatewayLoginId = ").append(reqGatewayLoginId)
        		.append("sourceType").append( sourceType)
        		.append("reqGatewayType = ").append( reqGatewayType)
        		.append("reqGatewayPassword = ").append(reqGatewayPassword)
        		.append("servicePort = ").append(servicePort)
        		.append("reqGatewayCode = ").append(reqGatewayCode).append("data = ").append( data)).toString();
    }
}
 






