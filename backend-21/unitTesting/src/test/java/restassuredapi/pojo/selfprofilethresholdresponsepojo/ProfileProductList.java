
package restassuredapi.pojo.selfprofilethresholdresponsepojo;

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
    "productCode",
    "productShortCode",
    "c2sMinTxnAmtAsLong",
    "c2sMaxTxnAmtAsLong",
    "allowedMaxPercentage",
    "minResidualBalanceAsLong",
    "allowedMaxPercentageInt",
    "maxBalanceAsLong",
    "altBalanceLong",
    "currentBalance",
    "altBalance",
    "c2sMaxTxnAmt",
    "minBalance",
    "maxBalance",
    "c2sMinTxnAmt",
    "productName"
})
public class ProfileProductList {

    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("productShortCode")
    private String productShortCode;
    @JsonProperty("c2sMinTxnAmtAsLong")
    private Long c2sMinTxnAmtAsLong;
    @JsonProperty("c2sMaxTxnAmtAsLong")
    private Long c2sMaxTxnAmtAsLong;
    @JsonProperty("allowedMaxPercentage")
    private String allowedMaxPercentage;
    @JsonProperty("minResidualBalanceAsLong")
    private Long minResidualBalanceAsLong;
    @JsonProperty("allowedMaxPercentageInt")
    private Long allowedMaxPercentageInt;
    @JsonProperty("maxBalanceAsLong")
    private Long maxBalanceAsLong;
    @JsonProperty("altBalanceLong")
    private Long altBalanceLong;
    @JsonProperty("currentBalance")
    private String currentBalance;
    @JsonProperty("altBalance")
    private String altBalance;
    @JsonProperty("c2sMaxTxnAmt")
    private String c2sMaxTxnAmt;
    @JsonProperty("minBalance")
    private String minBalance;
    @JsonProperty("maxBalance")
    private String maxBalance;
    @JsonProperty("c2sMinTxnAmt")
    private String c2sMinTxnAmt;
    @JsonProperty("productName")
    private String productName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("productShortCode")
    public String getProductShortCode() {
        return productShortCode;
    }

    @JsonProperty("productShortCode")
    public void setProductShortCode(String productShortCode) {
        this.productShortCode = productShortCode;
    }

    @JsonProperty("c2sMinTxnAmtAsLong")
    public Long getC2sMinTxnAmtAsLong() {
        return c2sMinTxnAmtAsLong;
    }

    @JsonProperty("c2sMinTxnAmtAsLong")
    public void setC2sMinTxnAmtAsLong(Long c2sMinTxnAmtAsLong) {
        this.c2sMinTxnAmtAsLong = c2sMinTxnAmtAsLong;
    }

    @JsonProperty("c2sMaxTxnAmtAsLong")
    public Long getC2sMaxTxnAmtAsLong() {
        return c2sMaxTxnAmtAsLong;
    }

    @JsonProperty("c2sMaxTxnAmtAsLong")
    public void setC2sMaxTxnAmtAsLong(Long c2sMaxTxnAmtAsLong) {
        this.c2sMaxTxnAmtAsLong = c2sMaxTxnAmtAsLong;
    }

    @JsonProperty("allowedMaxPercentage")
    public String getAllowedMaxPercentage() {
        return allowedMaxPercentage;
    }

    @JsonProperty("allowedMaxPercentage")
    public void setAllowedMaxPercentage(String allowedMaxPercentage) {
        this.allowedMaxPercentage = allowedMaxPercentage;
    }

    @JsonProperty("minResidualBalanceAsLong")
    public Long getMinResidualBalanceAsLong() {
        return minResidualBalanceAsLong;
    }

    @JsonProperty("minResidualBalanceAsLong")
    public void setMinResidualBalanceAsLong(Long minResidualBalanceAsLong) {
        this.minResidualBalanceAsLong = minResidualBalanceAsLong;
    }

    @JsonProperty("allowedMaxPercentageInt")
    public Long getAllowedMaxPercentageInt() {
        return allowedMaxPercentageInt;
    }

    @JsonProperty("allowedMaxPercentageInt")
    public void setAllowedMaxPercentageInt(Long allowedMaxPercentageInt) {
        this.allowedMaxPercentageInt = allowedMaxPercentageInt;
    }

    @JsonProperty("maxBalanceAsLong")
    public Long getMaxBalanceAsLong() {
        return maxBalanceAsLong;
    }

    @JsonProperty("maxBalanceAsLong")
    public void setMaxBalanceAsLong(Long maxBalanceAsLong) {
        this.maxBalanceAsLong = maxBalanceAsLong;
    }

    @JsonProperty("altBalanceLong")
    public Long getAltBalanceLong() {
        return altBalanceLong;
    }

    @JsonProperty("altBalanceLong")
    public void setAltBalanceLong(Long altBalanceLong) {
        this.altBalanceLong = altBalanceLong;
    }

    @JsonProperty("currentBalance")
    public String getCurrentBalance() {
        return currentBalance;
    }

    @JsonProperty("currentBalance")
    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    @JsonProperty("altBalance")
    public String getAltBalance() {
        return altBalance;
    }

    @JsonProperty("altBalance")
    public void setAltBalance(String altBalance) {
        this.altBalance = altBalance;
    }

    @JsonProperty("c2sMaxTxnAmt")
    public String getC2sMaxTxnAmt() {
        return c2sMaxTxnAmt;
    }

    @JsonProperty("c2sMaxTxnAmt")
    public void setC2sMaxTxnAmt(String c2sMaxTxnAmt) {
        this.c2sMaxTxnAmt = c2sMaxTxnAmt;
    }

    @JsonProperty("minBalance")
    public String getMinBalance() {
        return minBalance;
    }

    @JsonProperty("minBalance")
    public void setMinBalance(String minBalance) {
        this.minBalance = minBalance;
    }

    @JsonProperty("maxBalance")
    public String getMaxBalance() {
        return maxBalance;
    }

    @JsonProperty("maxBalance")
    public void setMaxBalance(String maxBalance) {
        this.maxBalance = maxBalance;
    }

    @JsonProperty("c2sMinTxnAmt")
    public String getC2sMinTxnAmt() {
        return c2sMinTxnAmt;
    }

    @JsonProperty("c2sMinTxnAmt")
    public void setC2sMinTxnAmt(String c2sMinTxnAmt) {
        this.c2sMinTxnAmt = c2sMinTxnAmt;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
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
