
package restassuredapi.pojo.viewselfcommenquiryresponsepojo;

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
    "paymentMode",
    "transactionType",
    "transactionTypeDesc",
    "paymentModeDesc",
    "productCode",
    "transferMultipleOffInDouble",
    "discountRateAsString",
    "minTransferValueAsString",
    "transferMultipleOffAsString",
    "maxTransferValueAsString",
    "productCodeDesc",
    "commProfileProductID",
    "commProfileSetID",
    "transferMultipleOff",
    "taxOnFOCApplicable",
    "version",
    "discountRate",
    "discountType",
    "maxTransferValue",
    "minTransferValue",
    "taxOnChannelTransfer"
})
public class CommissionProfileProductVO {

    @JsonProperty("paymentMode")
    private String paymentMode;
    @JsonProperty("transactionType")
    private String transactionType;
    @JsonProperty("transactionTypeDesc")
    private String transactionTypeDesc;
    @JsonProperty("paymentModeDesc")
    private String paymentModeDesc;
    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("transferMultipleOffInDouble")
    private Long transferMultipleOffInDouble;
    @JsonProperty("discountRateAsString")
    private String discountRateAsString;
    @JsonProperty("minTransferValueAsString")
    private String minTransferValueAsString;
    @JsonProperty("transferMultipleOffAsString")
    private String transferMultipleOffAsString;
    @JsonProperty("maxTransferValueAsString")
    private String maxTransferValueAsString;
    @JsonProperty("productCodeDesc")
    private String productCodeDesc;
    @JsonProperty("commProfileProductID")
    private String commProfileProductID;
    @JsonProperty("commProfileSetID")
    private String commProfileSetID;
    @JsonProperty("transferMultipleOff")
    private Long transferMultipleOff;
    @JsonProperty("taxOnFOCApplicable")
    private String taxOnFOCApplicable;
    @JsonProperty("version")
    private String version;
    @JsonProperty("discountRate")
    private Long discountRate;
    @JsonProperty("discountType")
    private String discountType;
    @JsonProperty("maxTransferValue")
    private Long maxTransferValue;
    @JsonProperty("minTransferValue")
    private Long minTransferValue;
    @JsonProperty("taxOnChannelTransfer")
    private String taxOnChannelTransfer;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("paymentMode")
    public String getPaymentMode() {
        return paymentMode;
    }

    @JsonProperty("paymentMode")
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    @JsonProperty("transactionType")
    public String getTransactionType() {
        return transactionType;
    }

    @JsonProperty("transactionType")
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @JsonProperty("transactionTypeDesc")
    public String getTransactionTypeDesc() {
        return transactionTypeDesc;
    }

    @JsonProperty("transactionTypeDesc")
    public void setTransactionTypeDesc(String transactionTypeDesc) {
        this.transactionTypeDesc = transactionTypeDesc;
    }

    @JsonProperty("paymentModeDesc")
    public String getPaymentModeDesc() {
        return paymentModeDesc;
    }

    @JsonProperty("paymentModeDesc")
    public void setPaymentModeDesc(String paymentModeDesc) {
        this.paymentModeDesc = paymentModeDesc;
    }

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("transferMultipleOffInDouble")
    public Long getTransferMultipleOffInDouble() {
        return transferMultipleOffInDouble;
    }

    @JsonProperty("transferMultipleOffInDouble")
    public void setTransferMultipleOffInDouble(Long transferMultipleOffInDouble) {
        this.transferMultipleOffInDouble = transferMultipleOffInDouble;
    }

    @JsonProperty("discountRateAsString")
    public String getDiscountRateAsString() {
        return discountRateAsString;
    }

    @JsonProperty("discountRateAsString")
    public void setDiscountRateAsString(String discountRateAsString) {
        this.discountRateAsString = discountRateAsString;
    }

    @JsonProperty("minTransferValueAsString")
    public String getMinTransferValueAsString() {
        return minTransferValueAsString;
    }

    @JsonProperty("minTransferValueAsString")
    public void setMinTransferValueAsString(String minTransferValueAsString) {
        this.minTransferValueAsString = minTransferValueAsString;
    }

    @JsonProperty("transferMultipleOffAsString")
    public String getTransferMultipleOffAsString() {
        return transferMultipleOffAsString;
    }

    @JsonProperty("transferMultipleOffAsString")
    public void setTransferMultipleOffAsString(String transferMultipleOffAsString) {
        this.transferMultipleOffAsString = transferMultipleOffAsString;
    }

    @JsonProperty("maxTransferValueAsString")
    public String getMaxTransferValueAsString() {
        return maxTransferValueAsString;
    }

    @JsonProperty("maxTransferValueAsString")
    public void setMaxTransferValueAsString(String maxTransferValueAsString) {
        this.maxTransferValueAsString = maxTransferValueAsString;
    }

    @JsonProperty("productCodeDesc")
    public String getProductCodeDesc() {
        return productCodeDesc;
    }

    @JsonProperty("productCodeDesc")
    public void setProductCodeDesc(String productCodeDesc) {
        this.productCodeDesc = productCodeDesc;
    }

    @JsonProperty("commProfileProductID")
    public String getCommProfileProductID() {
        return commProfileProductID;
    }

    @JsonProperty("commProfileProductID")
    public void setCommProfileProductID(String commProfileProductID) {
        this.commProfileProductID = commProfileProductID;
    }

    @JsonProperty("commProfileSetID")
    public String getCommProfileSetID() {
        return commProfileSetID;
    }

    @JsonProperty("commProfileSetID")
    public void setCommProfileSetID(String commProfileSetID) {
        this.commProfileSetID = commProfileSetID;
    }

    @JsonProperty("transferMultipleOff")
    public Long getTransferMultipleOff() {
        return transferMultipleOff;
    }

    @JsonProperty("transferMultipleOff")
    public void setTransferMultipleOff(Long transferMultipleOff) {
        this.transferMultipleOff = transferMultipleOff;
    }

    @JsonProperty("taxOnFOCApplicable")
    public String getTaxOnFOCApplicable() {
        return taxOnFOCApplicable;
    }

    @JsonProperty("taxOnFOCApplicable")
    public void setTaxOnFOCApplicable(String taxOnFOCApplicable) {
        this.taxOnFOCApplicable = taxOnFOCApplicable;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("discountRate")
    public Long getDiscountRate() {
        return discountRate;
    }

    @JsonProperty("discountRate")
    public void setDiscountRate(Long discountRate) {
        this.discountRate = discountRate;
    }

    @JsonProperty("discountType")
    public String getDiscountType() {
        return discountType;
    }

    @JsonProperty("discountType")
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    @JsonProperty("maxTransferValue")
    public Long getMaxTransferValue() {
        return maxTransferValue;
    }

    @JsonProperty("maxTransferValue")
    public void setMaxTransferValue(Long maxTransferValue) {
        this.maxTransferValue = maxTransferValue;
    }

    @JsonProperty("minTransferValue")
    public Long getMinTransferValue() {
        return minTransferValue;
    }

    @JsonProperty("minTransferValue")
    public void setMinTransferValue(Long minTransferValue) {
        this.minTransferValue = minTransferValue;
    }

    @JsonProperty("taxOnChannelTransfer")
    public String getTaxOnChannelTransfer() {
        return taxOnChannelTransfer;
    }

    @JsonProperty("taxOnChannelTransfer")
    public void setTaxOnChannelTransfer(String taxOnChannelTransfer) {
        this.taxOnChannelTransfer = taxOnChannelTransfer;
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
