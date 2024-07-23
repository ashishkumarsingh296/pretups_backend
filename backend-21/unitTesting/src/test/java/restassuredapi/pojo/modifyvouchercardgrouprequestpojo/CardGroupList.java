
package restassuredapi.pojo.modifyvouchercardgrouprequestpojo;

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
    "startRange",
    "endRange",
    "validityPeriodTypeDesc",
    "validityPeriod",
    "gracePeriod",
    "senderTax1Name",
    "senderTax1Type",
    "senderTax1RateAsString",
    "senderTax2Name",
    "senderTax2Type",
    "senderTax2RateAsString",
    "receiverTax1Name",
    "receiverTax1Type",
    "receiverTax1RateAsString",
    "receiverTax2Name",
    "receiverTax2Type",
    "receiverTax2RateAsString",
    "senderAccessFeeType",
    "senderAccessFeeRateAsString",
    "minSenderAccessFeeAsString",
    "maxSenderAccessFeeAsString",
    "receiverAccessFeeType",
    "receiverAccessFeeRateAsString",
    "minReceiverAccessFeeAsString",
    "maxReceiverAccessFeeAsString",
    "multipleOf",
    "bonusValidityValue",
    "online",
    "both",
    "receiverConvFactor",
    "status",
    "cosRequired",
    "inPromoAsString",
    "cardName",
    "cardGroupCode",
    "reversalPermitted",
    "reversalModifiedDate",
    "voucherTypeDesc",
    "voucherSegmentDesc",
    "voucherDenomination",
    "productName",
    "receiverTax3Name",
    "receiverTax3Type",
    "receiverTax3Rate",
    "receiverTax4Name",
    "receiverTax4Type",
    "receiverTax4Rate",
    "bonusAccList"
})
public class CardGroupList {

    @JsonProperty("startRange")
    private String startRange;
    @JsonProperty("endRange")
    private String endRange;
    @JsonProperty("validityPeriodTypeDesc")
    private String validityPeriodTypeDesc;
    @JsonProperty("validityPeriod")
    private String validityPeriod;
    @JsonProperty("gracePeriod")
    private String gracePeriod;
    @JsonProperty("senderTax1Name")
    private String senderTax1Name;
    @JsonProperty("senderTax1Type")
    private String senderTax1Type;
    @JsonProperty("senderTax1RateAsString")
    private String senderTax1RateAsString;
    @JsonProperty("senderTax2Name")
    private String senderTax2Name;
    @JsonProperty("senderTax2Type")
    private String senderTax2Type;
    @JsonProperty("senderTax2RateAsString")
    private String senderTax2RateAsString;
    @JsonProperty("receiverTax1Name")
    private String receiverTax1Name;
    @JsonProperty("receiverTax1Type")
    private String receiverTax1Type;
    @JsonProperty("receiverTax1RateAsString")
    private String receiverTax1RateAsString;
    @JsonProperty("receiverTax2Name")
    private String receiverTax2Name;
    @JsonProperty("receiverTax2Type")
    private String receiverTax2Type;
    @JsonProperty("receiverTax2RateAsString")
    private String receiverTax2RateAsString;
    @JsonProperty("senderAccessFeeType")
    private String senderAccessFeeType;
    @JsonProperty("senderAccessFeeRateAsString")
    private String senderAccessFeeRateAsString;
    @JsonProperty("minSenderAccessFeeAsString")
    private String minSenderAccessFeeAsString;
    @JsonProperty("maxSenderAccessFeeAsString")
    private String maxSenderAccessFeeAsString;
    @JsonProperty("receiverAccessFeeType")
    private String receiverAccessFeeType;
    @JsonProperty("receiverAccessFeeRateAsString")
    private String receiverAccessFeeRateAsString;
    @JsonProperty("minReceiverAccessFeeAsString")
    private String minReceiverAccessFeeAsString;
    @JsonProperty("maxReceiverAccessFeeAsString")
    private String maxReceiverAccessFeeAsString;
    @JsonProperty("multipleOf")
    private String multipleOf;
    @JsonProperty("bonusValidityValue")
    private String bonusValidityValue;
    @JsonProperty("online")
    private String online;
    @JsonProperty("both")
    private String both;
    @JsonProperty("receiverConvFactor")
    private String receiverConvFactor;
    @JsonProperty("status")
    private String status;
    @JsonProperty("cosRequired")
    private String cosRequired;
    @JsonProperty("inPromoAsString")
    private String inPromoAsString;
    @JsonProperty("cardName")
    private String cardName;
    @JsonProperty("cardGroupCode")
    private String cardGroupCode;
    @JsonProperty("reversalPermitted")
    private String reversalPermitted;
    @JsonProperty("reversalModifiedDate")
    private String reversalModifiedDate;
    @JsonProperty("voucherTypeDesc")
    private String voucherTypeDesc;
    @JsonProperty("voucherSegmentDesc")
    private String voucherSegmentDesc;
    @JsonProperty("voucherDenomination")
    private String voucherDenomination;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("receiverTax3Name")
    private String receiverTax3Name;
    @JsonProperty("receiverTax3Type")
    private String receiverTax3Type;
    @JsonProperty("receiverTax3Rate")
    private String receiverTax3Rate;
    @JsonProperty("receiverTax4Name")
    private String receiverTax4Name;
    @JsonProperty("receiverTax4Type")
    private String receiverTax4Type;
    @JsonProperty("receiverTax4Rate")
    private String receiverTax4Rate;
    @JsonProperty("bonusAccList")
    private List<BonusAccList> bonusAccList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("startRange")
    public String getStartRange() {
        return startRange;
    }

