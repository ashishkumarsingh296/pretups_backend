
package restassuredapi.pojo.phonedetailsresponsepojo;

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
    "rowIndex",
    "showSmsPin",
    "oldSmsPin",
    "multiBox",
    "networkCode",
    "mhash",
    "imei",
    "simProfileID",
    "otp",
    "prefixID",
    "encryptDecryptKey",
    "phoneLanguage",
    "pinRequired",
    "requestGatewayCode",
    "lastAccessOn",
    "accessOn",
    "barUserForInvalidPin",
    "phoneProfile",
    "invalidPinCount",
    "smsPin",
    "lastTransferID",
    "tempTransferID",
    "registered",
    "pinRequiredBool",
    "firstInvalidPinTime",
    "phoneProfileDesc",
    "forcePinCheckReqd",
    "operationType",
    "idGenerate",
    "pinGenerateAllow",
    "ownerTempTransferId",
    "userPhonesId",
    "currentModifiedOn",
    "pinModifyFlag",
    "primaryNumber",
    "userId",
    "confirmSmsPin",
    "lastTransferType",
    "pinModifiedOn",
    "lastTransactionOn",
    "lastTransactionStatus",
    "createdBy",
    "country",
    "msisdn",
    "description",
    "pinReset",
    "modifiedBy",
    "createdOn",
    "modifiedOn",
    "locale"
})
@Generated("jsonschema2pojo")
public class UserPhoneVO {

    @JsonProperty("rowIndex")
    private Integer rowIndex;
    @JsonProperty("showSmsPin")
    private Object showSmsPin;
    @JsonProperty("oldSmsPin")
    private Object oldSmsPin;
    @JsonProperty("multiBox")
    private Object multiBox;
    @JsonProperty("networkCode")
    private Object networkCode;
    @JsonProperty("mhash")
    private Object mhash;
    @JsonProperty("imei")
    private Object imei;
    @JsonProperty("simProfileID")
    private Object simProfileID;
    @JsonProperty("otp")
    private Object otp;
    @JsonProperty("prefixID")
    private Integer prefixID;
    @JsonProperty("encryptDecryptKey")
    private Object encryptDecryptKey;
    @JsonProperty("phoneLanguage")
    private String phoneLanguage;
    @JsonProperty("pinRequired")
    private Object pinRequired;
    @JsonProperty("requestGatewayCode")
    private Object requestGatewayCode;
    @JsonProperty("lastAccessOn")
    private Object lastAccessOn;
    @JsonProperty("accessOn")
    private Boolean accessOn;
    @JsonProperty("barUserForInvalidPin")
    private Boolean barUserForInvalidPin;
    @JsonProperty("phoneProfile")
    private Object phoneProfile;
    @JsonProperty("invalidPinCount")
    private Integer invalidPinCount;
    @JsonProperty("smsPin")
    private Object smsPin;
    @JsonProperty("lastTransferID")
    private Object lastTransferID;
    @JsonProperty("tempTransferID")
    private Object tempTransferID;
    @JsonProperty("registered")
    private Boolean registered;
    @JsonProperty("pinRequiredBool")
    private Boolean pinRequiredBool;
    @JsonProperty("firstInvalidPinTime")
    private Object firstInvalidPinTime;
    @JsonProperty("phoneProfileDesc")
    private Object phoneProfileDesc;
    @JsonProperty("forcePinCheckReqd")
    private Boolean forcePinCheckReqd;
    @JsonProperty("operationType")
    private Object operationType;
    @JsonProperty("idGenerate")
    private Boolean idGenerate;
    @JsonProperty("pinGenerateAllow")
    private Object pinGenerateAllow;
    @JsonProperty("ownerTempTransferId")
    private Object ownerTempTransferId;
    @JsonProperty("userPhonesId")
    private Object userPhonesId;
    @JsonProperty("currentModifiedOn")
    private Object currentModifiedOn;
    @JsonProperty("pinModifyFlag")
    private Boolean pinModifyFlag;
    @JsonProperty("primaryNumber")
    private String primaryNumber;
    @JsonProperty("userId")
    private Object userId;
    @JsonProperty("confirmSmsPin")
    private Object confirmSmsPin;
    @JsonProperty("lastTransferType")
    private Object lastTransferType;
    @JsonProperty("pinModifiedOn")
    private Object pinModifiedOn;
    @JsonProperty("lastTransactionOn")
    private Object lastTransactionOn;
    @JsonProperty("lastTransactionStatus")
    private Object lastTransactionStatus;
    @JsonProperty("createdBy")
    private Object createdBy;
    @JsonProperty("country")
    private Object country;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("description")
    private Object description;
    @JsonProperty("pinReset")
    private Object pinReset;
    @JsonProperty("modifiedBy")
    private Object modifiedBy;
    @JsonProperty("createdOn")
    private Object createdOn;
    @JsonProperty("modifiedOn")
    private Object modifiedOn;
    @JsonProperty("locale")
    private Object locale;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("rowIndex")
    public Integer getRowIndex() {
        return rowIndex;
    }

