
package restassuredapi.pojo.getStaffUsersResponsepojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "accessOn",
    "barUserForInvalidPin",
    "confirmSmsPin",
    "country",
    "createdBy",
    "createdOn",
    "currentModifiedOn",
    "description",
    "encryptDecryptKey",
    "firstInvalidPinTime",
    "forcePinCheckReqd",
    "idGenerate",
    "imei",
    "invalidPinCount",
    "lastAccessOn",
    "lastTransactionOn",
    "lastTransactionStatus",
    "lastTransferID",
    "lastTransferType",
    "locale",
    "mhash",
    "modifiedBy",
    "modifiedOn",
    "msisdn",
    "multiBox",
    "networkCode",
    "oldSmsPin",
    "operationType",
    "otp",
    "ownerTempTransferId",
    "phoneLanguage",
    "phoneProfile",
    "phoneProfileDesc",
    "pinGenerateAllow",
    "pinModifiedOn",
    "pinModifyFlag",
    "pinRequired",
    "pinRequiredBool",
    "pinReset",
    "prefixID",
    "primaryNumber",
    "registered",
    "requestGatewayCode",
    "rowIndex",
    "showSmsPin",
    "simProfileID",
    "smsPin",
    "tempTransferID",
    "userId",
    "userPhonesId"
})
@Generated("jsonschema2pojo")
public class UserPhoneVO {

    @JsonProperty("accessOn")
    private Boolean accessOn;
    @JsonProperty("barUserForInvalidPin")
    private Boolean barUserForInvalidPin;
    @JsonProperty("confirmSmsPin")
    private String confirmSmsPin;
    @JsonProperty("country")
    private String country;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdOn")
    private String createdOn;
    @JsonProperty("currentModifiedOn")
    private String currentModifiedOn;
    @JsonProperty("description")
    private String description;
    @JsonProperty("encryptDecryptKey")
    private String encryptDecryptKey;
    @JsonProperty("firstInvalidPinTime")
    private String firstInvalidPinTime;
    @JsonProperty("forcePinCheckReqd")
    private Boolean forcePinCheckReqd;
    @JsonProperty("idGenerate")
    private Boolean idGenerate;
    @JsonProperty("imei")
    private String imei;
    @JsonProperty("invalidPinCount")
    private Integer invalidPinCount;
    @JsonProperty("lastAccessOn")
    private String lastAccessOn;
    @JsonProperty("lastTransactionOn")
    private String lastTransactionOn;
    @JsonProperty("lastTransactionStatus")
    private String lastTransactionStatus;
    @JsonProperty("lastTransferID")
    private String lastTransferID;
    @JsonProperty("lastTransferType")
    private String lastTransferType;
    @JsonProperty("locale")
    private Locale locale;
    @JsonProperty("mhash")
    private String mhash;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("multiBox")
    private String multiBox;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("oldSmsPin")
    private String oldSmsPin;
    @JsonProperty("operationType")
    private String operationType;
    @JsonProperty("otp")
    private String otp;
    @JsonProperty("ownerTempTransferId")
    private String ownerTempTransferId;
    @JsonProperty("phoneLanguage")
    private String phoneLanguage;
    @JsonProperty("phoneProfile")
    private String phoneProfile;
    @JsonProperty("phoneProfileDesc")
    private String phoneProfileDesc;
    @JsonProperty("pinGenerateAllow")
    private String pinGenerateAllow;
    @JsonProperty("pinModifiedOn")
    private String pinModifiedOn;
    @JsonProperty("pinModifyFlag")
    private Boolean pinModifyFlag;
    @JsonProperty("pinRequired")
    private String pinRequired;
    @JsonProperty("pinRequiredBool")
    private Boolean pinRequiredBool;
    @JsonProperty("pinReset")
    private String pinReset;
    @JsonProperty("prefixID")
    private Integer prefixID;
    @JsonProperty("primaryNumber")
    private String primaryNumber;
    @JsonProperty("registered")
    private Boolean registered;
    @JsonProperty("requestGatewayCode")
    private String requestGatewayCode;
    @JsonProperty("rowIndex")
    private Integer rowIndex;
    @JsonProperty("showSmsPin")
    private String showSmsPin;
    @JsonProperty("simProfileID")
    private String simProfileID;
    @JsonProperty("smsPin")
    private String smsPin;
    @JsonProperty("tempTransferID")
    private String tempTransferID;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("userPhonesId")
    private String userPhonesId;

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

    @JsonProperty("confirmSmsPin")
    public String getConfirmSmsPin() {
        return confirmSmsPin;
    }

