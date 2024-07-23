
package restassuredapi.pojo.viewvouchercardgroupresponsepojo;

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
    "voucherType",
    "voucherTypeDesc",
    "voucherSegment",
    "voucherSegmentDesc",
    "voucherProductId",
    "cardGroupType",
    "voucherDenomination",
    "productName",
    "receiverTax1RateAsString",
    "receiverTax2RateAsString",
    "senderTax1Rate",
    "senderTax1RateAsString",
    "senderTax1Type",
    "senderTax2Rate",
    "senderTax2RateAsString",
    "senderTax2Type",
    "both",
    "validityPeriodType",
    "validityPeriod",
    "gracePeriod",
    "senderTax1Name",
    "senderTax2Name",
    "receiverTax1Name",
    "receiverTax2Name",
    "receiverAccessFeeType",
    "receiverAccessFeeRate",
    "minSenderAccessFee",
    "maxSenderAccessFee",
    "minReceiverAccessFee",
    "maxReceiverAccessFee",
    "multipleOf",
    "cardGroupSubServiceId",
    "cardGroupSubServiceIdDesc",
    "serviceTypeId",
    "bonusValidityValue",
    "cardName",
    "reversalModifiedDate",
    "receiverTax3Name",
    "receiverTax4Name",
    "bonusAccList",
    "reversalModifiedDateAsString",
    "applicableFrom",
    "serviceTypeSelector",
    "receiverTax4RateAsString",
    "minReceiverAccessFeeAsString",
    "minSenderAccessFeeAsString",
    "receiverAccessFeeRateAsString",
    "multipleOfAsString",
    "maxReceiverAccessFeeAsString",
    "bonus1validity",
    "bonus2validity",
    "bonusTalkTimeBundleType",
    "bonusTalkTimeValidity",
    "bonus1",
    "maxSenderAccessFeeAsString",
    "bonus2",
    "cardGroupList",
    "editDetail",
    "bonusTalkTimeType",
    "bonusTalkTimeConvFactor",
    "bonusTalktimevalidity",
    "inPromoAsString",
    "receiverTax3RateAsString",
    "networkCode",
    "oldApplicableFrom",
    "applicableFromAsString",
    "rowIndex",
    "startRangeAsString",
    "endRangeAsString",
    "cardGroupSetID",
    "cardGroupSetName",
    "lastVersion",
    "serviceTypeDesc",
    "setType",
    "setTypeName",
    "bonusTalkTimeRate",
    "transferValue",
    "bonusTalkTimeValue",
    "receiverTax3Rate",
    "receiverTax4Rate",
    "cardGroupCode",
    "cardGroupID",
    "receiverTax1Type",
    "receiverTax1Rate",
    "receiverTax2Type",
    "receiverTax2Rate",
    "receiverTax3Type",
    "receiverTax4Type",
    "online",
    "reversalPermitted",
    "senderConvFactor",
    "receiverConvFactor",
    "senderAccessFeeRate",
    "senderAccessFeeType",
    "inPromo",
    "cosRequired",
    "startRange",
    "endRange",
    "senderAccessFeeRateAsString",
    "validityPeriodAsString",
    "status",
    "version"
})
public class DataObject {

