
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
    "cookieID",
    "currentModuleCode",
    "currentPageCode",
    "currentPageName",
    "currentRoleCode",
    "messageGatewayVO",
    "remoteAddr",
    "remoteHost",
    "roleHitTimeMap",
    "sessionID",
    "totalHit",
    "underProcess",
    "underProcessHit"
})
public class SessionInfoVO {

    @JsonProperty("cookieID")
    private String cookieID;
    @JsonProperty("currentModuleCode")
    private String currentModuleCode;
    @JsonProperty("currentPageCode")
    private String currentPageCode;
    @JsonProperty("currentPageName")
    private String currentPageName;
    @JsonProperty("currentRoleCode")
    private String currentRoleCode;
    @JsonProperty("messageGatewayVO")
    private MessageGatewayVO messageGatewayVO;
    @JsonProperty("remoteAddr")
    private String remoteAddr;
    @JsonProperty("remoteHost")
    private String remoteHost;
    @JsonProperty("roleHitTimeMap")
    private RoleHitTimeMap roleHitTimeMap;
    @JsonProperty("sessionID")
    private String sessionID;
    @JsonProperty("totalHit")
    private int totalHit;
    @JsonProperty("underProcess")
    private boolean underProcess;
    @JsonProperty("underProcessHit")
    private int underProcessHit;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cookieID")
    public String getCookieID() {
        return cookieID;
    }

    @JsonProperty("cookieID")
    public void setCookieID(String cookieID) {
        this.cookieID = cookieID;
    }

    @JsonProperty("currentModuleCode")
    public String getCurrentModuleCode() {
        return currentModuleCode;
    }

    @JsonProperty("currentModuleCode")
    public void setCurrentModuleCode(String currentModuleCode) {
        this.currentModuleCode = currentModuleCode;
    }

    @JsonProperty("currentPageCode")
    public String getCurrentPageCode() {
        return currentPageCode;
    }

    @JsonProperty("currentPageCode")
    public void setCurrentPageCode(String currentPageCode) {
        this.currentPageCode = currentPageCode;
    }

    @JsonProperty("currentPageName")
    public String getCurrentPageName() {
        return currentPageName;
    }

    @JsonProperty("currentPageName")
    public void setCurrentPageName(String currentPageName) {
        this.currentPageName = currentPageName;
    }

    @JsonProperty("currentRoleCode")
    public String getCurrentRoleCode() {
        return currentRoleCode;
    }

    @JsonProperty("currentRoleCode")
    public void setCurrentRoleCode(String currentRoleCode) {
        this.currentRoleCode = currentRoleCode;
    }

    @JsonProperty("messageGatewayVO")
    public MessageGatewayVO getMessageGatewayVO() {
        return messageGatewayVO;
    }

    @JsonProperty("messageGatewayVO")
    public void setMessageGatewayVO(MessageGatewayVO messageGatewayVO) {
        this.messageGatewayVO = messageGatewayVO;
    }

    @JsonProperty("remoteAddr")
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @JsonProperty("remoteAddr")
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @JsonProperty("remoteHost")
    public String getRemoteHost() {
        return remoteHost;
    }

    @JsonProperty("remoteHost")
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    @JsonProperty("roleHitTimeMap")
    public RoleHitTimeMap getRoleHitTimeMap() {
        return roleHitTimeMap;
    }

    @JsonProperty("roleHitTimeMap")
    public void setRoleHitTimeMap(RoleHitTimeMap roleHitTimeMap) {
        this.roleHitTimeMap = roleHitTimeMap;
    }

    @JsonProperty("sessionID")
    public String getSessionID() {
        return sessionID;
    }

    @JsonProperty("sessionID")
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @JsonProperty("totalHit")
    public int getTotalHit() {
        return totalHit;
    }

    @JsonProperty("totalHit")
    public void setTotalHit(int totalHit) {
        this.totalHit = totalHit;
    }

    @JsonProperty("underProcess")
    public boolean isUnderProcess() {
        return underProcess;
    }

    @JsonProperty("underProcess")
    public void setUnderProcess(boolean underProcess) {
        this.underProcess = underProcess;
    }

    @JsonProperty("underProcessHit")
    public int getUnderProcessHit() {
        return underProcessHit;
    }

    @JsonProperty("underProcessHit")
    public void setUnderProcessHit(int underProcessHit) {
        this.underProcessHit = underProcessHit;
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
