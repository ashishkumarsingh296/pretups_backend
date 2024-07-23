
package restassuredapi.pojo.passbookdownloadresponsepojo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "c2cReverseAmount",
    "c2creturnSale",
    "c2cwithdrawal",
    "channelSales",
    "closingBalance",
    "commission",
    "customerSales",
    "o2cReturnAmount",
    "o2cWithdrawAmount",
    "openingBalance",
    "productName",
    "stockPurchase",
    "transDate"
})
@Generated("jsonschema2pojo")
public class Passbook {

    @JsonProperty("c2cReverseAmount")
    private Integer c2cReverseAmount;
    @JsonProperty("c2creturnSale")
    private Integer c2creturnSale;
    @JsonProperty("c2cwithdrawal")
    private Integer c2cwithdrawal;
    @JsonProperty("channelSales")
    private Integer channelSales;
    @JsonProperty("closingBalance")
    private Integer closingBalance;
    @JsonProperty("commission")
    private Integer commission;
    @JsonProperty("customerSales")
    private Integer customerSales;
    @JsonProperty("o2cReturnAmount")
    private Integer o2cReturnAmount;
    @JsonProperty("o2cWithdrawAmount")
    private Integer o2cWithdrawAmount;
    @JsonProperty("openingBalance")
    private Integer openingBalance;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("stockPurchase")
    private Integer stockPurchase;
    @JsonProperty("transDate")
    private String transDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("c2cReverseAmount")
    public Integer getC2cReverseAmount() {
        return c2cReverseAmount;
    }

    @JsonProperty("c2cReverseAmount")
    public void setC2cReverseAmount(Integer c2cReverseAmount) {
        this.c2cReverseAmount = c2cReverseAmount;
    }

    @JsonProperty("c2creturnSale")
    public Integer getC2creturnSale() {
        return c2creturnSale;
    }

    @JsonProperty("c2creturnSale")
    public void setC2creturnSale(Integer c2creturnSale) {
        this.c2creturnSale = c2creturnSale;
    }

    @JsonProperty("c2cwithdrawal")
    public Integer getC2cwithdrawal() {
        return c2cwithdrawal;
    }

    @JsonProperty("c2cwithdrawal")
    public void setC2cwithdrawal(Integer c2cwithdrawal) {
        this.c2cwithdrawal = c2cwithdrawal;
    }

    @JsonProperty("channelSales")
    public Integer getChannelSales() {
        return channelSales;
    }

    @JsonProperty("channelSales")
    public void setChannelSales(Integer channelSales) {
        this.channelSales = channelSales;
    }

    @JsonProperty("closingBalance")
    public Integer getClosingBalance() {
        return closingBalance;
    }

    @JsonProperty("closingBalance")
    public void setClosingBalance(Integer closingBalance) {
        this.closingBalance = closingBalance;
    }

    @JsonProperty("commission")
    public Integer getCommission() {
        return commission;
    }

    @JsonProperty("commission")
    public void setCommission(Integer commission) {
        this.commission = commission;
    }

    @JsonProperty("customerSales")
    public Integer getCustomerSales() {
        return customerSales;
    }

    @JsonProperty("customerSales")
    public void setCustomerSales(Integer customerSales) {
        this.customerSales = customerSales;
    }

    @JsonProperty("o2cReturnAmount")
    public Integer getO2cReturnAmount() {
        return o2cReturnAmount;
    }

    @JsonProperty("o2cReturnAmount")
    public void setO2cReturnAmount(Integer o2cReturnAmount) {
        this.o2cReturnAmount = o2cReturnAmount;
    }

    @JsonProperty("o2cWithdrawAmount")
    public Integer getO2cWithdrawAmount() {
        return o2cWithdrawAmount;
    }

    @JsonProperty("o2cWithdrawAmount")
    public void setO2cWithdrawAmount(Integer o2cWithdrawAmount) {
        this.o2cWithdrawAmount = o2cWithdrawAmount;
    }

    @JsonProperty("openingBalance")
    public Integer getOpeningBalance() {
        return openingBalance;
    }

    @JsonProperty("openingBalance")
    public void setOpeningBalance(Integer openingBalance) {
        this.openingBalance = openingBalance;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("stockPurchase")
    public Integer getStockPurchase() {
        return stockPurchase;
    }

    @JsonProperty("stockPurchase")
    public void setStockPurchase(Integer stockPurchase) {
        this.stockPurchase = stockPurchase;
    }

    @JsonProperty("transDate")
    public String getTransDate() {
        return transDate;
    }

    @JsonProperty("transDate")
    public void setTransDate(String transDate) {
        this.transDate = transDate;
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