    @JsonProperty("voucherType")
    private String voucherType;
    @JsonProperty("voucherTypeDesc")
    private Object voucherTypeDesc;
    @JsonProperty("voucherSegment")
    private String voucherSegment;
    @JsonProperty("voucherSegmentDesc")
    private Object voucherSegmentDesc;
    @JsonProperty("voucherProductId")
    private String voucherProductId;
    @JsonProperty("cardGroupType")
    private Object cardGroupType;
    @JsonProperty("voucherDenomination")
    private Object voucherDenomination;
    @JsonProperty("productName")
    private Object productName;
    @JsonProperty("receiverTax1RateAsString")
    private String receiverTax1RateAsString;
    @JsonProperty("receiverTax2RateAsString")
    private String receiverTax2RateAsString;
    @JsonProperty("senderTax1Rate")
    private Integer senderTax1Rate;
    @JsonProperty("senderTax1RateAsString")
    private String senderTax1RateAsString;
    @JsonProperty("senderTax1Type")
    private String senderTax1Type;
    @JsonProperty("senderTax2Rate")
    private Integer senderTax2Rate;
    @JsonProperty("senderTax2RateAsString")
    private String senderTax2RateAsString;
    @JsonProperty("senderTax2Type")
    private String senderTax2Type;
    @JsonProperty("both")
    private String both;
    @JsonProperty("validityPeriodType")
    private String validityPeriodType;
    @JsonProperty("validityPeriod")
    private Integer validityPeriod;
    @JsonProperty("gracePeriod")
    private Integer gracePeriod;
    @JsonProperty("senderTax1Name")
    private String senderTax1Name;
    @JsonProperty("senderTax2Name")
    private String senderTax2Name;
    @JsonProperty("receiverTax1Name")
    private String receiverTax1Name;
    @JsonProperty("receiverTax2Name")
    private String receiverTax2Name;
    @JsonProperty("receiverAccessFeeType")
    private String receiverAccessFeeType;
    @JsonProperty("receiverAccessFeeRate")
    private Integer receiverAccessFeeRate;
    @JsonProperty("minSenderAccessFee")
    private Integer minSenderAccessFee;
    @JsonProperty("maxSenderAccessFee")
    private Integer maxSenderAccessFee;
    @JsonProperty("minReceiverAccessFee")
    private Integer minReceiverAccessFee;
    @JsonProperty("maxReceiverAccessFee")
    private Integer maxReceiverAccessFee;
    @JsonProperty("multipleOf")
    private Integer multipleOf;
    @JsonProperty("cardGroupSubServiceId")
    private String cardGroupSubServiceId;
    @JsonProperty("cardGroupSubServiceIdDesc")
    private String cardGroupSubServiceIdDesc;
    @JsonProperty("serviceTypeId")
    private String serviceTypeId;
    @JsonProperty("bonusValidityValue")
    private Integer bonusValidityValue;
    @JsonProperty("cardName")
    private String cardName;
    @JsonProperty("reversalModifiedDate")
    private Object reversalModifiedDate;
    @JsonProperty("receiverTax3Name")
    private Object receiverTax3Name;
    @JsonProperty("receiverTax4Name")
    private Object receiverTax4Name;
    @JsonProperty("bonusAccList")
    private List<BonusAccList> bonusAccList = null;
    @JsonProperty("reversalModifiedDateAsString")
    private Object reversalModifiedDateAsString;
    @JsonProperty("applicableFrom")
    private Object applicableFrom;
    @JsonProperty("serviceTypeSelector")
    private Object serviceTypeSelector;
    @JsonProperty("receiverTax4RateAsString")
    private String receiverTax4RateAsString;
    @JsonProperty("minReceiverAccessFeeAsString")
    private String minReceiverAccessFeeAsString;
    @JsonProperty("minSenderAccessFeeAsString")
    private String minSenderAccessFeeAsString;
    @JsonProperty("receiverAccessFeeRateAsString")
    private String receiverAccessFeeRateAsString;
    @JsonProperty("multipleOfAsString")
    private String multipleOfAsString;
    @JsonProperty("maxReceiverAccessFeeAsString")
    private String maxReceiverAccessFeeAsString;
    @JsonProperty("bonus1validity")
    private Integer bonus1validity;
    @JsonProperty("bonus2validity")
    private Integer bonus2validity;
    @JsonProperty("bonusTalkTimeBundleType")
    private Object bonusTalkTimeBundleType;
    @JsonProperty("bonusTalkTimeValidity")
    private Object bonusTalkTimeValidity;
    @JsonProperty("bonus1")
    private Integer bonus1;
    @JsonProperty("maxSenderAccessFeeAsString")
    private String maxSenderAccessFeeAsString;
    @JsonProperty("bonus2")
    private Integer bonus2;
    @JsonProperty("cardGroupList")
    private Object cardGroupList;
    @JsonProperty("editDetail")
    private Object editDetail;
    @JsonProperty("bonusTalkTimeType")
    private Object bonusTalkTimeType;
    @JsonProperty("bonusTalkTimeConvFactor")
    private Integer bonusTalkTimeConvFactor;
    @JsonProperty("bonusTalktimevalidity")
    private Integer bonusTalktimevalidity;
    @JsonProperty("inPromoAsString")
    private String inPromoAsString;
    @JsonProperty("receiverTax3RateAsString")
    private String receiverTax3RateAsString;
    @JsonProperty("networkCode")
    private Object networkCode;
    @JsonProperty("oldApplicableFrom")
    private Integer oldApplicableFrom;
    @JsonProperty("applicableFromAsString")
    private String applicableFromAsString;
    @JsonProperty("rowIndex")
    private Integer rowIndex;
    @JsonProperty("startRangeAsString")
    private String startRangeAsString;
    @JsonProperty("endRangeAsString")
    private String endRangeAsString;
    @JsonProperty("cardGroupSetID")
    private String cardGroupSetID;
    @JsonProperty("cardGroupSetName")
    private String cardGroupSetName;
    @JsonProperty("lastVersion")
    private Object lastVersion;
    @JsonProperty("serviceTypeDesc")
    private String serviceTypeDesc;
    @JsonProperty("setType")
    private String setType;
    @JsonProperty("setTypeName")
    private String setTypeName;
    @JsonProperty("bonusTalkTimeRate")
    private Integer bonusTalkTimeRate;
    @JsonProperty("transferValue")
    private Integer transferValue;
    @JsonProperty("bonusTalkTimeValue")
    private Integer bonusTalkTimeValue;
    @JsonProperty("receiverTax3Rate")
    private Integer receiverTax3Rate;
    @JsonProperty("receiverTax4Rate")
    private Integer receiverTax4Rate;
    @JsonProperty("cardGroupCode")
    private String cardGroupCode;
    @JsonProperty("cardGroupID")
    private String cardGroupID;
    @JsonProperty("receiverTax1Type")
    private String receiverTax1Type;
    @JsonProperty("receiverTax1Rate")
    private Integer receiverTax1Rate;
    @JsonProperty("receiverTax2Type")
    private String receiverTax2Type;
    @JsonProperty("receiverTax2Rate")
    private Integer receiverTax2Rate;
    @JsonProperty("receiverTax3Type")
    private Object receiverTax3Type;
    @JsonProperty("receiverTax4Type")
    private Object receiverTax4Type;
    @JsonProperty("online")
    private String online;
    @JsonProperty("reversalPermitted")
    private Object reversalPermitted;
    @JsonProperty("senderConvFactor")
    private String senderConvFactor;
    @JsonProperty("receiverConvFactor")
    private String receiverConvFactor;
    @JsonProperty("senderAccessFeeRate")
    private Integer senderAccessFeeRate;
    @JsonProperty("senderAccessFeeType")
    private String senderAccessFeeType;
    @JsonProperty("inPromo")
    private Integer inPromo;
    @JsonProperty("cosRequired")
    private String cosRequired;
    @JsonProperty("startRange")
    private Integer startRange;
    @JsonProperty("endRange")
    private Integer endRange;
    @JsonProperty("senderAccessFeeRateAsString")
    private String senderAccessFeeRateAsString;
    @JsonProperty("validityPeriodAsString")
    private String validityPeriodAsString;
    @JsonProperty("status")
    private String status;
    @JsonProperty("version")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("voucherType")
    public String getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucherType")
    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    @JsonProperty("voucherTypeDesc")
    public Object getVoucherTypeDesc() {
        return voucherTypeDesc;
    }

