package com.btsl.pretups.channel.transfer.requesthandler;

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
public class C2CTransferDetailsVO {
	C2CTransferDetailsVO(){
			DataTrf data = new DataTrf();
			this.setData(data);
			
		}


		@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	 	@JsonProperty("reqGatewayLoginId")
	    private String reqGatewayLoginId;
		
	    @JsonProperty("data")
	    private DataTrf data;
	    
	    @io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	    @JsonProperty("sourceType")
	    private String sourceType;
	    
	    @io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	    @JsonProperty("reqGatewayType")
	    private String reqGatewayType;
	    
	    @io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	    @JsonProperty("reqGatewayPassword")
	    private String reqGatewayPassword;
	    
	    @io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	    @JsonProperty("servicePort")
	    private String servicePort;
	    
	    @io.swagger.v3.oas.annotations.media.Schema(hidden =true)
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
	    public DataTrf getData() {
	        return data;
	    }

	    @JsonProperty("data")
	    public void setData(DataTrf data) {
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
//Created new public class for below code with name DataTrf
//	class DataTrf {
//
//	    @JsonProperty("extcode")
//	    private String extcode;
//	    @JsonProperty("loginid")
//	    private String loginid;
//	    
//	    @JsonProperty("extnwcode")
//	    private String extnwcode;
//	   
//	    @JsonProperty("transferId")
//	    private String transferId;
//	    @JsonProperty("transferType")
//	    private String transferType;
//	    @JsonProperty("networkCode")
//	    private String networkCode;
//	    @JsonProperty("networkCodeFor")
//	    private String networkCodeFor;
//	    
//
//	    @JsonProperty("password")
//	    private String password;
//	    @JsonProperty("pin")
//	    private String pin;
//	    @JsonProperty("msisdn")
//	    private String msisdn;
//	   
//	    
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "234", required = true/* , defaultValue = "" */)
//	    @JsonProperty("extcode")
//	    public String getExtcode() {
//	        return extcode;
//	    }
//
//	    @JsonProperty("extcode")
//	    public void setExtcode(String extcode) {
//	        this.extcode = extcode;
//	    }
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
//	    @JsonProperty("loginid")
//	    public String getLoginid() {
//	        return loginid;
//	    }
//
//	    @JsonProperty("loginid")
//	    public void setLoginid(String loginid) {
//	        this.loginid = loginid;
//	    }
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
//	    @JsonProperty("extnwcode")
//	    public String getExtnwcode() {
//	        return extnwcode;
//	    }
//
//	    @JsonProperty("extnwcode")
//	    public void setExtnwcode(String extnwcode) {
//	        this.extnwcode = extnwcode;
//	    }
//	    
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "CT200109.0609.100001", required = true/* , defaultValue = "" */)
//	    @JsonProperty("transferId")
//	    public String getTransferId() {
//	        return transferId;
//	    }
//
//	    @JsonProperty("transferId")
//	    public void setTransferId(String transferId) {
//	        this.transferId = transferId;
//	    }
//	    
//	    
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
//	    @JsonProperty("password")
//	    public String getPassword() {
//	        return password;
//	    }
//
//	    @JsonProperty("password")
//	    public void setPassword(String password) {
//	        this.password = password;
//	    }
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = true/* , defaultValue = "" */)
//	    @JsonProperty("pin")
//	    public String getPin() {
//	        return pin;
//	    }
//
//	    @JsonProperty("pin")
//	    public void setPin(String pin) {
//	        this.pin = pin;
//	    }
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
//	    @JsonProperty("msisdn")
//	    public String getMsisdn() {
//	        return msisdn;
//	    }
//
//	    @JsonProperty("msisdn")
//	    public void setMsisdn(String msisdn) {
//	        this.msisdn = msisdn;
//	    }
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "T", required = true/* , defaultValue = "" */)
//	    @JsonProperty("transferType")
//	    public String getTransferType() {
//	        return transferType;
//	    }
//
//	    @JsonProperty("transferType")
//	    public void setTransferType(String transferType) {
//	        this.transferType = transferType;
//	    }
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
//	    @JsonProperty("networkCode")
//	    public String getNetworkCode() {
//	        return networkCode;
//	    }
//
//	    @JsonProperty("networkCode")
//	    public void setNetworkCode(String networkCode) {
//	        this.networkCode = networkCode;
//	    }
//	    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
//	    @JsonProperty("networkCodeFor")
//	    public String getNetworkCodeFor() {
//	        return networkCodeFor;
//	    }
//
//	    @JsonProperty("networkCodeFor")
//	    public void setNetworkCodeFor(String networkCodeFor) {
//	        this.networkCodeFor = networkCodeFor;
//	    }
//	    @Override
//	    public String toString() {
//	    	StringBuilder sb = new StringBuilder();
//	        return (sb.append("transferId = ").append(transferId)
//	        		.append("transferType = ").append( transferType).append("networkCode").append( networkCode)
//	        		.append("networkCodeFor = ").append( networkCodeFor)
//	        		.append("extnwcode = ").append(extnwcode)
//	        		.append("msisdn = ").append(msisdn)
//	        		.append("pin = ").append(pin)
//	        		.append("loginid = ").append(loginid)
//	        		.append("password = ").append(password)
//	        		.append("extcode = ").append(extcode)).toString();
//	    }
//	    
//	}

