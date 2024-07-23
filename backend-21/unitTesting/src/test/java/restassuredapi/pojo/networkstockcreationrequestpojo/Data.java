
package restassuredapi.pojo.networkstockcreationrequestpojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "networkCode",
    "userId",
    "referenceNumber",
    "remarks",
    "walletType",
    "stockProductList"
})
public class Data {

    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("referenceNumber")
    private String referenceNumber;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("walletType")
    private String walletType;
    @JsonProperty("stockProductList")
    private List<StockProductList> stockProductList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("referenceNumber")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @JsonProperty("referenceNumber")
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("walletType")
    public String getWalletType() {
        return walletType;
    }

    @JsonProperty("walletType")
    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    @JsonProperty("stockProductList")
    public List<StockProductList> getStockProductList() {
        return stockProductList;
    }

    @JsonProperty("stockProductList")
    public void setStockProductList(List<StockProductList> stockProductList) {
        this.stockProductList = stockProductList;
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