    @JsonProperty("confirmSmsPin")
    public void setConfirmSmsPin(String confirmSmsPin) {
        this.confirmSmsPin = confirmSmsPin;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("createdOn")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("currentModifiedOn")
    public String getCurrentModifiedOn() {
        return currentModifiedOn;
    }

    @JsonProperty("currentModifiedOn")
    public void setCurrentModifiedOn(String currentModifiedOn) {
        this.currentModifiedOn = currentModifiedOn;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("encryptDecryptKey")
    public String getEncryptDecryptKey() {
        return encryptDecryptKey;
    }

    @JsonProperty("encryptDecryptKey")
    public void setEncryptDecryptKey(String encryptDecryptKey) {
        this.encryptDecryptKey = encryptDecryptKey;
    }

    @JsonProperty("firstInvalidPinTime")
    public String getFirstInvalidPinTime() {
        return firstInvalidPinTime;
    }

    @JsonProperty("firstInvalidPinTime")
    public void setFirstInvalidPinTime(String firstInvalidPinTime) {
        this.firstInvalidPinTime = firstInvalidPinTime;
    }

    @JsonProperty("forcePinCheckReqd")
    public Boolean getForcePinCheckReqd() {
        return forcePinCheckReqd;
    }

    @JsonProperty("forcePinCheckReqd")
    public void setForcePinCheckReqd(Boolean forcePinCheckReqd) {
        this.forcePinCheckReqd = forcePinCheckReqd;
    }

    @JsonProperty("idGenerate")
    public Boolean getIdGenerate() {
        return idGenerate;
    }

    @JsonProperty("idGenerate")
    public void setIdGenerate(Boolean idGenerate) {
        this.idGenerate = idGenerate;
    }

    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    @JsonProperty("imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    @JsonProperty("invalidPinCount")
    public Integer getInvalidPinCount() {
        return invalidPinCount;
    }

    @JsonProperty("invalidPinCount")
    public void setInvalidPinCount(Integer invalidPinCount) {
        this.invalidPinCount = invalidPinCount;
    }

    @JsonProperty("lastAccessOn")
    public String getLastAccessOn() {
        return lastAccessOn;
    }

    @JsonProperty("lastAccessOn")
    public void setLastAccessOn(String lastAccessOn) {
        this.lastAccessOn = lastAccessOn;
    }

    @JsonProperty("lastTransactionOn")
    public String getLastTransactionOn() {
        return lastTransactionOn;
    }

    @JsonProperty("lastTransactionOn")
    public void setLastTransactionOn(String lastTransactionOn) {
        this.lastTransactionOn = lastTransactionOn;
    }

    @JsonProperty("lastTransactionStatus")
    public String getLastTransactionStatus() {
        return lastTransactionStatus;
    }

    @JsonProperty("lastTransactionStatus")
    public void setLastTransactionStatus(String lastTransactionStatus) {
        this.lastTransactionStatus = lastTransactionStatus;
    }

    @JsonProperty("lastTransferID")
    public String getLastTransferID() {
        return lastTransferID;
    }

    @JsonProperty("lastTransferID")
    public void setLastTransferID(String lastTransferID) {
        this.lastTransferID = lastTransferID;
    }

    @JsonProperty("lastTransferType")
    public String getLastTransferType() {
        return lastTransferType;
    }

    @JsonProperty("lastTransferType")
    public void setLastTransferType(String lastTransferType) {
        this.lastTransferType = lastTransferType;
    }

    @JsonProperty("locale")
    public Locale getLocale() {
        return locale;
    }

    @JsonProperty("locale")
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @JsonProperty("mhash")
    public String getMhash() {
        return mhash;
    }

    @JsonProperty("mhash")
    public void setMhash(String mhash) {
        this.mhash = mhash;
    }

    @JsonProperty("modifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("modifiedOn")
    public String getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("multiBox")
    public String getMultiBox() {
        return multiBox;
    }

    @JsonProperty("multiBox")
    public void setMultiBox(String multiBox) {
        this.multiBox = multiBox;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("oldSmsPin")
    public String getOldSmsPin() {
        return oldSmsPin;
    }

    @JsonProperty("oldSmsPin")
    public void setOldSmsPin(String oldSmsPin) {
        this.oldSmsPin = oldSmsPin;
    }

    @JsonProperty("operationType")
    public String getOperationType() {
        return operationType;
    }

    @JsonProperty("operationType")
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @JsonProperty("otp")
    public String getOtp() {
        return otp;
    }

    @JsonProperty("otp")
    public void setOtp(String otp) {
        this.otp = otp;
    }

    @JsonProperty("ownerTempTransferId")
    public String getOwnerTempTransferId() {
        return ownerTempTransferId;
    }

    @JsonProperty("ownerTempTransferId")
    public void setOwnerTempTransferId(String ownerTempTransferId) {
        this.ownerTempTransferId = ownerTempTransferId;
    }

    @JsonProperty("phoneLanguage")
    public String getPhoneLanguage() {
        return phoneLanguage;
    }

    @JsonProperty("phoneLanguage")
    public void setPhoneLanguage(String phoneLanguage) {
        this.phoneLanguage = phoneLanguage;
    }

    @JsonProperty("phoneProfile")
    public String getPhoneProfile() {
        return phoneProfile;
    }

    @JsonProperty("phoneProfile")
    public void setPhoneProfile(String phoneProfile) {
        this.phoneProfile = phoneProfile;
    }

    @JsonProperty("phoneProfileDesc")
    public String getPhoneProfileDesc() {
        return phoneProfileDesc;
    }

    @JsonProperty("phoneProfileDesc")
    public void setPhoneProfileDesc(String phoneProfileDesc) {
        this.phoneProfileDesc = phoneProfileDesc;
    }

    @JsonProperty("pinGenerateAllow")
    public String getPinGenerateAllow() {
        return pinGenerateAllow;
    }

    @JsonProperty("pinGenerateAllow")
    public void setPinGenerateAllow(String pinGenerateAllow) {
        this.pinGenerateAllow = pinGenerateAllow;
    }

    @JsonProperty("pinModifiedOn")
    public String getPinModifiedOn() {
        return pinModifiedOn;
    }

    @JsonProperty("pinModifiedOn")
    public void setPinModifiedOn(String pinModifiedOn) {
        this.pinModifiedOn = pinModifiedOn;
    }

    @JsonProperty("pinModifyFlag")
    public Boolean getPinModifyFlag() {
        return pinModifyFlag;
    }

    @JsonProperty("pinModifyFlag")
    public void setPinModifyFlag(Boolean pinModifyFlag) {
        this.pinModifyFlag = pinModifyFlag;
    }

    @JsonProperty("pinRequired")
    public String getPinRequired() {
        return pinRequired;
    }

    @JsonProperty("pinRequired")
    public void setPinRequired(String pinRequired) {
        this.pinRequired = pinRequired;
    }

    @JsonProperty("pinRequiredBool")
    public Boolean getPinRequiredBool() {
        return pinRequiredBool;
    }

    @JsonProperty("pinRequiredBool")
    public void setPinRequiredBool(Boolean pinRequiredBool) {
        this.pinRequiredBool = pinRequiredBool;
    }

    @JsonProperty("pinReset")
    public String getPinReset() {
        return pinReset;
    }

    @JsonProperty("pinReset")
    public void setPinReset(String pinReset) {
        this.pinReset = pinReset;
    }

    @JsonProperty("prefixID")
    public Integer getPrefixID() {
        return prefixID;
    }

    @JsonProperty("prefixID")
    public void setPrefixID(Integer prefixID) {
        this.prefixID = prefixID;
    }

    @JsonProperty("primaryNumber")
    public String getPrimaryNumber() {
        return primaryNumber;
    }

    @JsonProperty("primaryNumber")
    public void setPrimaryNumber(String primaryNumber) {
        this.primaryNumber = primaryNumber;
    }

    @JsonProperty("registered")
    public Boolean getRegistered() {
        return registered;
    }

    @JsonProperty("registered")
    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    @JsonProperty("requestGatewayCode")
    public String getRequestGatewayCode() {
        return requestGatewayCode;
    }

    @JsonProperty("requestGatewayCode")
    public void setRequestGatewayCode(String requestGatewayCode) {
        this.requestGatewayCode = requestGatewayCode;
    }

    @JsonProperty("rowIndex")
    public Integer getRowIndex() {
        return rowIndex;
    }

    @JsonProperty("rowIndex")
    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    @JsonProperty("showSmsPin")
    public String getShowSmsPin() {
        return showSmsPin;
    }

    @JsonProperty("showSmsPin")
    public void setShowSmsPin(String showSmsPin) {
        this.showSmsPin = showSmsPin;
    }

    @JsonProperty("simProfileID")
    public String getSimProfileID() {
        return simProfileID;
    }

    @JsonProperty("simProfileID")
    public void setSimProfileID(String simProfileID) {
        this.simProfileID = simProfileID;
    }

    @JsonProperty("smsPin")
    public String getSmsPin() {
        return smsPin;
    }

    @JsonProperty("smsPin")
    public void setSmsPin(String smsPin) {
        this.smsPin = smsPin;
    }

    @JsonProperty("tempTransferID")
    public String getTempTransferID() {
        return tempTransferID;
    }

    @JsonProperty("tempTransferID")
    public void setTempTransferID(String tempTransferID) {
        this.tempTransferID = tempTransferID;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("userPhonesId")
    public String getUserPhonesId() {
        return userPhonesId;
    }

    @JsonProperty("userPhonesId")
    public void setUserPhonesId(String userPhonesId) {
        this.userPhonesId = userPhonesId;
    }

}
