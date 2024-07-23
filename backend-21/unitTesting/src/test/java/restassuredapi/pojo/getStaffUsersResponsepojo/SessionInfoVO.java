
package restassuredapi.pojo.getStaffUsersResponsepojo;

import javax.annotation.Generated;
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
@Generated("jsonschema2pojo")
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
    private Integer totalHit;
    @JsonProperty("underProcess")
    private Boolean underProcess;
    @JsonProperty("underProcessHit")
    private Integer underProcessHit;

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
    public Integer getTotalHit() {
        return totalHit;
    }

    @JsonProperty("totalHit")
    public void setTotalHit(Integer totalHit) {
        this.totalHit = totalHit;
    }

    @JsonProperty("underProcess")
    public Boolean getUnderProcess() {
        return underProcess;
    }

    @JsonProperty("underProcess")
    public void setUnderProcess(Boolean underProcess) {
        this.underProcess = underProcess;
    }

    @JsonProperty("underProcessHit")
    public Integer getUnderProcessHit() {
        return underProcessHit;
    }

    @JsonProperty("underProcessHit")
    public void setUnderProcessHit(Integer underProcessHit) {
        this.underProcessHit = underProcessHit;
    }

}
