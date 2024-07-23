
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

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
public class PassbookViewRequestVO {

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
    @JsonProperty("data")
    private PassbookDetailsViewData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("reqGatewayLoginId")
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = true/* , defaultValue = "" */)
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
    public void setReqGatewayPassword(String reqGatewayPassword) {
        this.reqGatewayPassword = reqGatewayPassword;
    }

    @JsonProperty("reqGatewayCode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */)
    public String getReqGatewayCode() {
        return reqGatewayCode;
    }

    @JsonProperty("reqGatewayCode")
    public void setReqGatewayCode(String reqGatewayCode) {
        this.reqGatewayCode = reqGatewayCode;
    }

    @JsonProperty("reqGatewayType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */)
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
    }

    @JsonProperty("servicePort")
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required = true/* , defaultValue = "" */)
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    @JsonProperty("sourceType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = true/* , defaultValue = "" */)
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("sourceType")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @JsonProperty("data")
    public PassbookDetailsViewData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(PassbookDetailsViewData data) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("reqGatewayLoginId", reqGatewayLoginId).append("reqGatewayPassword", reqGatewayPassword).append("reqGatewayCode", reqGatewayCode).append("reqGatewayType", reqGatewayType).append("servicePort", servicePort).append("sourceType", sourceType).append("data", data).append("additionalProperties", additionalProperties).toString();
    }
    
    public class PassbookDetailsViewData {

        @JsonProperty("fromDate")
        private String fromDate;
        @JsonProperty("toDate")
        private String toDate;
        @JsonProperty("extnwcode")
        private String extnwcode;
        @JsonProperty("msisdn")
        private String msisdn;
        @JsonProperty("pin")
        private String pin;
        @JsonProperty("loginid")
        private String loginid;
        @JsonProperty("password")
        private String password;
        @JsonProperty("extcode")
        private String extcode;
        @JsonProperty("msisdn2")
        private String msisdn2;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("fromDate")
        public String getFromDate() {
            return fromDate;
        }

        @JsonProperty("fromDate")
        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        @JsonProperty("toDate")
        public String getToDate() {
            return toDate;
        }

        @JsonProperty("toDate")
        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        @JsonProperty("extnwcode")
        public String getExtnwcode() {
            return extnwcode;
        }

        @JsonProperty("extnwcode")
        public void setExtnwcode(String extnwcode) {
            this.extnwcode = extnwcode;
        }

        @JsonProperty("msisdn")
        public String getMsisdn() {
            return msisdn;
        }

        @JsonProperty("msisdn")
        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        @JsonProperty("pin")
        public String getPin() {
            return pin;
        }

        @JsonProperty("pin")
        public void setPin(String pin) {
            this.pin = pin;
        }

        @JsonProperty("loginid")
        public String getLoginid() {
            return loginid;
        }

        @JsonProperty("loginid")
        public void setLoginid(String loginid) {
            this.loginid = loginid;
        }

        @JsonProperty("password")
        public String getPassword() {
            return password;
        }

        @JsonProperty("password")
        public void setPassword(String password) {
            this.password = password;
        }

        @JsonProperty("extcode")
        public String getExtcode() {
            return extcode;
        }

        @JsonProperty("extcode")
        public void setExtcode(String extcode) {
            this.extcode = extcode;
        }

        @JsonProperty("msisdn2")
        public String getMsisdn2() {
            return msisdn2;
        }

        @JsonProperty("msisdn2")
        public void setMsisdn2(String msisdn2) {
            this.msisdn2 = msisdn2;
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
            return new ToStringBuilder(this).append("fromDate", fromDate).append("toDate", toDate).append("extnwcode", extnwcode).append("msisdn", msisdn).append("pin", pin).append("loginid", loginid).append("password", password).append("extcode", extcode).append("msisdn2", msisdn2).append("additionalProperties", additionalProperties).toString();
        }

    }

}