    @JsonProperty("voucherTypeDesc")
    public void setVoucherTypeDesc(Object voucherTypeDesc) {
        this.voucherTypeDesc = voucherTypeDesc;
    }

    @JsonProperty("voucherSegment")
    public String getVoucherSegment() {
        return voucherSegment;
    }

    @JsonProperty("voucherSegment")
    public void setVoucherSegment(String voucherSegment) {
        this.voucherSegment = voucherSegment;
    }

    @JsonProperty("voucherSegmentDesc")
    public Object getVoucherSegmentDesc() {
        return voucherSegmentDesc;
    }

    @JsonProperty("voucherSegmentDesc")
    public void setVoucherSegmentDesc(Object voucherSegmentDesc) {
        this.voucherSegmentDesc = voucherSegmentDesc;
    }

    @JsonProperty("voucherProductId")
    public String getVoucherProductId() {
        return voucherProductId;
    }

    @JsonProperty("voucherProductId")
    public void setVoucherProductId(String voucherProductId) {
        this.voucherProductId = voucherProductId;
    }

    @JsonProperty("cardGroupType")
    public Object getCardGroupType() {
        return cardGroupType;
    }

    @JsonProperty("cardGroupType")
    public void setCardGroupType(Object cardGroupType) {
        this.cardGroupType = cardGroupType;
    }

    @JsonProperty("voucherDenomination")
    public Object getVoucherDenomination() {
        return voucherDenomination;
    }

    @JsonProperty("voucherDenomination")
    public void setVoucherDenomination(Object voucherDenomination) {
        this.voucherDenomination = voucherDenomination;
    }

    @JsonProperty("productName")
    public Object getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(Object productName) {
        this.productName = productName;
    }

    @JsonProperty("receiverTax1RateAsString")
    public String getReceiverTax1RateAsString() {
        return receiverTax1RateAsString;
    }

    @JsonProperty("receiverTax1RateAsString")
    public void setReceiverTax1RateAsString(String receiverTax1RateAsString) {
        this.receiverTax1RateAsString = receiverTax1RateAsString;
    }

    @JsonProperty("receiverTax2RateAsString")
    public String getReceiverTax2RateAsString() {
        return receiverTax2RateAsString;
    }

    @JsonProperty("receiverTax2RateAsString")
    public void setReceiverTax2RateAsString(String receiverTax2RateAsString) {
        this.receiverTax2RateAsString = receiverTax2RateAsString;
    }

    @JsonProperty("senderTax1Rate")
    public Integer getSenderTax1Rate() {
        return senderTax1Rate;
    }

    @JsonProperty("senderTax1Rate")
    public void setSenderTax1Rate(Integer senderTax1Rate) {
        this.senderTax1Rate = senderTax1Rate;
    }

    @JsonProperty("senderTax1RateAsString")
    public String getSenderTax1RateAsString() {
        return senderTax1RateAsString;
    }

    @JsonProperty("senderTax1RateAsString")
    public void setSenderTax1RateAsString(String senderTax1RateAsString) {
        this.senderTax1RateAsString = senderTax1RateAsString;
    }

    @JsonProperty("senderTax1Type")
    public String getSenderTax1Type() {
        return senderTax1Type;
    }

    @JsonProperty("senderTax1Type")
    public void setSenderTax1Type(String senderTax1Type) {
        this.senderTax1Type = senderTax1Type;
    }

