
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
    "productCode",
    "otfDetails",
    "otfDetailsSize",
    "otfmodify",
    "rowIndex",
    "otfApplicableFromStr",
    "otfApplicableToStr",
    "otfApplicableFrom",
    "otfApplicableTo",
    "otfType",
    "otfTimeSlab",
    "otfValueAsString",
    "otfRateAsString",
    "otfTypePctOrAMt",
    "otfValue",
    "otfRate",
    "baseCommProfileOTFDetailID",
    "transactionType",
    "paymentMode",
    "origOtfApplicableToStr",
    "baseCommProfileDetailID",
    "commRateAsString",
    "tax1RateAsString",
    "tax2RateAsString",
    "tax3RateAsString",
    "startRange",
    "endRange",
    "commType",
    "commProfileProductsID",
    "startRangeAsString",
    "endRangeAsString",
    "tax1Type",
    "tax1Rate",
    "tax2Type",
    "tax2Rate",
    "tax3Type",
    "tax3Rate",
    "commRate",
    "commProfileDetailID"
})
public class SlabsList {

    @JsonProperty("productCode")
    private Object productCode;
    @JsonProperty("otfDetails")
    private Object otfDetails;
    @JsonProperty("otfDetailsSize")
    private String otfDetailsSize;
    @JsonProperty("otfmodify")
    private Boolean otfmodify;
    @JsonProperty("rowIndex")
    private Long rowIndex;
    @JsonProperty("otfApplicableFromStr")
    private Object otfApplicableFromStr;
    @JsonProperty("otfApplicableToStr")
    private Object otfApplicableToStr;
    @JsonProperty("otfApplicableFrom")
    private Object otfApplicableFrom;
    @JsonProperty("otfApplicableTo")
    private Object otfApplicableTo;
    @JsonProperty("otfType")
    private Object otfType;
    @JsonProperty("otfTimeSlab")
    private Object otfTimeSlab;
    @JsonProperty("otfValueAsString")
    private Object otfValueAsString;
    @JsonProperty("otfRateAsString")
    private Object otfRateAsString;
    @JsonProperty("otfTypePctOrAMt")
    private Object otfTypePctOrAMt;
    @JsonProperty("otfValue")
    private Long otfValue;
    @JsonProperty("otfRate")
    private Long otfRate;
    @JsonProperty("baseCommProfileOTFDetailID")
    private Object baseCommProfileOTFDetailID;
    @JsonProperty("transactionType")
    private Object transactionType;
    @JsonProperty("paymentMode")
    private Object paymentMode;
    @JsonProperty("origOtfApplicableToStr")
    private Object origOtfApplicableToStr;
    @JsonProperty("baseCommProfileDetailID")
    private Object baseCommProfileDetailID;
    @JsonProperty("commRateAsString")
    private String commRateAsString;
    @JsonProperty("tax1RateAsString")
    private String tax1RateAsString;
    @JsonProperty("tax2RateAsString")
    private String tax2RateAsString;
    @JsonProperty("tax3RateAsString")
    private String tax3RateAsString;
    @JsonProperty("startRange")
    private Long startRange;
    @JsonProperty("endRange")
    private Long endRange;
    @JsonProperty("commType")
    private String commType;
    @JsonProperty("commProfileProductsID")
    private String commProfileProductsID;
    @JsonProperty("startRangeAsString")
    private String startRangeAsString;
    @JsonProperty("endRangeAsString")
    private String endRangeAsString;
    @JsonProperty("tax1Type")
    private String tax1Type;
    @JsonProperty("tax1Rate")
    private Long tax1Rate;
    @JsonProperty("tax2Type")
    private String tax2Type;
    @JsonProperty("tax2Rate")
    private Long tax2Rate;
    @JsonProperty("tax3Type")
    private String tax3Type;
    @JsonProperty("tax3Rate")
    private Long tax3Rate;
    @JsonProperty("commRate")
    private Long commRate;
    @JsonProperty("commProfileDetailID")
    private String commProfileDetailID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("productCode")
    public Object getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(Object productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("otfDetails")
    public Object getOtfDetails() {
        return otfDetails;
    }

    @JsonProperty("otfDetails")
    public void setOtfDetails(Object otfDetails) {
        this.otfDetails = otfDetails;
    }

    @JsonProperty("otfDetailsSize")
    public String getOtfDetailsSize() {
        return otfDetailsSize;
    }

    @JsonProperty("otfDetailsSize")
    public void setOtfDetailsSize(String otfDetailsSize) {
        this.otfDetailsSize = otfDetailsSize;
    }

    @JsonProperty("otfmodify")
    public Boolean getOtfmodify() {
        return otfmodify;
    }

    @JsonProperty("otfmodify")
    public void setOtfmodify(Boolean otfmodify) {
        this.otfmodify = otfmodify;
    }

    @JsonProperty("rowIndex")
    public Long getRowIndex() {
        return rowIndex;
    }

    @JsonProperty("rowIndex")
    public void setRowIndex(Long rowIndex) {
        this.rowIndex = rowIndex;
    }

    @JsonProperty("otfApplicableFromStr")
    public Object getOtfApplicableFromStr() {
        return otfApplicableFromStr;
    }

    @JsonProperty("otfApplicableFromStr")
    public void setOtfApplicableFromStr(Object otfApplicableFromStr) {
        this.otfApplicableFromStr = otfApplicableFromStr;
    }

    @JsonProperty("otfApplicableToStr")
    public Object getOtfApplicableToStr() {
        return otfApplicableToStr;
    }

    @JsonProperty("otfApplicableToStr")
    public void setOtfApplicableToStr(Object otfApplicableToStr) {
        this.otfApplicableToStr = otfApplicableToStr;
    }

    @JsonProperty("otfApplicableFrom")
    public Object getOtfApplicableFrom() {
        return otfApplicableFrom;
    }

    @JsonProperty("otfApplicableFrom")
    public void setOtfApplicableFrom(Object otfApplicableFrom) {
        this.otfApplicableFrom = otfApplicableFrom;
    }

    @JsonProperty("otfApplicableTo")
    public Object getOtfApplicableTo() {
        return otfApplicableTo;
    }

    @JsonProperty("otfApplicableTo")
    public void setOtfApplicableTo(Object otfApplicableTo) {
        this.otfApplicableTo = otfApplicableTo;
    }

    @JsonProperty("otfType")
    public Object getOtfType() {
        return otfType;
    }

    @JsonProperty("otfType")
    public void setOtfType(Object otfType) {
        this.otfType = otfType;
    }

    @JsonProperty("otfTimeSlab")
    public Object getOtfTimeSlab() {
        return otfTimeSlab;
    }

    @JsonProperty("otfTimeSlab")
    public void setOtfTimeSlab(Object otfTimeSlab) {
        this.otfTimeSlab = otfTimeSlab;
    }

    @JsonProperty("otfValueAsString")
    public Object getOtfValueAsString() {
        return otfValueAsString;
    }

    @JsonProperty("otfValueAsString")
    public void setOtfValueAsString(Object otfValueAsString) {
        this.otfValueAsString = otfValueAsString;
    }

    @JsonProperty("otfRateAsString")
    public Object getOtfRateAsString() {
        return otfRateAsString;
    }

    @JsonProperty("otfRateAsString")
    public void setOtfRateAsString(Object otfRateAsString) {
        this.otfRateAsString = otfRateAsString;
    }

    @JsonProperty("otfTypePctOrAMt")
    public Object getOtfTypePctOrAMt() {
        return otfTypePctOrAMt;
    }

    @JsonProperty("otfTypePctOrAMt")
    public void setOtfTypePctOrAMt(Object otfTypePctOrAMt) {
        this.otfTypePctOrAMt = otfTypePctOrAMt;
    }

    @JsonProperty("otfValue")
    public Long getOtfValue() {
        return otfValue;
    }

    @JsonProperty("otfValue")
    public void setOtfValue(Long otfValue) {
        this.otfValue = otfValue;
    }

    @JsonProperty("otfRate")
    public Long getOtfRate() {
        return otfRate;
    }

    @JsonProperty("otfRate")
    public void setOtfRate(Long otfRate) {
        this.otfRate = otfRate;
    }

    @JsonProperty("baseCommProfileOTFDetailID")
    public Object getBaseCommProfileOTFDetailID() {
        return baseCommProfileOTFDetailID;
    }

    @JsonProperty("baseCommProfileOTFDetailID")
    public void setBaseCommProfileOTFDetailID(Object baseCommProfileOTFDetailID) {
        this.baseCommProfileOTFDetailID = baseCommProfileOTFDetailID;
    }

    @JsonProperty("transactionType")
    public Object getTransactionType() {
        return transactionType;
    }

    @JsonProperty("transactionType")
    public void setTransactionType(Object transactionType) {
        this.transactionType = transactionType;
    }

    @JsonProperty("paymentMode")
    public Object getPaymentMode() {
        return paymentMode;
    }

    @JsonProperty("paymentMode")
    public void setPaymentMode(Object paymentMode) {
        this.paymentMode = paymentMode;
    }

    @JsonProperty("origOtfApplicableToStr")
    public Object getOrigOtfApplicableToStr() {
        return origOtfApplicableToStr;
    }

    @JsonProperty("origOtfApplicableToStr")
    public void setOrigOtfApplicableToStr(Object origOtfApplicableToStr) {
        this.origOtfApplicableToStr = origOtfApplicableToStr;
    }

    @JsonProperty("baseCommProfileDetailID")
    public Object getBaseCommProfileDetailID() {
        return baseCommProfileDetailID;
    }

    @JsonProperty("baseCommProfileDetailID")
    public void setBaseCommProfileDetailID(Object baseCommProfileDetailID) {
        this.baseCommProfileDetailID = baseCommProfileDetailID;
    }

    @JsonProperty("commRateAsString")
    public String getCommRateAsString() {
        return commRateAsString;
    }

    @JsonProperty("commRateAsString")
    public void setCommRateAsString(String commRateAsString) {
        this.commRateAsString = commRateAsString;
    }

    @JsonProperty("tax1RateAsString")
    public String getTax1RateAsString() {
        return tax1RateAsString;
    }

    @JsonProperty("tax1RateAsString")
    public void setTax1RateAsString(String tax1RateAsString) {
        this.tax1RateAsString = tax1RateAsString;
    }

    @JsonProperty("tax2RateAsString")
    public String getTax2RateAsString() {
        return tax2RateAsString;
    }

    @JsonProperty("tax2RateAsString")
    public void setTax2RateAsString(String tax2RateAsString) {
        this.tax2RateAsString = tax2RateAsString;
    }

    @JsonProperty("tax3RateAsString")
    public String getTax3RateAsString() {
        return tax3RateAsString;
    }

    @JsonProperty("tax3RateAsString")
    public void setTax3RateAsString(String tax3RateAsString) {
        this.tax3RateAsString = tax3RateAsString;
    }

    @JsonProperty("startRange")
    public Long getStartRange() {
        return startRange;
    }

    @JsonProperty("startRange")
    public void setStartRange(Long startRange) {
        this.startRange = startRange;
    }

    @JsonProperty("endRange")
    public Long getEndRange() {
        return endRange;
    }

    @JsonProperty("endRange")
    public void setEndRange(Long endRange) {
        this.endRange = endRange;
    }

    @JsonProperty("commType")
    public String getCommType() {
        return commType;
    }

    @JsonProperty("commType")
    public void setCommType(String commType) {
        this.commType = commType;
    }

    @JsonProperty("commProfileProductsID")
    public String getCommProfileProductsID() {
        return commProfileProductsID;
    }

    @JsonProperty("commProfileProductsID")
    public void setCommProfileProductsID(String commProfileProductsID) {
        this.commProfileProductsID = commProfileProductsID;
    }

    @JsonProperty("startRangeAsString")
    public String getStartRangeAsString() {
        return startRangeAsString;
    }

    @JsonProperty("startRangeAsString")
    public void setStartRangeAsString(String startRangeAsString) {
        this.startRangeAsString = startRangeAsString;
    }

    @JsonProperty("endRangeAsString")
    public String getEndRangeAsString() {
        return endRangeAsString;
    }

    @JsonProperty("endRangeAsString")
    public void setEndRangeAsString(String endRangeAsString) {
        this.endRangeAsString = endRangeAsString;
    }

    @JsonProperty("tax1Type")
    public String getTax1Type() {
        return tax1Type;
    }

    @JsonProperty("tax1Type")
    public void setTax1Type(String tax1Type) {
        this.tax1Type = tax1Type;
    }

    @JsonProperty("tax1Rate")
    public Long getTax1Rate() {
        return tax1Rate;
    }

    @JsonProperty("tax1Rate")
    public void setTax1Rate(Long tax1Rate) {
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
    public Long getTax2Rate() {
        return tax2Rate;
    }

    @JsonProperty("tax2Rate")
    public void setTax2Rate(Long tax2Rate) {
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
    public Long getTax3Rate() {
        return tax3Rate;
    }

    @JsonProperty("tax3Rate")
    public void setTax3Rate(Long tax3Rate) {
        this.tax3Rate = tax3Rate;
    }

    @JsonProperty("commRate")
    public Long getCommRate() {
        return commRate;
    }

    @JsonProperty("commRate")
    public void setCommRate(Long commRate) {
        this.commRate = commRate;
    }

    @JsonProperty("commProfileDetailID")
    public String getCommProfileDetailID() {
        return commProfileDetailID;
    }

    @JsonProperty("commProfileDetailID")
    public void setCommProfileDetailID(String commProfileDetailID) {
        this.commProfileDetailID = commProfileDetailID;
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
