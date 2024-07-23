
package restassuredapi.pojo.focInitiateRequestPojo;

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
    "appQuantity",
    "productCode"
})
public class FocProduct {

    @JsonProperty("appQuantity")
    private int appQuantity;
    @JsonProperty("productCode")
    private String productCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public FocProduct() {
    }

    /**
     * 
     * @param appQuantity
     * @param productCode
     */
    public FocProduct(int appQuantity, String productCode) {
        super();
        this.appQuantity = appQuantity;
        this.productCode = productCode;
    }

    @JsonProperty("appQuantity")
    public int getAppQuantity() {
        return appQuantity;
    }

    @JsonProperty("appQuantity")
    public void setAppQuantity(int appQuantity) {
        this.appQuantity = appQuantity;
    }

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

   /* @Override
    public String toString() {
        return new ToStringBuilder(this).append("appQuantity", appQuantity).append("productCode", productCode).append("additionalProperties", additionalProperties).toString();
    }*/

}
