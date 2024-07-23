
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
    "paymentinstnumber",
    "paymentdate",
    "paymenttype"
})
public class PaymentdetailC2C {

    @JsonProperty("paymentinstnumber")
    private String paymentinstnumber;
    @JsonProperty("paymentdate")
    private String paymentdate;
    @JsonProperty("paymenttype")
    private String paymenttype;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("paymentinstnumber")
    public String getPaymentinstnumber() {
        return paymentinstnumber;
    }

    @JsonProperty("paymentinstnumber")
    public void setPaymentinstnumber(String paymentinstnumber) {
        this.paymentinstnumber = paymentinstnumber;
    }

    @JsonProperty("paymentdate")
    public String getPaymentdate() {
        return paymentdate;
    }

    @JsonProperty("paymentdate")
    public void setPaymentdate(String paymentdate) {
        this.paymentdate = paymentdate;
    }

    @JsonProperty("paymenttype")
    public String getPaymenttype() {
        return paymenttype;
    }

    @JsonProperty("paymenttype")
    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
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