    @JsonProperty("senderTax2Rate")
    public Integer getSenderTax2Rate() {
        return senderTax2Rate;
    }

    @JsonProperty("senderTax2Rate")
    public void setSenderTax2Rate(Integer senderTax2Rate) {
        this.senderTax2Rate = senderTax2Rate;
    }

    @JsonProperty("senderTax2RateAsString")
    public String getSenderTax2RateAsString() {
        return senderTax2RateAsString;
    }

    @JsonProperty("senderTax2RateAsString")
    public void setSenderTax2RateAsString(String senderTax2RateAsString) {
        this.senderTax2RateAsString = senderTax2RateAsString;
    }

    @JsonProperty("senderTax2Type")
    public String getSenderTax2Type() {
        return senderTax2Type;
    }

    @JsonProperty("senderTax2Type")
    public void setSenderTax2Type(String senderTax2Type) {
        this.senderTax2Type = senderTax2Type;
    }

    @JsonProperty("both")
    public String getBoth() {
        return both;
    }

    @JsonProperty("both")
    public void setBoth(String both) {
        this.both = both;
    }

    @JsonProperty("validityPeriodType")
    public String getValidityPeriodType() {
        return validityPeriodType;
    }

    @JsonProperty("validityPeriodType")
    public void setValidityPeriodType(String validityPeriodType) {
        this.validityPeriodType = validityPeriodType;
    }

    @JsonProperty("validityPeriod")
    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    @JsonProperty("validityPeriod")
    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    @JsonProperty("gracePeriod")
    public Integer getGracePeriod() {
        return gracePeriod;
    }

