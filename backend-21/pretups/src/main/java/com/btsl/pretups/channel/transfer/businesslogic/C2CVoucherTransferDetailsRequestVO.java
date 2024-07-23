package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.pretups.channel.transfer.requesthandler.C2CVoucherTransferDetailsData;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2CVoucherTransferDetailsRequestVO {

    @JsonProperty("reqGatewayLoginId")
    private String reqGatewayLoginId;
    @JsonProperty("reqGatewayPassword")
    private String reqGatewayPassword;
    @JsonProperty("reqGatewayCode")
    private String reqGatewayCode;
    @JsonProperty("reqGatewayType")
    private String reqGatewayType;
    @JsonProperty("servicePort")
    private String servicePort;
    @JsonProperty("sourceType")
    private String sourceType;
    
    @JsonProperty("dataCal")
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true)
    C2CVoucherTransferDetailsData dataCal;
     
    /*@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();*/

    @JsonProperty("reqGatewayLoginId")
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = true/* , defaultValue = "" */, description = "Request Gateway Login Id")
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description = "Request Gateway Password")
    public void setReqGatewayPassword(String reqGatewayPassword) {
        this.reqGatewayPassword = reqGatewayPassword;
    }

    @JsonProperty("reqGatewayCode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */, description = "Request Gateway Code")
    public String getReqGatewayCode() {
        return reqGatewayCode;
    }

    @JsonProperty("reqGatewayCode")
    public void setReqGatewayCode(String reqGatewayCode) {
        this.reqGatewayCode = reqGatewayCode;
    }

    @JsonProperty("reqGatewayType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */, description = "Request Gateway Type")
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
    }

    @JsonProperty("servicePort")
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required = true/* , defaultValue = "" */,description = "Service Port")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    @JsonProperty("sourceType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = true/* , defaultValue = "" */, description = "Source Type")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("sourceType")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }


    @JsonProperty("dataCal")
    public void setDataCal(C2CVoucherTransferDetailsData dataCal) {
        this.dataCal = dataCal;
    }
    
    @JsonProperty("data")
    public C2CVoucherTransferDetailsData getDataCal() {
		return dataCal;
	}

	/*@JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);s
    }*/

	@Override
	public String toString() {
		return "C2CVoucherTransferDetailsRequestVO [reqGatewayLoginId=" + reqGatewayLoginId + ", reqGatewayPassword="
				+ reqGatewayPassword + ", reqGatewayCode=" + reqGatewayCode + ", reqGatewayType=" + reqGatewayType
				+ ", servicePort=" + servicePort + ", sourceType=" + sourceType + ", dataCal=" + dataCal
				+ "]";
	}

   
}
