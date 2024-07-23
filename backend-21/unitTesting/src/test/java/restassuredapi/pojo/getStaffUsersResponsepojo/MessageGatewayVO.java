
package restassuredapi.pojo.getStaffUsersResponsepojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "accessFrom",
    "alternateGatewayVO",
    "binaryMsgAllowed",
    "categoryCode",
    "createdBy",
    "createdOn",
    "flowType",
    "gatewayCode",
    "gatewayName",
    "gatewaySubType",
    "gatewaySubTypeDes",
    "gatewaySubTypeName",
    "gatewayType",
    "gatewayTypeDes",
    "handlerClass",
    "host",
    "lastModifiedTime",
    "modifiedBy",
    "modifiedOn",
    "modifiedOnTimestamp",
    "networkCode",
    "plainMsgAllowed",
    "protocol",
    "reqpasswordtype",
    "requestGatewayVO",
    "responseGatewayVO",
    "responseType",
    "status",
    "timeoutValue",
    "userAuthorizationReqd"
})
@Generated("jsonschema2pojo")
public class MessageGatewayVO {

    @JsonProperty("accessFrom")
    private String accessFrom;
    @JsonProperty("alternateGatewayVO")
    private AlternateGatewayVO alternateGatewayVO;
    @JsonProperty("binaryMsgAllowed")
    private String binaryMsgAllowed;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdOn")
    private String createdOn;
    @JsonProperty("flowType")
    private String flowType;
    @JsonProperty("gatewayCode")
    private String gatewayCode;
    @JsonProperty("gatewayName")
    private String gatewayName;
    @JsonProperty("gatewaySubType")
    private String gatewaySubType;
    @JsonProperty("gatewaySubTypeDes")
    private String gatewaySubTypeDes;
    @JsonProperty("gatewaySubTypeName")
    private String gatewaySubTypeName;
    @JsonProperty("gatewayType")
    private String gatewayType;
    @JsonProperty("gatewayTypeDes")
    private String gatewayTypeDes;
    @JsonProperty("handlerClass")
    private String handlerClass;
    @JsonProperty("host")
    private String host;
    @JsonProperty("lastModifiedTime")
    private Integer lastModifiedTime;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("modifiedOnTimestamp")
    private ModifiedOnTimestamp__1 modifiedOnTimestamp;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("plainMsgAllowed")
    private String plainMsgAllowed;
    @JsonProperty("protocol")
    private String protocol;
    @JsonProperty("reqpasswordtype")
    private String reqpasswordtype;
    @JsonProperty("requestGatewayVO")
    private RequestGatewayVO requestGatewayVO;
    @JsonProperty("responseGatewayVO")
    private ResponseGatewayVO responseGatewayVO;
    @JsonProperty("responseType")
    private String responseType;
    @JsonProperty("status")
    private String status;
    @JsonProperty("timeoutValue")
    private Integer timeoutValue;
    @JsonProperty("userAuthorizationReqd")
    private Boolean userAuthorizationReqd;

    @JsonProperty("accessFrom")
    public String getAccessFrom() {
        return accessFrom;
    }

    @JsonProperty("accessFrom")
    public void setAccessFrom(String accessFrom) {
        this.accessFrom = accessFrom;
    }

    @JsonProperty("alternateGatewayVO")
    public AlternateGatewayVO getAlternateGatewayVO() {
        return alternateGatewayVO;
    }

    @JsonProperty("alternateGatewayVO")
    public void setAlternateGatewayVO(AlternateGatewayVO alternateGatewayVO) {
        this.alternateGatewayVO = alternateGatewayVO;
    }

    @JsonProperty("binaryMsgAllowed")
    public String getBinaryMsgAllowed() {
        return binaryMsgAllowed;
    }

