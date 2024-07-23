
package restassuredapi.pojo.trfuserdetailsresponsepojo;

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
    "productName",
    "productUserMinTransferValue",
    "productUserMaxTransferValue",
    "productUserBalance"
})
public class Userproductdetail {

    @JsonProperty("productName")
    private String productName;
    @JsonProperty("productUserMinTransferValue")
    private String productUserMinTransferValue;
    @JsonProperty("productUserMaxTransferValue")
    private String productUserMaxTransferValue;
    @JsonProperty("productUserBalance")
    private String productUserBalance;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("productUserMinTransferValue")
    public String getProductUserMinTransferValue() {
        return productUserMinTransferValue;
    }

    @JsonProperty("productUserMinTransferValue")
    public void setProductUserMinTransferValue(String productUserMinTransferValue) {
        this.productUserMinTransferValue = productUserMinTransferValue;
    }

    @JsonProperty("productUserMaxTransferValue")
    public String getProductUserMaxTransferValue() {
        return productUserMaxTransferValue;
    }

    @JsonProperty("productUserMaxTransferValue")
    public void setProductUserMaxTransferValue(String productUserMaxTransferValue) {
        this.productUserMaxTransferValue = productUserMaxTransferValue;
    }

    @JsonProperty("productUserBalance")
    public String getProductUserBalance() {
        return productUserBalance;
    }

    @JsonProperty("productUserBalance")
    public void setProductUserBalance(String productUserBalance) {
        this.productUserBalance = productUserBalance;
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
