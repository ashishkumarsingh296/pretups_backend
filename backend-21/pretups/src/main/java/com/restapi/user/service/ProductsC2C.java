
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
    "productcode",
    "qty"
})
public class ProductsC2C {

    @JsonProperty("productcode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "101", required= true, description = "Source Type")
    private String productcode;
    @JsonProperty("qty")
    @io.swagger.v3.oas.annotations.media.Schema(example = "22", required= true, description = "Source Type")
    private String qty;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("productcode")
    public String getProductcode() {
        return productcode;
    }

    @JsonProperty("productcode")
    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    @JsonProperty("qty")
    public String getQty() {
        return qty;
    }

    @JsonProperty("qty")
    public void setQty(String qty) {
        this.qty = qty;
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
