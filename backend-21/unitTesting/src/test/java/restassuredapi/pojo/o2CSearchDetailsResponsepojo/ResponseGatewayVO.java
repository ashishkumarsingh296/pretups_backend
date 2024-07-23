
package restassuredapi.pojo.o2CSearchDetailsResponsepojo;

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
    "confirmPassword",
    "createdBy",
    "createdOn",
    "decryptedPassword",
    "destNo",
    "gatewayCode",
    "lastModifiedTime",
    "loginID",
    "modifiedBy",
    "modifiedOn",
    "modifiedOnTimestamp",
    "oldPassword",
    "password",
    "path",
    "port",
    "servicePort",
    "status",
    "timeOut",
    "updatePassword"
})
public class ResponseGatewayVO {

    @JsonProperty("confirmPassword")
    private String confirmPassword;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdOn")
    private String createdOn;
    @JsonProperty("decryptedPassword")
    private String decryptedPassword;
    @JsonProperty("destNo")
    private String destNo;
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
    private ModifiedOnTimestamp___ modifiedOnTimestamp;
    @JsonProperty("oldPassword")
    private String oldPassword;
    @JsonProperty("password")
    private String password;
    @JsonProperty("path")
    private String path;
    @JsonProperty("port")
    private String port;
    @JsonProperty("servicePort")
    private String servicePort;
    @JsonProperty("status")
    private String status;
    @JsonProperty("timeOut")
    private int timeOut;
    @JsonProperty("updatePassword")
    private String updatePassword;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("confirmPassword")
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @JsonProperty("confirmPassword")
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

    @JsonProperty("destNo")
    public String getDestNo() {
        return destNo;
    }

    @JsonProperty("destNo")
    public void setDestNo(String destNo) {
        this.destNo = destNo;
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
    public ModifiedOnTimestamp___ getModifiedOnTimestamp() {
        return modifiedOnTimestamp;
    }

    @JsonProperty("modifiedOnTimestamp")
    public void setModifiedOnTimestamp(ModifiedOnTimestamp___ modifiedOnTimestamp) {
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

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
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

    @JsonProperty("timeOut")
    public int getTimeOut() {
        return timeOut;
    }

    @JsonProperty("timeOut")
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
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
