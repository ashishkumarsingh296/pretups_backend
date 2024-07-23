
package restassuredapi.pojo.viewvouchercardgroupresponsepojo;

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
    "cardGroupSetID",
    "version",
    "cardGroupID",
    "bundleID",
    "type",
    "bonusValidity",
    "bonusValue",
    "multFactor",
    "bonusName",
    "bundleType",
    "restrictedOnIN",
    "bonusAccDetailList",
    "bonusCode"
})
public class BonusAccList {

    @JsonProperty("cardGroupSetID")
    private String cardGroupSetID;
    @JsonProperty("version")
    private String version;
    @JsonProperty("cardGroupID")
    private String cardGroupID;
    @JsonProperty("bundleID")
    private String bundleID;
    @JsonProperty("type")
    private String type;
    @JsonProperty("bonusValidity")
    private String bonusValidity;
    @JsonProperty("bonusValue")
    private String bonusValue;
    @JsonProperty("multFactor")
    private String multFactor;
    @JsonProperty("bonusName")
    private String bonusName;
    @JsonProperty("bundleType")
    private String bundleType;
    @JsonProperty("restrictedOnIN")
    private String restrictedOnIN;
    @JsonProperty("bonusAccDetailList")
    private Object bonusAccDetailList;
    @JsonProperty("bonusCode")
    private String bonusCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cardGroupSetID")
    public String getCardGroupSetID() {
        return cardGroupSetID;
    }

    @JsonProperty("cardGroupSetID")
    public void setCardGroupSetID(String cardGroupSetID) {
        this.cardGroupSetID = cardGroupSetID;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("cardGroupID")
    public String getCardGroupID() {
        return cardGroupID;
    }

    @JsonProperty("cardGroupID")
    public void setCardGroupID(String cardGroupID) {
        this.cardGroupID = cardGroupID;
    }

    @JsonProperty("bundleID")
    public String getBundleID() {
        return bundleID;
    }

    @JsonProperty("bundleID")
    public void setBundleID(String bundleID) {
        this.bundleID = bundleID;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("bonusValidity")
    public String getBonusValidity() {
        return bonusValidity;
    }

    @JsonProperty("bonusValidity")
    public void setBonusValidity(String bonusValidity) {
        this.bonusValidity = bonusValidity;
    }

    @JsonProperty("bonusValue")
    public String getBonusValue() {
        return bonusValue;
    }

    @JsonProperty("bonusValue")
    public void setBonusValue(String bonusValue) {
        this.bonusValue = bonusValue;
    }

    @JsonProperty("multFactor")
    public String getMultFactor() {
        return multFactor;
    }

    @JsonProperty("multFactor")
    public void setMultFactor(String multFactor) {
        this.multFactor = multFactor;
    }

    @JsonProperty("bonusName")
    public String getBonusName() {
        return bonusName;
    }

    @JsonProperty("bonusName")
    public void setBonusName(String bonusName) {
        this.bonusName = bonusName;
    }

    @JsonProperty("bundleType")
    public String getBundleType() {
        return bundleType;
    }

    @JsonProperty("bundleType")
    public void setBundleType(String bundleType) {
        this.bundleType = bundleType;
    }

    @JsonProperty("restrictedOnIN")
    public String getRestrictedOnIN() {
        return restrictedOnIN;
    }

    @JsonProperty("restrictedOnIN")
    public void setRestrictedOnIN(String restrictedOnIN) {
        this.restrictedOnIN = restrictedOnIN;
    }

    @JsonProperty("bonusAccDetailList")
    public Object getBonusAccDetailList() {
        return bonusAccDetailList;
    }

    @JsonProperty("bonusAccDetailList")
    public void setBonusAccDetailList(Object bonusAccDetailList) {
        this.bonusAccDetailList = bonusAccDetailList;
    }

    @JsonProperty("bonusCode")
    public String getBonusCode() {
        return bonusCode;
    }

    @JsonProperty("bonusCode")
    public void setBonusCode(String bonusCode) {
        this.bonusCode = bonusCode;
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
