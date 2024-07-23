
package restassuredapi.pojo.viewvoucherC2cO2ctrfdetailsresp;

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
    "commValue",
    "tax1Value",
    "tax2Value",
    "tax3Value",
    "commType",
    "commRate",
    "tax1Type",
    "otfTypePctOrAMt",
    "cbcRate",
    "cbcAmount",
    "tax1Rate",
    "tax2Type",
    "tax2Rate",
    "tax3Type",
    "tax3Rate",
    "payableAmount",
    "netPayableAmount",
    "productCode",
    "senderDebitQty",
    "receiverCreditQty",
    "requestedQty"
})
public class TansferProductdetailList {

    @JsonProperty("commValue")
    private String commValue;
    @JsonProperty("tax1Value")
    private String tax1Value;
    @JsonProperty("tax2Value")
    private String tax2Value;
    @JsonProperty("tax3Value")
    private String tax3Value;
    @JsonProperty("commType")
    private String commType;
    @JsonProperty("commRate")
    private int commRate;
    @JsonProperty("tax1Type")
    private String tax1Type;
    @JsonProperty("otfTypePctOrAMt")
    private Object otfTypePctOrAMt;
    @JsonProperty("cbcRate")
    private int cbcRate;
    @JsonProperty("cbcAmount")
    private int cbcAmount;
    @JsonProperty("tax1Rate")
    private int tax1Rate;
    @JsonProperty("tax2Type")
    private String tax2Type;
    @JsonProperty("tax2Rate")
    private int tax2Rate;
    @JsonProperty("tax3Type")
    private String tax3Type;
    @JsonProperty("tax3Rate")
    private int tax3Rate;
    @JsonProperty("payableAmount")
    private String payableAmount;
    @JsonProperty("netPayableAmount")
    private String netPayableAmount;
    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("senderDebitQty")
    private String senderDebitQty;
    @JsonProperty("receiverCreditQty")
    private String receiverCreditQty;
    @JsonProperty("requestedQty")
    private String requestedQty;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public TansferProductdetailList() {
    }

    /**
     * 
     * @param commRate
     * @param tax3Type
     * @param tax1Type
     * @param tax1Rate
     * @param tax1Value
     * @param netPayableAmount
     * @param cbcAmount
     * @param payableAmount
     * @param commValue
     * @param receiverCreditQty
     * @param senderDebitQty
     * @param tax2Type
     * @param productCode
     * @param requestedQty
     * @param tax2Value
     * @param commType
     * @param tax2Rate
     * @param tax3Rate
     * @param tax3Value
     * @param cbcRate
     * @param otfTypePctOrAMt
     */
    public TansferProductdetailList(String commValue, String tax1Value, String tax2Value, String tax3Value, String commType, int commRate, String tax1Type, Object otfTypePctOrAMt, int cbcRate, int cbcAmount, int tax1Rate, String tax2Type, int tax2Rate, String tax3Type, int tax3Rate, String payableAmount, String netPayableAmount, String productCode, String senderDebitQty, String receiverCreditQty, String requestedQty) {
        super();
        this.commValue = commValue;
        this.tax1Value = tax1Value;
        this.tax2Value = tax2Value;
        this.tax3Value = tax3Value;
        this.commType = commType;
        this.commRate = commRate;
        this.tax1Type = tax1Type;
        this.otfTypePctOrAMt = otfTypePctOrAMt;
        this.cbcRate = cbcRate;
        this.cbcAmount = cbcAmount;
        this.tax1Rate = tax1Rate;
        this.tax2Type = tax2Type;
        this.tax2Rate = tax2Rate;
        this.tax3Type = tax3Type;
        this.tax3Rate = tax3Rate;
        this.payableAmount = payableAmount;
        this.netPayableAmount = netPayableAmount;
        this.productCode = productCode;
        this.senderDebitQty = senderDebitQty;
        this.receiverCreditQty = receiverCreditQty;
        this.requestedQty = requestedQty;
    }

    @JsonProperty("commValue")
    public String getCommValue() {
        return commValue;
    }

    @JsonProperty("commValue")
    public void setCommValue(String commValue) {
        this.commValue = commValue;
    }

    @JsonProperty("tax1Value")
    public String getTax1Value() {
        return tax1Value;
    }

    @JsonProperty("tax1Value")
    public void setTax1Value(String tax1Value) {
        this.tax1Value = tax1Value;
    }

    @JsonProperty("tax2Value")
    public String getTax2Value() {
        return tax2Value;
    }

    @JsonProperty("tax2Value")
    public void setTax2Value(String tax2Value) {
        this.tax2Value = tax2Value;
    }

    @JsonProperty("tax3Value")
    public String getTax3Value() {
        return tax3Value;
    }

    @JsonProperty("tax3Value")
    public void setTax3Value(String tax3Value) {
        this.tax3Value = tax3Value;
    }