    @JsonProperty("startRange")
    public void setStartRange(String startRange) {
        this.startRange = startRange;
    }

    @JsonProperty("endRange")
    public String getEndRange() {
        return endRange;
    }

    @JsonProperty("endRange")
    public void setEndRange(String endRange) {
        this.endRange = endRange;
    }

    @JsonProperty("validityPeriodTypeDesc")
    public String getValidityPeriodTypeDesc() {
        return validityPeriodTypeDesc;
    }

    @JsonProperty("validityPeriodTypeDesc")
    public void setValidityPeriodTypeDesc(String validityPeriodTypeDesc) {
        this.validityPeriodTypeDesc = validityPeriodTypeDesc;
    }

    @JsonProperty("validityPeriod")
    public String getValidityPeriod() {
        return validityPeriod;
    }

    @JsonProperty("validityPeriod")
    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    @JsonProperty("gracePeriod")
    public String getGracePeriod() {
        return gracePeriod;
    }

    @JsonProperty("gracePeriod")
    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    @JsonProperty("senderTax1Name")
    public String getSenderTax1Name() {
        return senderTax1Name;
    }

    @JsonProperty("senderTax1Name")
    public void setSenderTax1Name(String senderTax1Name) {
        this.senderTax1Name = senderTax1Name;
    }

    @JsonProperty("senderTax1Type")
    public String getSenderTax1Type() {
        return senderTax1Type;
    }

    @JsonProperty("senderTax1Type")
    public void setSenderTax1Type(String senderTax1Type) {
        this.senderTax1Type = senderTax1Type;
    }

    @JsonProperty("senderTax1RateAsString")
    public String getSenderTax1RateAsString() {
        return senderTax1RateAsString;
    }

    @JsonProperty("senderTax1RateAsString")
    public void setSenderTax1RateAsString(String senderTax1RateAsString) {
        this.senderTax1RateAsString = senderTax1RateAsString;
    }

    @JsonProperty("senderTax2Name")
    public String getSenderTax2Name() {
        return senderTax2Name;
    }

    @JsonProperty("senderTax2Name")
    public void setSenderTax2Name(String senderTax2Name) {
        this.senderTax2Name = senderTax2Name;
    }

    @JsonProperty("senderTax2Type")
    public String getSenderTax2Type() {
        return senderTax2Type;
    }

    @JsonProperty("senderTax2Type")
    public void setSenderTax2Type(String senderTax2Type) {
        this.senderTax2Type = senderTax2Type;
    }

    @JsonProperty("senderTax2RateAsString")
    public String getSenderTax2RateAsString() {
        return senderTax2RateAsString;
    }

    @JsonProperty("senderTax2RateAsString")
    public void setSenderTax2RateAsString(String senderTax2RateAsString) {
        this.senderTax2RateAsString = senderTax2RateAsString;
    }

    @JsonProperty("receiverTax1Name")
    public String getReceiverTax1Name() {
        return receiverTax1Name;
    }

    @JsonProperty("receiverTax1Name")
    public void setReceiverTax1Name(String receiverTax1Name) {
        this.receiverTax1Name = receiverTax1Name;
    }

    @JsonProperty("receiverTax1Type")
    public String getReceiverTax1Type() {
        return receiverTax1Type;
    }

    @JsonProperty("receiverTax1Type")
    public void setReceiverTax1Type(String receiverTax1Type) {
        this.receiverTax1Type = receiverTax1Type;
    }

    @JsonProperty("receiverTax1RateAsString")
    public String getReceiverTax1RateAsString() {
        return receiverTax1RateAsString;
    }

    @JsonProperty("receiverTax1RateAsString")
    public void setReceiverTax1RateAsString(String receiverTax1RateAsString) {
        this.receiverTax1RateAsString = receiverTax1RateAsString;
    }

    @JsonProperty("receiverTax2Name")
    public String getReceiverTax2Name() {
        return receiverTax2Name;
    }

    @JsonProperty("receiverTax2Name")
    public void setReceiverTax2Name(String receiverTax2Name) {
        this.receiverTax2Name = receiverTax2Name;
    }