    @JsonProperty("gracePeriod")
    public void setGracePeriod(Integer gracePeriod) {
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

    @JsonProperty("senderTax2Name")
    public String getSenderTax2Name() {
        return senderTax2Name;
    }

    @JsonProperty("senderTax2Name")
    public void setSenderTax2Name(String senderTax2Name) {
        this.senderTax2Name = senderTax2Name;
    }

    @JsonProperty("receiverTax1Name")
    public String getReceiverTax1Name() {
        return receiverTax1Name;
    }

    @JsonProperty("receiverTax1Name")
    public void setReceiverTax1Name(String receiverTax1Name) {
        this.receiverTax1Name = receiverTax1Name;
    }

    @JsonProperty("receiverTax2Name")
    public String getReceiverTax2Name() {
        return receiverTax2Name;
    }

    @JsonProperty("receiverTax2Name")
    public void setReceiverTax2Name(String receiverTax2Name) {
        this.receiverTax2Name = receiverTax2Name;
    }

    @JsonProperty("receiverAccessFeeType")
    public String getReceiverAccessFeeType() {
        return receiverAccessFeeType;
    }

    @JsonProperty("receiverAccessFeeType")
    public void setReceiverAccessFeeType(String receiverAccessFeeType) {
        this.receiverAccessFeeType = receiverAccessFeeType;
    }

    @JsonProperty("receiverAccessFeeRate")
    public Integer getReceiverAccessFeeRate() {
        return receiverAccessFeeRate;
    }

    @JsonProperty("receiverAccessFeeRate")
    public void setReceiverAccessFeeRate(Integer receiverAccessFeeRate) {
        this.receiverAccessFeeRate = receiverAccessFeeRate;
    }

    @JsonProperty("minSenderAccessFee")
    public Integer getMinSenderAccessFee() {
        return minSenderAccessFee;
    }

    @JsonProperty("minSenderAccessFee")
    public void setMinSenderAccessFee(Integer minSenderAccessFee) {
        this.minSenderAccessFee = minSenderAccessFee;
    }

    @JsonProperty("maxSenderAccessFee")
    public Integer getMaxSenderAccessFee() {
        return maxSenderAccessFee;
    }

    @JsonProperty("maxSenderAccessFee")
    public void setMaxSenderAccessFee(Integer maxSenderAccessFee) {
        this.maxSenderAccessFee = maxSenderAccessFee;
    }

    @JsonProperty("minReceiverAccessFee")
    public Integer getMinReceiverAccessFee() {
        return minReceiverAccessFee;
    }

    @JsonProperty("minReceiverAccessFee")
    public void setMinReceiverAccessFee(Integer minReceiverAccessFee) {
        this.minReceiverAccessFee = minReceiverAccessFee;
    }

    @JsonProperty("maxReceiverAccessFee")
    public Integer getMaxReceiverAccessFee() {
        return maxReceiverAccessFee;
    }

    @JsonProperty("maxReceiverAccessFee")
    public void setMaxReceiverAccessFee(Integer maxReceiverAccessFee) {
        this.maxReceiverAccessFee = maxReceiverAccessFee;
    }

    @JsonProperty("multipleOf")
    public Integer getMultipleOf() {
        return multipleOf;
    }

    @JsonProperty("multipleOf")
    public void setMultipleOf(Integer multipleOf) {
        this.multipleOf = multipleOf;
    }

    @JsonProperty("cardGroupSubServiceId")
    public String getCardGroupSubServiceId() {
        return cardGroupSubServiceId;
    }

    @JsonProperty("cardGroupSubServiceId")
    public void setCardGroupSubServiceId(String cardGroupSubServiceId) {
        this.cardGroupSubServiceId = cardGroupSubServiceId;
    }

    @JsonProperty("cardGroupSubServiceIdDesc")
    public String getCardGroupSubServiceIdDesc() {
        return cardGroupSubServiceIdDesc;
    }

    @JsonProperty("cardGroupSubServiceIdDesc")
    public void setCardGroupSubServiceIdDesc(String cardGroupSubServiceIdDesc) {
        this.cardGroupSubServiceIdDesc = cardGroupSubServiceIdDesc;
    }

    @JsonProperty("serviceTypeId")
    public String getServiceTypeId() {
        return serviceTypeId;
    }

    @JsonProperty("serviceTypeId")
    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    @JsonProperty("bonusValidityValue")
    public Integer getBonusValidityValue() {
        return bonusValidityValue;
    }

    @JsonProperty("bonusValidityValue")
    public void setBonusValidityValue(Integer bonusValidityValue) {
        this.bonusValidityValue = bonusValidityValue;
    }

    @JsonProperty("cardName")
    public String getCardName() {
        return cardName;
    }

    @JsonProperty("cardName")
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    @JsonProperty("reversalModifiedDate")
    public Object getReversalModifiedDate() {
        return reversalModifiedDate;
    }

    @JsonProperty("reversalModifiedDate")
    public void setReversalModifiedDate(Object reversalModifiedDate) {
        this.reversalModifiedDate = reversalModifiedDate;
    }

    @JsonProperty("receiverTax3Name")
    public Object getReceiverTax3Name() {
        return receiverTax3Name;
    }

    @JsonProperty("receiverTax3Name")
    public void setReceiverTax3Name(Object receiverTax3Name) {
        this.receiverTax3Name = receiverTax3Name;
    }

    @JsonProperty("receiverTax4Name")
    public Object getReceiverTax4Name() {
        return receiverTax4Name;
    }

    @JsonProperty("receiverTax4Name")
    public void setReceiverTax4Name(Object receiverTax4Name) {
        this.receiverTax4Name = receiverTax4Name;
    }

    @JsonProperty("bonusAccList")
    public List<BonusAccList> getBonusAccList() {
        return bonusAccList;
    }

    @JsonProperty("bonusAccList")
    public void setBonusAccList(List<BonusAccList> bonusAccList) {
        this.bonusAccList = bonusAccList;
    }

    @JsonProperty("reversalModifiedDateAsString")
    public Object getReversalModifiedDateAsString() {
        return reversalModifiedDateAsString;
    }

    @JsonProperty("reversalModifiedDateAsString")
    public void setReversalModifiedDateAsString(Object reversalModifiedDateAsString) {
        this.reversalModifiedDateAsString = reversalModifiedDateAsString;
    }

    @JsonProperty("applicableFrom")
    public Object getApplicableFrom() {
        return applicableFrom;
    }

    @JsonProperty("applicableFrom")
    public void setApplicableFrom(Object applicableFrom) {
        this.applicableFrom = applicableFrom;
    }

    @JsonProperty("serviceTypeSelector")
    public Object getServiceTypeSelector() {
        return serviceTypeSelector;
    }

    @JsonProperty("serviceTypeSelector")
    public void setServiceTypeSelector(Object serviceTypeSelector) {
        this.serviceTypeSelector = serviceTypeSelector;
    }

    @JsonProperty("receiverTax4RateAsString")
    public String getReceiverTax4RateAsString() {
        return receiverTax4RateAsString;
    }

    @JsonProperty("receiverTax4RateAsString")
    public void setReceiverTax4RateAsString(String receiverTax4RateAsString) {
        this.receiverTax4RateAsString = receiverTax4RateAsString;
    }

    @JsonProperty("minReceiverAccessFeeAsString")
    public String getMinReceiverAccessFeeAsString() {
        return minReceiverAccessFeeAsString;
    }

    @JsonProperty("minReceiverAccessFeeAsString")
    public void setMinReceiverAccessFeeAsString(String minReceiverAccessFeeAsString) {
        this.minReceiverAccessFeeAsString = minReceiverAccessFeeAsString;
    }

    @JsonProperty("minSenderAccessFeeAsString")
    public String getMinSenderAccessFeeAsString() {
        return minSenderAccessFeeAsString;
    }

    @JsonProperty("minSenderAccessFeeAsString")
    public void setMinSenderAccessFeeAsString(String minSenderAccessFeeAsString) {
        this.minSenderAccessFeeAsString = minSenderAccessFeeAsString;
    }

    @JsonProperty("receiverAccessFeeRateAsString")
    public String getReceiverAccessFeeRateAsString() {
        return receiverAccessFeeRateAsString;
    }

    @JsonProperty("receiverAccessFeeRateAsString")
    public void setReceiverAccessFeeRateAsString(String receiverAccessFeeRateAsString) {
        this.receiverAccessFeeRateAsString = receiverAccessFeeRateAsString;
    }

    @JsonProperty("multipleOfAsString")
    public String getMultipleOfAsString() {
        return multipleOfAsString;
    }

    @JsonProperty("multipleOfAsString")
    public void setMultipleOfAsString(String multipleOfAsString) {
        this.multipleOfAsString = multipleOfAsString;
    }

    @JsonProperty("maxReceiverAccessFeeAsString")
    public String getMaxReceiverAccessFeeAsString() {
        return maxReceiverAccessFeeAsString;
    }

    @JsonProperty("maxReceiverAccessFeeAsString")
    public void setMaxReceiverAccessFeeAsString(String maxReceiverAccessFeeAsString) {
        this.maxReceiverAccessFeeAsString = maxReceiverAccessFeeAsString;
    }

    @JsonProperty("bonus1validity")
    public Integer getBonus1validity() {
        return bonus1validity;
    }

    @JsonProperty("bonus1validity")
    public void setBonus1validity(Integer bonus1validity) {
        this.bonus1validity = bonus1validity;
    }

    @JsonProperty("bonus2validity")
    public Integer getBonus2validity() {
        return bonus2validity;
    }

    @JsonProperty("bonus2validity")
    public void setBonus2validity(Integer bonus2validity) {
        this.bonus2validity = bonus2validity;
    }

    @JsonProperty("bonusTalkTimeBundleType")
    public Object getBonusTalkTimeBundleType() {
        return bonusTalkTimeBundleType;
    }

    @JsonProperty("bonusTalkTimeBundleType")
    public void setBonusTalkTimeBundleType(Object bonusTalkTimeBundleType) {
        this.bonusTalkTimeBundleType = bonusTalkTimeBundleType;
    }

    @JsonProperty("bonusTalkTimeValidity")
    public Object getBonusTalkTimeValidity() {
        return bonusTalkTimeValidity;
    }

    @JsonProperty("bonusTalkTimeValidity")
    public void setBonusTalkTimeValidity(Object bonusTalkTimeValidity) {
        this.bonusTalkTimeValidity = bonusTalkTimeValidity;
    }

    @JsonProperty("bonus1")
    public Integer getBonus1() {
        return bonus1;
    }

    @JsonProperty("bonus1")
    public void setBonus1(Integer bonus1) {
        this.bonus1 = bonus1;
    }

    @JsonProperty("maxSenderAccessFeeAsString")
    public String getMaxSenderAccessFeeAsString() {
        return maxSenderAccessFeeAsString;
    }

    @JsonProperty("maxSenderAccessFeeAsString")
    public void setMaxSenderAccessFeeAsString(String maxSenderAccessFeeAsString) {
        this.maxSenderAccessFeeAsString = maxSenderAccessFeeAsString;
    }

    @JsonProperty("bonus2")
    public Integer getBonus2() {
        return bonus2;
    }

    @JsonProperty("bonus2")
    public void setBonus2(Integer bonus2) {
        this.bonus2 = bonus2;
    }

    @JsonProperty("cardGroupList")
    public Object getCardGroupList() {
        return cardGroupList;
    }

    @JsonProperty("cardGroupList")
    public void setCardGroupList(Object cardGroupList) {
        this.cardGroupList = cardGroupList;
    }

    @JsonProperty("editDetail")
    public Object getEditDetail() {
        return editDetail;
    }

    @JsonProperty("editDetail")
    public void setEditDetail(Object editDetail) {
        this.editDetail = editDetail;
    }

    @JsonProperty("bonusTalkTimeType")
    public Object getBonusTalkTimeType() {
        return bonusTalkTimeType;
    }

    @JsonProperty("bonusTalkTimeType")
    public void setBonusTalkTimeType(Object bonusTalkTimeType) {
        this.bonusTalkTimeType = bonusTalkTimeType;
    }

    @JsonProperty("bonusTalkTimeConvFactor")
    public Integer getBonusTalkTimeConvFactor() {
        return bonusTalkTimeConvFactor;
    }

    @JsonProperty("bonusTalkTimeConvFactor")
    public void setBonusTalkTimeConvFactor(Integer bonusTalkTimeConvFactor) {
        this.bonusTalkTimeConvFactor = bonusTalkTimeConvFactor;
    }

    @JsonProperty("bonusTalktimevalidity")
    public Integer getBonusTalktimevalidity() {
        return bonusTalktimevalidity;
    }

    @JsonProperty("bonusTalktimevalidity")
    public void setBonusTalktimevalidity(Integer bonusTalktimevalidity) {
        this.bonusTalktimevalidity = bonusTalktimevalidity;
    }

    @JsonProperty("inPromoAsString")
    public String getInPromoAsString() {
        return inPromoAsString;
    }

    @JsonProperty("inPromoAsString")
    public void setInPromoAsString(String inPromoAsString) {
        this.inPromoAsString = inPromoAsString;
    }

    @JsonProperty("receiverTax3RateAsString")
    public String getReceiverTax3RateAsString() {
        return receiverTax3RateAsString;
    }

    @JsonProperty("receiverTax3RateAsString")
    public void setReceiverTax3RateAsString(String receiverTax3RateAsString) {
        this.receiverTax3RateAsString = receiverTax3RateAsString;
    }

    @JsonProperty("networkCode")
    public Object getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(Object networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("oldApplicableFrom")
    public Integer getOldApplicableFrom() {
        return oldApplicableFrom;
    }

    @JsonProperty("oldApplicableFrom")
    public void setOldApplicableFrom(Integer oldApplicableFrom) {
        this.oldApplicableFrom = oldApplicableFrom;
    }

    @JsonProperty("applicableFromAsString")
    public String getApplicableFromAsString() {
        return applicableFromAsString;
    }

    @JsonProperty("applicableFromAsString")
    public void setApplicableFromAsString(String applicableFromAsString) {
        this.applicableFromAsString = applicableFromAsString;
    }

    @JsonProperty("rowIndex")
    public Integer getRowIndex() {
        return rowIndex;
    }

    @JsonProperty("rowIndex")
    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
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

    @JsonProperty("cardGroupSetID")
    public String getCardGroupSetID() {
        return cardGroupSetID;
    }

    @JsonProperty("cardGroupSetID")
    public void setCardGroupSetID(String cardGroupSetID) {
        this.cardGroupSetID = cardGroupSetID;
    }

    @JsonProperty("cardGroupSetName")
    public String getCardGroupSetName() {
        return cardGroupSetName;
    }

    @JsonProperty("cardGroupSetName")
    public void setCardGroupSetName(String cardGroupSetName) {
        this.cardGroupSetName = cardGroupSetName;
    }

    @JsonProperty("lastVersion")
    public Object getLastVersion() {
        return lastVersion;
    }

    @JsonProperty("lastVersion")
    public void setLastVersion(Object lastVersion) {
        this.lastVersion = lastVersion;
    }

    @JsonProperty("serviceTypeDesc")
    public String getServiceTypeDesc() {
        return serviceTypeDesc;
    }

    @JsonProperty("serviceTypeDesc")
    public void setServiceTypeDesc(String serviceTypeDesc) {
        this.serviceTypeDesc = serviceTypeDesc;
    }

    @JsonProperty("setType")
    public String getSetType() {
        return setType;
    }

    @JsonProperty("setType")
    public void setSetType(String setType) {
        this.setType = setType;
    }

    @JsonProperty("setTypeName")
    public String getSetTypeName() {
        return setTypeName;
    }

    @JsonProperty("setTypeName")
    public void setSetTypeName(String setTypeName) {
        this.setTypeName = setTypeName;
    }

    @JsonProperty("bonusTalkTimeRate")
    public Integer getBonusTalkTimeRate() {
        return bonusTalkTimeRate;
    }

    @JsonProperty("bonusTalkTimeRate")
    public void setBonusTalkTimeRate(Integer bonusTalkTimeRate) {
        this.bonusTalkTimeRate = bonusTalkTimeRate;
    }

    @JsonProperty("transferValue")
    public Integer getTransferValue() {
        return transferValue;
    }

    @JsonProperty("transferValue")
    public void setTransferValue(Integer transferValue) {
        this.transferValue = transferValue;
    }

    @JsonProperty("bonusTalkTimeValue")
    public Integer getBonusTalkTimeValue() {
        return bonusTalkTimeValue;
    }

    @JsonProperty("bonusTalkTimeValue")
    public void setBonusTalkTimeValue(Integer bonusTalkTimeValue) {
        this.bonusTalkTimeValue = bonusTalkTimeValue;
    }

    @JsonProperty("receiverTax3Rate")
    public Integer getReceiverTax3Rate() {
        return receiverTax3Rate;
    }

    @JsonProperty("receiverTax3Rate")
    public void setReceiverTax3Rate(Integer receiverTax3Rate) {
        this.receiverTax3Rate = receiverTax3Rate;
    }

    @JsonProperty("receiverTax4Rate")
    public Integer getReceiverTax4Rate() {
        return receiverTax4Rate;
    }

    @JsonProperty("receiverTax4Rate")
    public void setReceiverTax4Rate(Integer receiverTax4Rate) {
        this.receiverTax4Rate = receiverTax4Rate;
    }

    @JsonProperty("cardGroupCode")
    public String getCardGroupCode() {
        return cardGroupCode;
    }

    @JsonProperty("cardGroupCode")
    public void setCardGroupCode(String cardGroupCode) {
        this.cardGroupCode = cardGroupCode;
    }

    @JsonProperty("cardGroupID")
    public String getCardGroupID() {
        return cardGroupID;
    }

    @JsonProperty("cardGroupID")
    public void setCardGroupID(String cardGroupID) {
        this.cardGroupID = cardGroupID;
    }

    @JsonProperty("receiverTax1Type")
    public String getReceiverTax1Type() {
        return receiverTax1Type;
    }

    @JsonProperty("receiverTax1Type")
    public void setReceiverTax1Type(String receiverTax1Type) {
        this.receiverTax1Type = receiverTax1Type;
    }

    @JsonProperty("receiverTax1Rate")
    public Integer getReceiverTax1Rate() {
        return receiverTax1Rate;
    }

    @JsonProperty("receiverTax1Rate")
    public void setReceiverTax1Rate(Integer receiverTax1Rate) {
        this.receiverTax1Rate = receiverTax1Rate;
    }

    @JsonProperty("receiverTax2Type")
    public String getReceiverTax2Type() {
        return receiverTax2Type;
    }

    @JsonProperty("receiverTax2Type")
    public void setReceiverTax2Type(String receiverTax2Type) {
        this.receiverTax2Type = receiverTax2Type;
    }

    @JsonProperty("receiverTax2Rate")
    public Integer getReceiverTax2Rate() {
        return receiverTax2Rate;
    }

    @JsonProperty("receiverTax2Rate")
    public void setReceiverTax2Rate(Integer receiverTax2Rate) {
        this.receiverTax2Rate = receiverTax2Rate;
    }

    @JsonProperty("receiverTax3Type")
    public Object getReceiverTax3Type() {
        return receiverTax3Type;
    }

    @JsonProperty("receiverTax3Type")
    public void setReceiverTax3Type(Object receiverTax3Type) {
        this.receiverTax3Type = receiverTax3Type;
    }

    @JsonProperty("receiverTax4Type")
    public Object getReceiverTax4Type() {
        return receiverTax4Type;
    }

    @JsonProperty("receiverTax4Type")
    public void setReceiverTax4Type(Object receiverTax4Type) {
        this.receiverTax4Type = receiverTax4Type;
    }

    @JsonProperty("online")
    public String getOnline() {
        return online;
    }

    @JsonProperty("online")
    public void setOnline(String online) {
        this.online = online;
    }

    @JsonProperty("reversalPermitted")
    public Object getReversalPermitted() {
        return reversalPermitted;
    }

    @JsonProperty("reversalPermitted")
    public void setReversalPermitted(Object reversalPermitted) {
        this.reversalPermitted = reversalPermitted;
    }

    @JsonProperty("senderConvFactor")
    public String getSenderConvFactor() {
        return senderConvFactor;
    }

    @JsonProperty("senderConvFactor")
    public void setSenderConvFactor(String senderConvFactor) {
        this.senderConvFactor = senderConvFactor;
    }

    @JsonProperty("receiverConvFactor")
    public String getReceiverConvFactor() {
        return receiverConvFactor;
    }

    @JsonProperty("receiverConvFactor")
    public void setReceiverConvFactor(String receiverConvFactor) {
        this.receiverConvFactor = receiverConvFactor;
    }

    @JsonProperty("senderAccessFeeRate")
    public Integer getSenderAccessFeeRate() {
        return senderAccessFeeRate;
    }

    @JsonProperty("senderAccessFeeRate")
    public void setSenderAccessFeeRate(Integer senderAccessFeeRate) {
        this.senderAccessFeeRate = senderAccessFeeRate;
    }

    @JsonProperty("senderAccessFeeType")
    public String getSenderAccessFeeType() {
        return senderAccessFeeType;
    }

    @JsonProperty("senderAccessFeeType")
    public void setSenderAccessFeeType(String senderAccessFeeType) {
        this.senderAccessFeeType = senderAccessFeeType;
    }

    @JsonProperty("inPromo")
    public Integer getInPromo() {
        return inPromo;
    }

    @JsonProperty("inPromo")
    public void setInPromo(Integer inPromo) {
        this.inPromo = inPromo;
    }

    @JsonProperty("cosRequired")
    public String getCosRequired() {
        return cosRequired;
    }

    @JsonProperty("cosRequired")
    public void setCosRequired(String cosRequired) {
        this.cosRequired = cosRequired;
    }

    @JsonProperty("startRange")
    public Integer getStartRange() {
        return startRange;
    }

    @JsonProperty("startRange")
    public void setStartRange(Integer startRange) {
        this.startRange = startRange;
    }

    @JsonProperty("endRange")
    public Integer getEndRange() {
        return endRange;
    }

    @JsonProperty("endRange")
    public void setEndRange(Integer endRange) {
        this.endRange = endRange;
    }

    @JsonProperty("senderAccessFeeRateAsString")
    public String getSenderAccessFeeRateAsString() {
        return senderAccessFeeRateAsString;
    }

    @JsonProperty("senderAccessFeeRateAsString")
    public void setSenderAccessFeeRateAsString(String senderAccessFeeRateAsString) {
        this.senderAccessFeeRateAsString = senderAccessFeeRateAsString;
    }

    @JsonProperty("validityPeriodAsString")
    public String getValidityPeriodAsString() {
        return validityPeriodAsString;
    }

    @JsonProperty("validityPeriodAsString")
    public void setValidityPeriodAsString(String validityPeriodAsString) {
        this.validityPeriodAsString = validityPeriodAsString;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
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