    @JsonProperty("rowIndex")
    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    @JsonProperty("showSmsPin")
    public Object getShowSmsPin() {
        return showSmsPin;
    }

    @JsonProperty("showSmsPin")
    public void setShowSmsPin(Object showSmsPin) {
        this.showSmsPin = showSmsPin;
    }

    @JsonProperty("oldSmsPin")
    public Object getOldSmsPin() {
        return oldSmsPin;
    }

    @JsonProperty("oldSmsPin")
    public void setOldSmsPin(Object oldSmsPin) {
        this.oldSmsPin = oldSmsPin;
    }

    @JsonProperty("multiBox")
    public Object getMultiBox() {
        return multiBox;
    }

    @JsonProperty("multiBox")
    public void setMultiBox(Object multiBox) {
        this.multiBox = multiBox;
    }

    @JsonProperty("networkCode")
    public Object getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(Object networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("mhash")
    public Object getMhash() {
        return mhash;
    }

    @JsonProperty("mhash")
    public void setMhash(Object mhash) {
        this.mhash = mhash;
    }

    @JsonProperty("imei")
    public Object getImei() {
        return imei;
    }

    @JsonProperty("imei")
    public void setImei(Object imei) {
        this.imei = imei;
    }

    @JsonProperty("simProfileID")
    public Object getSimProfileID() {
        return simProfileID;
    }

    @JsonProperty("simProfileID")
    public void setSimProfileID(Object simProfileID) {
        this.simProfileID = simProfileID;
    }

    @JsonProperty("otp")
    public Object getOtp() {
        return otp;
    }

    @JsonProperty("otp")
    public void setOtp(Object otp) {
        this.otp = otp;
    }

    @JsonProperty("prefixID")
    public Integer getPrefixID() {
        return prefixID;
    }

    @JsonProperty("prefixID")
    public void setPrefixID(Integer prefixID) {
        this.prefixID = prefixID;
    }

    @JsonProperty("encryptDecryptKey")
    public Object getEncryptDecryptKey() {
        return encryptDecryptKey;
    }

    @JsonProperty("encryptDecryptKey")
    public void setEncryptDecryptKey(Object encryptDecryptKey) {
        this.encryptDecryptKey = encryptDecryptKey;
    }

    @JsonProperty("phoneLanguage")
    public String getPhoneLanguage() {
        return phoneLanguage;
    }

    @JsonProperty("phoneLanguage")
    public void setPhoneLanguage(String phoneLanguage) {
        this.phoneLanguage = phoneLanguage;
    }

    @JsonProperty("pinRequired")
    public Object getPinRequired() {
        return pinRequired;
    }

    @JsonProperty("pinRequired")
    public void setPinRequired(Object pinRequired) {
        this.pinRequired = pinRequired;
    }

    @JsonProperty("requestGatewayCode")
    public Object getRequestGatewayCode() {
        return requestGatewayCode;
    }

    @JsonProperty("requestGatewayCode")
    public void setRequestGatewayCode(Object requestGatewayCode) {
        this.requestGatewayCode = requestGatewayCode;
    }

    @JsonProperty("lastAccessOn")
    public Object getLastAccessOn() {
        return lastAccessOn;
    }

    @JsonProperty("lastAccessOn")
    public void setLastAccessOn(Object lastAccessOn) {
        this.lastAccessOn = lastAccessOn;
    }

    @JsonProperty("accessOn")
    public Boolean getAccessOn() {
        return accessOn;
    }

    @JsonProperty("accessOn")
    public void setAccessOn(Boolean accessOn) {
        this.accessOn = accessOn;
    }

    @JsonProperty("barUserForInvalidPin")
    public Boolean getBarUserForInvalidPin() {
        return barUserForInvalidPin;
    }

    @JsonProperty("barUserForInvalidPin")
    public void setBarUserForInvalidPin(Boolean barUserForInvalidPin) {
        this.barUserForInvalidPin = barUserForInvalidPin;
    }

    @JsonProperty("phoneProfile")
    public Object getPhoneProfile() {
        return phoneProfile;
    }

    @JsonProperty("phoneProfile")
    public void setPhoneProfile(Object phoneProfile) {
        this.phoneProfile = phoneProfile;
    }

    @JsonProperty("invalidPinCount")
    public Integer getInvalidPinCount() {
        return invalidPinCount;
    }

    @JsonProperty("invalidPinCount")
    public void setInvalidPinCount(Integer invalidPinCount) {
        this.invalidPinCount = invalidPinCount;
    }

    @JsonProperty("smsPin")
    public Object getSmsPin() {
        return smsPin;
    }

    @JsonProperty("smsPin")
    public void setSmsPin(Object smsPin) {
        this.smsPin = smsPin;
    }

    @JsonProperty("lastTransferID")
    public Object getLastTransferID() {
        return lastTransferID;
    }

    @JsonProperty("lastTransferID")
    public void setLastTransferID(Object lastTransferID) {
        this.lastTransferID = lastTransferID;
    }

    @JsonProperty("tempTransferID")
    public Object getTempTransferID() {
        return tempTransferID;
    }

    @JsonProperty("tempTransferID")
    public void setTempTransferID(Object tempTransferID) {
        this.tempTransferID = tempTransferID;
    }

    @JsonProperty("registered")
    public Boolean getRegistered() {
        return registered;
    }

    @JsonProperty("registered")
    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    @JsonProperty("pinRequiredBool")
    public Boolean getPinRequiredBool() {
        return pinRequiredBool;
    }

    @JsonProperty("pinRequiredBool")
    public void setPinRequiredBool(Boolean pinRequiredBool) {
        this.pinRequiredBool = pinRequiredBool;
    }

    @JsonProperty("firstInvalidPinTime")
    public Object getFirstInvalidPinTime() {
        return firstInvalidPinTime;
    }

    @JsonProperty("firstInvalidPinTime")
    public void setFirstInvalidPinTime(Object firstInvalidPinTime) {
        this.firstInvalidPinTime = firstInvalidPinTime;
    }

    @JsonProperty("phoneProfileDesc")
    public Object getPhoneProfileDesc() {
        return phoneProfileDesc;
    }

    @JsonProperty("phoneProfileDesc")
    public void setPhoneProfileDesc(Object phoneProfileDesc) {
        this.phoneProfileDesc = phoneProfileDesc;
    }

    @JsonProperty("forcePinCheckReqd")
    public Boolean getForcePinCheckReqd() {
        return forcePinCheckReqd;
    }

    @JsonProperty("forcePinCheckReqd")
    public void setForcePinCheckReqd(Boolean forcePinCheckReqd) {
        this.forcePinCheckReqd = forcePinCheckReqd;
    }

    @JsonProperty("operationType")
    public Object getOperationType() {
        return operationType;
    }

    @JsonProperty("operationType")
    public void setOperationType(Object operationType) {
        this.operationType = operationType;
    }

    @JsonProperty("idGenerate")
    public Boolean getIdGenerate() {
        return idGenerate;
    }

    @JsonProperty("idGenerate")
    public void setIdGenerate(Boolean idGenerate) {
        this.idGenerate = idGenerate;
    }

    @JsonProperty("pinGenerateAllow")
    public Object getPinGenerateAllow() {
        return pinGenerateAllow;
    }

    @JsonProperty("pinGenerateAllow")
    public void setPinGenerateAllow(Object pinGenerateAllow) {
        this.pinGenerateAllow = pinGenerateAllow;
    }

    @JsonProperty("ownerTempTransferId")
    public Object getOwnerTempTransferId() {
        return ownerTempTransferId;
    }

    @JsonProperty("ownerTempTransferId")
    public void setOwnerTempTransferId(Object ownerTempTransferId) {
        this.ownerTempTransferId = ownerTempTransferId;
    }

    @JsonProperty("userPhonesId")
    public Object getUserPhonesId() {
        return userPhonesId;
    }

    @JsonProperty("userPhonesId")
    public void setUserPhonesId(Object userPhonesId) {
        this.userPhonesId = userPhonesId;
    }

    @JsonProperty("currentModifiedOn")
    public Object getCurrentModifiedOn() {
        return currentModifiedOn;
    }

    @JsonProperty("currentModifiedOn")
    public void setCurrentModifiedOn(Object currentModifiedOn) {
        this.currentModifiedOn = currentModifiedOn;
    }

    @JsonProperty("pinModifyFlag")
    public Boolean getPinModifyFlag() {
        return pinModifyFlag;
    }

    @JsonProperty("pinModifyFlag")
    public void setPinModifyFlag(Boolean pinModifyFlag) {
        this.pinModifyFlag = pinModifyFlag;
    }

    @JsonProperty("primaryNumber")
    public String getPrimaryNumber() {
        return primaryNumber;
    }

    @JsonProperty("primaryNumber")
    public void setPrimaryNumber(String primaryNumber) {
        this.primaryNumber = primaryNumber;
    }

    @JsonProperty("userId")
    public Object getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(Object userId) {
        this.userId = userId;
    }

    @JsonProperty("confirmSmsPin")
    public Object getConfirmSmsPin() {
        return confirmSmsPin;
    }

    @JsonProperty("confirmSmsPin")
    public void setConfirmSmsPin(Object confirmSmsPin) {
        this.confirmSmsPin = confirmSmsPin;
    }

    @JsonProperty("lastTransferType")
    public Object getLastTransferType() {
        return lastTransferType;
    }

    @JsonProperty("lastTransferType")
    public void setLastTransferType(Object lastTransferType) {
        this.lastTransferType = lastTransferType;
    }

    @JsonProperty("pinModifiedOn")
    public Object getPinModifiedOn() {
        return pinModifiedOn;
    }

    @JsonProperty("pinModifiedOn")
    public void setPinModifiedOn(Object pinModifiedOn) {
        this.pinModifiedOn = pinModifiedOn;
    }

    @JsonProperty("lastTransactionOn")
    public Object getLastTransactionOn() {
        return lastTransactionOn;
    }

    @JsonProperty("lastTransactionOn")
    public void setLastTransactionOn(Object lastTransactionOn) {
        this.lastTransactionOn = lastTransactionOn;
    }

    @JsonProperty("lastTransactionStatus")
    public Object getLastTransactionStatus() {
        return lastTransactionStatus;
    }

    @JsonProperty("lastTransactionStatus")
    public void setLastTransactionStatus(Object lastTransactionStatus) {
        this.lastTransactionStatus = lastTransactionStatus;
    }

    @JsonProperty("createdBy")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("country")
    public Object getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(Object country) {
        this.country = country;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("description")
    public Object getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Object description) {
        this.description = description;
    }

    @JsonProperty("pinReset")
    public Object getPinReset() {
        return pinReset;
    }

    @JsonProperty("pinReset")
    public void setPinReset(Object pinReset) {
        this.pinReset = pinReset;
    }

    @JsonProperty("modifiedBy")
    public Object getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(Object modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("createdOn")
    public Object getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("modifiedOn")
    public Object getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(Object modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("locale")
    public Object getLocale() {
        return locale;
    }

    @JsonProperty("locale")
    public void setLocale(Object locale) {
        this.locale = locale;
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