    @JsonProperty("commType")
    public String getCommType() {
        return commType;
    }

    @JsonProperty("commType")
    public void setCommType(String commType) {
        this.commType = commType;
    }

    @JsonProperty("commRate")
    public int getCommRate() {
        return commRate;
    }

    @JsonProperty("commRate")
    public void setCommRate(int commRate) {
        this.commRate = commRate;
    }

    @JsonProperty("tax1Type")
    public String getTax1Type() {
        return tax1Type;
    }

    @JsonProperty("tax1Type")
    public void setTax1Type(String tax1Type) {
        this.tax1Type = tax1Type;
    }

    @JsonProperty("otfTypePctOrAMt")
    public Object getOtfTypePctOrAMt() {
        return otfTypePctOrAMt;
    }

    @JsonProperty("otfTypePctOrAMt")
    public void setOtfTypePctOrAMt(Object otfTypePctOrAMt) {
        this.otfTypePctOrAMt = otfTypePctOrAMt;
    }

    @JsonProperty("cbcRate")
    public int getCbcRate() {
        return cbcRate;
    }

    @JsonProperty("cbcRate")
    public void setCbcRate(int cbcRate) {
        this.cbcRate = cbcRate;
    }

    @JsonProperty("cbcAmount")
    public int getCbcAmount() {
        return cbcAmount;
    }

    @JsonProperty("cbcAmount")
    public void setCbcAmount(int cbcAmount) {
        this.cbcAmount = cbcAmount;
    }

    @JsonProperty("tax1Rate")
    public int getTax1Rate() {
        return tax1Rate;
    }

    @JsonProperty("tax1Rate")
    public void setTax1Rate(int tax1Rate) {
        this.tax1Rate = tax1Rate;
    }

    @JsonProperty("tax2Type")
    public String getTax2Type() {
        return tax2Type;
    }

    @JsonProperty("tax2Type")
    public void setTax2Type(String tax2Type) {
        this.tax2Type = tax2Type;
    }

    @JsonProperty("tax2Rate")
    public int getTax2Rate() {
        return tax2Rate;
    }

    @JsonProperty("tax2Rate")
    public void setTax2Rate(int tax2Rate) {
        this.tax2Rate = tax2Rate;
    }

    @JsonProperty("tax3Type")
    public String getTax3Type() {
        return tax3Type;
    }

    @JsonProperty("tax3Type")
    public void setTax3Type(String tax3Type) {
        this.tax3Type = tax3Type;
    }

    @JsonProperty("tax3Rate")
    public int getTax3Rate() {
        return tax3Rate;
    }

    @JsonProperty("tax3Rate")
    public void setTax3Rate(int tax3Rate) {
        this.tax3Rate = tax3Rate;
    }

    @JsonProperty("payableAmount")
    public String getPayableAmount() {
        return payableAmount;
    }

    @JsonProperty("payableAmount")
    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonProperty("netPayableAmount")
    public String getNetPayableAmount() {
        return netPayableAmount;
    }

    @JsonProperty("netPayableAmount")
    public void setNetPayableAmount(String netPayableAmount) {
        this.netPayableAmount = netPayableAmount;
    }

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("senderDebitQty")
    public String getSenderDebitQty() {
        return senderDebitQty;
    }

    @JsonProperty("senderDebitQty")
    public void setSenderDebitQty(String senderDebitQty) {
        this.senderDebitQty = senderDebitQty;
    }

    @JsonProperty("receiverCreditQty")
    public String getReceiverCreditQty() {
        return receiverCreditQty;
    }

    @JsonProperty("receiverCreditQty")
    public void setReceiverCreditQty(String receiverCreditQty) {
        this.receiverCreditQty = receiverCreditQty;
    }

    @JsonProperty("requestedQty")
    public String getRequestedQty() {
        return requestedQty;
    }

    @JsonProperty("requestedQty")
    public void setRequestedQty(String requestedQty) {
        this.requestedQty = requestedQty;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

 /*   @Override
    public String toString() {
        return new ToStringBuilder(this).append("commValue", commValue).append("tax1Value", tax1Value).append("tax2Value", tax2Value).append("tax3Value", tax3Value).append("commType", commType).append("commRate", commRate).append("tax1Type", tax1Type).append("otfTypePctOrAMt", otfTypePctOrAMt).append("cbcRate", cbcRate).append("cbcAmount", cbcAmount).append("tax1Rate", tax1Rate).append("tax2Type", tax2Type).append("tax2Rate", tax2Rate).append("tax3Type", tax3Type).append("tax3Rate", tax3Rate).append("payableAmount", payableAmount).append("netPayableAmount", netPayableAmount).append("productCode", productCode).append("senderDebitQty", senderDebitQty).append("receiverCreditQty", receiverCreditQty).append("requestedQty", requestedQty).append("additionalProperties", additionalProperties).toString();
    }*/

}
