
package com.restapi.user.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reqGatewayLoginId",
    "reqGatewayPassword",
    "reqGatewayCode",
    "reqGatewayType",
    "servicePort",
    "sourceType",
    "data"
})
public class C2CReturnRequestVO {

    @JsonProperty("reqGatewayLoginId")
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required= false, description = "Source Type", hidden = true)
    private String reqGatewayLoginId;
    @JsonProperty("reqGatewayPassword")
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required= false, description = "Source Type", hidden = true)
    private String reqGatewayPassword;
    @JsonProperty("reqGatewayCode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required= false, description = "Source Type", hidden = true)
    private String reqGatewayCode;
    @JsonProperty("reqGatewayType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required= false, description = "Source Type", hidden = true)
    private String reqGatewayType;
    @JsonProperty("servicePort")
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required= false, description = "Source Type", hidden = true)
    private String servicePort;
    @JsonProperty("sourceType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required= false, description = "Source Type", hidden = true)
    private String sourceType;
    @JsonProperty("data")
    private C2CReturnWithdrawData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("reqGatewayLoginId")
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
    public void setReqGatewayPassword(String reqGatewayPassword) {
        this.reqGatewayPassword = reqGatewayPassword;
    }

    @JsonProperty("reqGatewayCode")
    public String getReqGatewayCode() {
        return reqGatewayCode;
    }

    @JsonProperty("reqGatewayCode")
    public void setReqGatewayCode(String reqGatewayCode) {
        this.reqGatewayCode = reqGatewayCode;
    }

    @JsonProperty("reqGatewayType")
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
    }

    @JsonProperty("servicePort")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    @JsonProperty("sourceType")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("sourceType")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @JsonProperty("data")
    public C2CReturnWithdrawData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(C2CReturnWithdrawData data) {
        this.data = data;
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