    @JsonProperty("receiverTax2Type")
    public String getReceiverTax2Type() {
        return receiverTax2Type;
    }

    @JsonProperty("receiverTax2Type")
    public void setReceiverTax2Type(String receiverTax2Type) {
        this.receiverTax2Type = receiverTax2Type;
    }

    @JsonProperty("receiverTax2RateAsString")
    public String getReceiverTax2RateAsString() {
        return receiverTax2RateAsString;
    }

    @JsonProperty("receiverTax2RateAsString")
    public void setReceiverTax2RateAsString(String receiverTax2RateAsString) {
        this.receiverTax2RateAsString = receiverTax2RateAsString;
    }

    @JsonProperty("senderAccessFeeType")
    public String getSenderAccessFeeType() {
        return senderAccessFeeType;
    }

    @JsonProperty("senderAccessFeeType")
    public void setSenderAccessFeeType(String senderAccessFeeType) {
        this.senderAccessFeeType = senderAccessFeeType;
    }

    @JsonProperty("senderAccessFeeRateAsString")
    public String getSenderAccessFeeRateAsString() {
        return senderAccessFeeRateAsString;
    }

    @JsonProperty("senderAccessFeeRateAsString")
    public void setSenderAccessFeeRateAsString(String senderAccessFeeRateAsString) {
        this.senderAccessFeeRateAsString = senderAccessFeeRateAsString;
    }

    @JsonProperty("minSenderAccessFeeAsString")
    public String getMinSenderAccessFeeAsString() {
        return minSenderAccessFeeAsString;
    }

    @JsonProperty("minSenderAccessFeeAsString")
    public void setMinSenderAccessFeeAsString(String minSenderAccessFeeAsString) {
        this.minSenderAccessFeeAsString = minSenderAccessFeeAsString;
    }

    @JsonProperty("maxSenderAccessFeeAsString")
    public String getMaxSenderAccessFeeAsString() {
        return maxSenderAccessFeeAsString;
    }

    @JsonProperty("maxSenderAccessFeeAsString")
    public void setMaxSenderAccessFeeAsString(String maxSenderAccessFeeAsString) {
        this.maxSenderAccessFeeAsString = maxSenderAccessFeeAsString;
    }

    @JsonProperty("receiverAccessFeeType")
    public String getReceiverAccessFeeType() {
        return receiverAccessFeeType;
    }

    @JsonProperty("receiverAccessFeeType")
    public void setReceiverAccessFeeType(String receiverAccessFeeType) {
        this.receiverAccessFeeType = receiverAccessFeeType;
    }

    @JsonProperty("receiverAccessFeeRateAsString")
    public String getReceiverAccessFeeRateAsString() {
        return receiverAccessFeeRateAsString;
    }

    @JsonProperty("receiverAccessFeeRateAsString")
    public void setReceiverAccessFeeRateAsString(String receiverAccessFeeRateAsString) {
        this.receiverAccessFeeRateAsString = receiverAccessFeeRateAsString;
    }

    @JsonProperty("minReceiverAccessFeeAsString")
    public String getMinReceiverAccessFeeAsString() {
        return minReceiverAccessFeeAsString;
    }

    @JsonProperty("minReceiverAccessFeeAsString")
    public void setMinReceiverAccessFeeAsString(String minReceiverAccessFeeAsString) {
        this.minReceiverAccessFeeAsString = minReceiverAccessFeeAsString;
    }

    @JsonProperty("maxReceiverAccessFeeAsString")
    public String getMaxReceiverAccessFeeAsString() {
        return maxReceiverAccessFeeAsString;
    }

    @JsonProperty("maxReceiverAccessFeeAsString")
    public void setMaxReceiverAccessFeeAsString(String maxReceiverAccessFeeAsString) {
        this.maxReceiverAccessFeeAsString = maxReceiverAccessFeeAsString;
    }

    @JsonProperty("multipleOf")
    public String getMultipleOf() {
        return multipleOf;
    }

    @JsonProperty("multipleOf")
    public void setMultipleOf(String multipleOf) {
        this.multipleOf = multipleOf;
    }

    @JsonProperty("bonusValidityValue")
    public String getBonusValidityValue() {
        return bonusValidityValue;
    }

    @JsonProperty("bonusValidityValue")
    public void setBonusValidityValue(String bonusValidityValue) {
        this.bonusValidityValue = bonusValidityValue;
    }

    @JsonProperty("online")
    public String getOnline() {
        return online;
    }

    @JsonProperty("online")
    public void setOnline(String online) {
        this.online = online;
    }

    @JsonProperty("both")
    public String getBoth() {
        return both;
    }

    @JsonProperty("both")
    public void setBoth(String both) {
        this.both = both;
    }

