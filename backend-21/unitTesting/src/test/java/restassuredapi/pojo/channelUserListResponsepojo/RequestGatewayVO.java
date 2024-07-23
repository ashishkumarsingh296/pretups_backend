
package restassuredapi.pojo.channelUserListResponsepojo;

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
    "authType",
    "confirmPassword",
    "contentType",
    "createdBy",
    "createdOn",
    "decryptedPassword",
    "encryptionKey",
    "encryptionLevel",
    "gatewayCode",
    "lastModifiedTime",
    "loginID",
    "modifiedBy",
    "modifiedOn",
    "modifiedOnTimestamp",
    "oldPassword",
    "password",
    "port",
    "servicePort",
    "status",
    "underProcessCheckReqd",
    "updatePassword"
})
public class RequestGatewayVO {

    @JsonProperty("authType")
    private String authType;
    @JsonProperty("confirmPassword")
    private String confirmPassword;
    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdOn")
    private String createdOn;
    @JsonProperty("decryptedPassword")
    private String decryptedPassword;
    @JsonProperty("encryptionKey")
    private String encryptionKey;
    @JsonProperty("encryptionLevel")
    private String encryptionLevel;
    @JsonProperty("gatewayCode")
    private String gatewayCode;
    @JsonProperty("lastModifiedTime")
    private int lastModifiedTime;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("modifiedOnTimestamp")
    private ModifiedOnTimestamp__ modifiedOnTimestamp;
    @JsonProperty("oldPassword")
    private String oldPassword;
    @JsonProperty("password")
    private String password;
    @JsonProperty("port")
    private String port;
    @JsonProperty("servicePort")
    private String servicePort;
    @JsonProperty("status")
    private String status;
    @JsonProperty("underProcessCheckReqd")
    private String underProcessCheckReqd;
    @JsonProperty("updatePassword")
    private String updatePassword;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("authType")
    public String getAuthType() {
        return authType;
    }

    @JsonProperty("authType")
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @JsonProperty("confirmPassword")
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @JsonProperty("confirmPassword")
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    @JsonProperty("decryptedPassword")
    public String getDecryptedPassword() {
        return decryptedPassword;
    }

    @JsonProperty("decryptedPassword")
    public void setDecryptedPassword(String decryptedPassword) {
        this.decryptedPassword = decryptedPassword;
    }

    @JsonProperty("encryptionKey")
    public String getEncryptionKey() {
        return encryptionKey;
    }

    @JsonProperty("encryptionKey")
    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    @JsonProperty("encryptionLevel")
    public String getEncryptionLevel() {
        return encryptionLevel;
    }

    @JsonProperty("encryptionLevel")
    public void setEncryptionLevel(String encryptionLevel) {
        this.encryptionLevel = encryptionLevel;
    }

    @JsonProperty("gatewayCode")
    public String getGatewayCode() {
        return gatewayCode;
    }

    @JsonProperty("gatewayCode")
    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    @JsonProperty("lastModifiedTime")
    public int getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("lastModifiedTime")
    public void setLastModifiedTime(int lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
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

    @JsonProperty("modifiedOnTimestamp")
    public ModifiedOnTimestamp__ getModifiedOnTimestamp() {
        return modifiedOnTimestamp;
    }

    @JsonProperty("modifiedOnTimestamp")
    public void setModifiedOnTimestamp(ModifiedOnTimestamp__ modifiedOnTimestamp) {
        this.modifiedOnTimestamp = modifiedOnTimestamp;
    }

    @JsonProperty("oldPassword")
    public String getOldPassword() {
        return oldPassword;
    }

    @JsonProperty("oldPassword")
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("port")
    public String getPort() {
        return port;
    }

    @JsonProperty("port")
    public void setPort(String port) {
        this.port = port;
    }

    @JsonProperty("servicePort")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("underProcessCheckReqd")
    public String getUnderProcessCheckReqd() {
        return underProcessCheckReqd;
    }

    @JsonProperty("underProcessCheckReqd")
    public void setUnderProcessCheckReqd(String underProcessCheckReqd) {
        this.underProcessCheckReqd = underProcessCheckReqd;
    }

    @JsonProperty("updatePassword")
    public String getUpdatePassword() {
        return updatePassword;
    }

    @JsonProperty("updatePassword")
    public void setUpdatePassword(String updatePassword) {
        this.updatePassword = updatePassword;
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
