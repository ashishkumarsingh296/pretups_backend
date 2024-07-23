
package restassuredapi.pojo.commissionslabdetailsresponsepojo;

import java.util.HashMap;
import java.util.List;
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
    "listCommissionSlabDet",
    "maxTransferValue",
    "minTransferValue",
    "multipleOf",
    "paymentMode",
    "product",
    "taxCalcOnC2CTransfer",
    "taxCalcOnFOC",
    "transactionType"
})
@Generated("jsonschema2pojo")
public class ListcommissionSlabDetVO {

    @JsonProperty("listCommissionSlabDet")
    private List<ListCommissionSlabDet> listCommissionSlabDet = null;
    @JsonProperty("maxTransferValue")
    private String maxTransferValue;
    @JsonProperty("minTransferValue")
    private String minTransferValue;
    @JsonProperty("multipleOf")
    private String multipleOf;
    @JsonProperty("paymentMode")
    private String paymentMode;
    @JsonProperty("product")
    private String product;
    @JsonProperty("taxCalcOnC2CTransfer")
    private String taxCalcOnC2CTransfer;
    @JsonProperty("taxCalcOnFOC")
    private String taxCalcOnFOC;
    @JsonProperty("transactionType")
    private String transactionType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("listCommissionSlabDet")
    public List<ListCommissionSlabDet> getListCommissionSlabDet() {
        return listCommissionSlabDet;
    }

    @JsonProperty("listCommissionSlabDet")
    public void setListCommissionSlabDet(List<ListCommissionSlabDet> listCommissionSlabDet) {
        this.listCommissionSlabDet = listCommissionSlabDet;
    }

    @JsonProperty("maxTransferValue")
    public String getMaxTransferValue() {
        return maxTransferValue;
    }

    @JsonProperty("maxTransferValue")
    public void setMaxTransferValue(String maxTransferValue) {
        this.maxTransferValue = maxTransferValue;
    }

    @JsonProperty("minTransferValue")
    public String getMinTransferValue() {
        return minTransferValue;
    }

    @JsonProperty("minTransferValue")
    public void setMinTransferValue(String minTransferValue) {
        this.minTransferValue = minTransferValue;
    }

    @JsonProperty("multipleOf")
    public String getMultipleOf() {
        return multipleOf;
    }

    @JsonProperty("multipleOf")
    public void setMultipleOf(String multipleOf) {
        this.multipleOf = multipleOf;
    }

    @JsonProperty("paymentMode")
    public String getPaymentMode() {
        return paymentMode;
    }

    @JsonProperty("paymentMode")
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    @JsonProperty("product")
    public String getProduct() {
        return product;
    }

    @JsonProperty("product")
    public void setProduct(String product) {
        this.product = product;
    }

    @JsonProperty("taxCalcOnC2CTransfer")
    public String getTaxCalcOnC2CTransfer() {
        return taxCalcOnC2CTransfer;
    }

    @JsonProperty("taxCalcOnC2CTransfer")
    public void setTaxCalcOnC2CTransfer(String taxCalcOnC2CTransfer) {
        this.taxCalcOnC2CTransfer = taxCalcOnC2CTransfer;
    }

    @JsonProperty("taxCalcOnFOC")
    public String getTaxCalcOnFOC() {
        return taxCalcOnFOC;
    }

    @JsonProperty("taxCalcOnFOC")
    public void setTaxCalcOnFOC(String taxCalcOnFOC) {
        this.taxCalcOnFOC = taxCalcOnFOC;
    }

    @JsonProperty("transactionType")
    public String getTransactionType() {
        return transactionType;
    }

    @JsonProperty("transactionType")
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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
