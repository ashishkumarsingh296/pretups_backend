package com.restapi.superadminVO;

import java.util.Date;

import com.btsl.pretups.gateway.businesslogic.RequestGatewayVO;
import com.btsl.pretups.gateway.businesslogic.ResponseGatewayVO;

public class MessGatewayVO {
	
	private String _gatewayCode=null;

    private String _gatewayName;

    private String _gatewayType;

    private String _gatewaySubType;

    private String _host;

    private String _protocol;

    private String _handlerClass;

    private String _networkCode;

    private Date _createdOn;

    private String _createdBy;

    private Date _modifiedOn;
    
    private String _modifiedBy;
    private String _plainMsgAllowed;
    private String _binaryMsgAllowed;
    private String _gatewaySubTypeName;
    private String _accessFrom;
    private String _flowType;
    private String _responseType;
    private long _timeoutValue;

    private long _lastModifiedTime = 0;// this field is to keep the last
                                       // modified time for the transaction
                                       // control during the transaction

    private String _gatewayTypeDes; // to store the description of the gateway
                                    // type
    private String _gatewaySubTypeDes;// to store the description of the gateway
                                      // sub type

    private ReqGatewayVO _reqGatewayVO;

    private ResGatewayVO _resGatewayVO;
    
    private ResGatewayVO _altGatewayVO; // For storing the alternate
    									// gateway info
    
    private String _status;

    private boolean _userAuthorizationReqd = true;
    private String _reqpasswordtype = "Y";

    private String _categoryCode;
    
    

	public MessGatewayVO() {
        _reqGatewayVO = new ReqGatewayVO();
        _resGatewayVO = new ResGatewayVO();
    }
    
    public String getGatewaySubTypeDes() {
        return _gatewaySubTypeDes;
    }

    public void setGatewaySubTypeDes(String gatewaySubTypeDes) {
        _gatewaySubTypeDes = gatewaySubTypeDes;
    }

    public String getGatewayTypeDes() {
        return _gatewayTypeDes;
    }

    public void setGatewayTypeDes(String gatewayTypeDes) {
        _gatewayTypeDes = gatewayTypeDes;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getGatewayCode() {
        return _gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        _gatewayCode = gatewayCode;
    }

    public String getGatewayName() {
        return _gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        _gatewayName = gatewayName;
    }

    public String getGatewaySubType() {
        return _gatewaySubType;
    }

    public void setGatewaySubType(String gatewaySubType) {
        _gatewaySubType = gatewaySubType;
    }

    public String getGatewayType() {
        return _gatewayType;
    }

    public void setGatewayType(String gatewayType) {
        _gatewayType = gatewayType;
    }

    public String getHandlerClass() {
        return _handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        _handlerClass = handlerClass;
    }

    public String getHost() {
        return _host;
    }

    public void setHost(String host) {
        _host = host;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getProtocol() {
        return _protocol;
    }

    public void setProtocol(String protocol) {
        _protocol = protocol;
    }

    public ReqGatewayVO getReqGatewayVO() {
        return _reqGatewayVO;
    }

    public void setReqGatewayVO(ReqGatewayVO reqGatewayVO) {
        _reqGatewayVO = reqGatewayVO;
    }

    public ResGatewayVO getResGatewayVO() {
        return _resGatewayVO;
    }

    public void setResGatewayVO(ResGatewayVO resGatewayVO) {
        _resGatewayVO = resGatewayVO;
    }
    
    public String getBinaryMsgAllowed() {
        return _binaryMsgAllowed;
    }

    public void setBinaryMsgAllowed(String binaryMsgAllowed) {
        _binaryMsgAllowed = binaryMsgAllowed;
    }

    public String getGatewaySubTypeName() {
        return _gatewaySubTypeName;
    }

    public void setGatewaySubTypeName(String gatewaySubTypeName) {
        _gatewaySubTypeName = gatewaySubTypeName;
    }

    public String getPlainMsgAllowed() {
        return _plainMsgAllowed;
    }

    public void setPlainMsgAllowed(String plainMsgAllowed) {
        _plainMsgAllowed = plainMsgAllowed;
    }

    public String getFlowType() {
        return _flowType;
    }

    public void setFlowType(String flowType) {
        _flowType = flowType;
    }

    public String getResponseType() {
        return _responseType;
    }

    public void setResponseType(String responseType) {
        _responseType = responseType;
    }

    public long getTimeoutValue() {
        return _timeoutValue;
    }

    public void setTimeoutValue(long timeoutValue) {
        _timeoutValue = timeoutValue;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public boolean isUserAuthorizationReqd() {
        return _userAuthorizationReqd;
    }

    public void setUserAuthorizationReqd(boolean userAuthorizationReqd) {
        _userAuthorizationReqd = userAuthorizationReqd;
    }

    public String getReqpasswordtype() {
        return _reqpasswordtype;
    }

    public void setReqpasswordtype(String _reqpasswordtype) {
        this._reqpasswordtype = _reqpasswordtype;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }
    
    public String getAccessFrom() {
        return _accessFrom;
    }

    public void setAccessFrom(String accessFrom) {
        _accessFrom = accessFrom;
    }

    public ResGatewayVO getAltGatewayVO() {
        return _altGatewayVO;
    }

    public void setAltGatewayVO(ResGatewayVO alternateGatewayVO) {
        _altGatewayVO = alternateGatewayVO;
    }
    
    
    

}