    @JsonProperty("binaryMsgAllowed")
    public void setBinaryMsgAllowed(String binaryMsgAllowed) {
        this.binaryMsgAllowed = binaryMsgAllowed;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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

    @JsonProperty("flowType")
    public String getFlowType() {
        return flowType;
    }

    @JsonProperty("flowType")
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    @JsonProperty("gatewayCode")
    public String getGatewayCode() {
        return gatewayCode;
    }

    @JsonProperty("gatewayCode")
    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    @JsonProperty("gatewayName")
    public String getGatewayName() {
        return gatewayName;
    }

    @JsonProperty("gatewayName")
    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    @JsonProperty("gatewaySubType")
    public String getGatewaySubType() {
        return gatewaySubType;
    }

    @JsonProperty("gatewaySubType")
    public void setGatewaySubType(String gatewaySubType) {
        this.gatewaySubType = gatewaySubType;
    }

    @JsonProperty("gatewaySubTypeDes")
    public String getGatewaySubTypeDes() {
        return gatewaySubTypeDes;
    }

    @JsonProperty("gatewaySubTypeDes")
    public void setGatewaySubTypeDes(String gatewaySubTypeDes) {
        this.gatewaySubTypeDes = gatewaySubTypeDes;
    }

    @JsonProperty("gatewaySubTypeName")
    public String getGatewaySubTypeName() {
        return gatewaySubTypeName;
    }

    @JsonProperty("gatewaySubTypeName")
    public void setGatewaySubTypeName(String gatewaySubTypeName) {
        this.gatewaySubTypeName = gatewaySubTypeName;
    }

    @JsonProperty("gatewayType")
    public String getGatewayType() {
        return gatewayType;
    }

    @JsonProperty("gatewayType")
    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    @JsonProperty("gatewayTypeDes")
    public String getGatewayTypeDes() {
        return gatewayTypeDes;
    }

    @JsonProperty("gatewayTypeDes")
    public void setGatewayTypeDes(String gatewayTypeDes) {
        this.gatewayTypeDes = gatewayTypeDes;
    }

    @JsonProperty("handlerClass")
    public String getHandlerClass() {
        return handlerClass;
    }

    @JsonProperty("handlerClass")
    public void setHandlerClass(String handlerClass) {
        this.handlerClass = handlerClass;
    }

    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty("lastModifiedTime")
    public Integer getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("lastModifiedTime")
    public void setLastModifiedTime(Integer lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
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
    public ModifiedOnTimestamp__1 getModifiedOnTimestamp() {
        return modifiedOnTimestamp;
    }

    @JsonProperty("modifiedOnTimestamp")
    public void setModifiedOnTimestamp(ModifiedOnTimestamp__1 modifiedOnTimestamp) {
        this.modifiedOnTimestamp = modifiedOnTimestamp;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("plainMsgAllowed")
    public String getPlainMsgAllowed() {
        return plainMsgAllowed;
    }

    @JsonProperty("plainMsgAllowed")
    public void setPlainMsgAllowed(String plainMsgAllowed) {
        this.plainMsgAllowed = plainMsgAllowed;
    }

    @JsonProperty("protocol")
    public String getProtocol() {
        return protocol;
    }

    @JsonProperty("protocol")
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @JsonProperty("reqpasswordtype")
    public String getReqpasswordtype() {
        return reqpasswordtype;
    }

    @JsonProperty("reqpasswordtype")
    public void setReqpasswordtype(String reqpasswordtype) {
        this.reqpasswordtype = reqpasswordtype;
    }

    @JsonProperty("requestGatewayVO")
    public RequestGatewayVO getRequestGatewayVO() {
        return requestGatewayVO;
    }

    @JsonProperty("requestGatewayVO")
    public void setRequestGatewayVO(RequestGatewayVO requestGatewayVO) {
        this.requestGatewayVO = requestGatewayVO;
    }

    @JsonProperty("responseGatewayVO")
    public ResponseGatewayVO getResponseGatewayVO() {
        return responseGatewayVO;
    }

    @JsonProperty("responseGatewayVO")
    public void setResponseGatewayVO(ResponseGatewayVO responseGatewayVO) {
        this.responseGatewayVO = responseGatewayVO;
    }

    @JsonProperty("responseType")
    public String getResponseType() {
        return responseType;
    }

    @JsonProperty("responseType")
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("timeoutValue")
    public Integer getTimeoutValue() {
        return timeoutValue;
    }

    @JsonProperty("timeoutValue")
    public void setTimeoutValue(Integer timeoutValue) {
        this.timeoutValue = timeoutValue;
    }

    @JsonProperty("userAuthorizationReqd")
    public Boolean getUserAuthorizationReqd() {
        return userAuthorizationReqd;
    }

    @JsonProperty("userAuthorizationReqd")
    public void setUserAuthorizationReqd(Boolean userAuthorizationReqd) {
        this.userAuthorizationReqd = userAuthorizationReqd;
    }

}