    @JsonProperty("receiverConvFactor")
    public String getReceiverConvFactor() {
        return receiverConvFactor;
    }

    @JsonProperty("receiverConvFactor")
    public void setReceiverConvFactor(String receiverConvFactor) {
        this.receiverConvFactor = receiverConvFactor;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("cosRequired")
    public String getCosRequired() {
        return cosRequired;
    }

    @JsonProperty("cosRequired")
    public void setCosRequired(String cosRequired) {
        this.cosRequired = cosRequired;
    }

    @JsonProperty("inPromoAsString")
    public String getInPromoAsString() {
        return inPromoAsString;
    }

    @JsonProperty("inPromoAsString")
    public void setInPromoAsString(String inPromoAsString) {
        this.inPromoAsString = inPromoAsString;
    }

    @JsonProperty("cardName")
    public String getCardName() {
        return cardName;
    }

    @JsonProperty("cardName")
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    @JsonProperty("cardGroupCode")
    public String getCardGroupCode() {
        return cardGroupCode;
    }

    @JsonProperty("cardGroupCode")
    public void setCardGroupCode(String cardGroupCode) {
        this.cardGroupCode = cardGroupCode;
    }

    @JsonProperty("reversalPermitted")
    public String getReversalPermitted() {
        return reversalPermitted;
    }

    @JsonProperty("reversalPermitted")
    public void setReversalPermitted(String reversalPermitted) {
        this.reversalPermitted = reversalPermitted;
    }

    @JsonProperty("reversalModifiedDate")
    public String getReversalModifiedDate() {
        return reversalModifiedDate;
    }

    @JsonProperty("reversalModifiedDate")
    public void setReversalModifiedDate(String reversalModifiedDate) {
        this.reversalModifiedDate = reversalModifiedDate;
    }

    @JsonProperty("voucherTypeDesc")
    public String getVoucherTypeDesc() {
        return voucherTypeDesc;
    }

    @JsonProperty("voucherTypeDesc")
    public void setVoucherTypeDesc(String voucherTypeDesc) {
        this.voucherTypeDesc = voucherTypeDesc;
    }

    @JsonProperty("voucherSegmentDesc")
    public String getVoucherSegmentDesc() {
        return voucherSegmentDesc;
    }

    @JsonProperty("voucherSegmentDesc")
    public void setVoucherSegmentDesc(String voucherSegmentDesc) {
        this.voucherSegmentDesc = voucherSegmentDesc;
    }

    @JsonProperty("voucherDenomination")
    public String getVoucherDenomination() {
        return voucherDenomination;
    }

    @JsonProperty("voucherDenomination")
    public void setVoucherDenomination(String voucherDenomination) {
        this.voucherDenomination = voucherDenomination;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("receiverTax3Name")
    public String getReceiverTax3Name() {
        return receiverTax3Name;
    }

    @JsonProperty("receiverTax3Name")
    public void setReceiverTax3Name(String receiverTax3Name) {
        this.receiverTax3Name = receiverTax3Name;
    }

    @JsonProperty("receiverTax3Type")
    public String getReceiverTax3Type() {
        return receiverTax3Type;
    }

    @JsonProperty("receiverTax3Type")
    public void setReceiverTax3Type(String receiverTax3Type) {
        this.receiverTax3Type = receiverTax3Type;
    }

    @JsonProperty("receiverTax3Rate")
    public String getReceiverTax3Rate() {
        return receiverTax3Rate;
    }

    @JsonProperty("receiverTax3Rate")
    public void setReceiverTax3Rate(String receiverTax3Rate) {
        this.receiverTax3Rate = receiverTax3Rate;
    }

    @JsonProperty("receiverTax4Name")
    public String getReceiverTax4Name() {
        return receiverTax4Name;
    }

    @JsonProperty("receiverTax4Name")
    public void setReceiverTax4Name(String receiverTax4Name) {
        this.receiverTax4Name = receiverTax4Name;
    }

    @JsonProperty("receiverTax4Type")
    public String getReceiverTax4Type() {
        return receiverTax4Type;
    }

    @JsonProperty("receiverTax4Type")
    public void setReceiverTax4Type(String receiverTax4Type) {
        this.receiverTax4Type = receiverTax4Type;
    }

    @JsonProperty("receiverTax4Rate")
    public String getReceiverTax4Rate() {
        return receiverTax4Rate;
    }

    @JsonProperty("receiverTax4Rate")
    public void setReceiverTax4Rate(String receiverTax4Rate) {
        this.receiverTax4Rate = receiverTax4Rate;
    }

    @JsonProperty("bonusAccList")
    public List<BonusAccList> getBonusAccList() {
        return bonusAccList;
    }

    @JsonProperty("bonusAccList")
    public void setBonusAccList(List<BonusAccList> bonusAccList) {
        this.bonusAccList = bonusAccList;
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
