/*
 * @# MessageGatewayVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 7, 2005 Sandeep Goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.gateway.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class MessageGatewayVO implements Serializable {

    private String _gatewayCode;

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
    private Timestamp _modifiedOnTimestamp;

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

    private RequestGatewayVO _requestGatewayVO;

    private ResponseGatewayVO _responseGatewayVO;
    private ResponseGatewayVO _alternateGatewayVO; // For storing the alternate
                                                   // gateway info

    private String _status;

    private boolean _userAuthorizationReqd = true;
    private String _reqpasswordtype = "Y";

    private String _categoryCode;

    public MessageGatewayVO() {
        _requestGatewayVO = new RequestGatewayVO();
        _responseGatewayVO = new ResponseGatewayVO();
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

    public RequestGatewayVO getRequestGatewayVO() {
        return _requestGatewayVO;
    }

    public void setRequestGatewayVO(RequestGatewayVO requestGatewayVO) {
        _requestGatewayVO = requestGatewayVO;
    }

    public ResponseGatewayVO getResponseGatewayVO() {
        return _responseGatewayVO;
    }

    public void setResponseGatewayVO(ResponseGatewayVO responseGatewayVO) {
        _responseGatewayVO = responseGatewayVO;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_gatewayCode=" + _gatewayCode);
        sbf.append(",_gatewayName=" + _gatewayName);
        sbf.append(",_gatewaySubType=" + _gatewaySubType);
        sbf.append(",_gatewayType=" + _gatewayType);
        sbf.append(",_handlerClass=" + _handlerClass);
        sbf.append(",_host=" + _host);
        sbf.append(",_networkCode=" + _networkCode);
        sbf.append(",_protocol=" + _protocol);
        sbf.append(",_binaryMsgAllowed=" + _binaryMsgAllowed);
        sbf.append(",_plainMsgAllowed=" + _plainMsgAllowed);
        sbf.append(",_accessFrom=" + _accessFrom);
        sbf.append(",_gatewaySubTypeName=" + _gatewaySubTypeName);
        sbf.append(",_createdBy=" + _createdBy);
        sbf.append(",_createdOn=" + _createdOn);
        sbf.append(",_modifiedBy=" + _modifiedBy);
        sbf.append(",_modifiedOn=" + _modifiedOn);
        sbf.append("\n_requestGatewayVO=>" + _requestGatewayVO);
        sbf.append("\n_responseGatewayVO=>" + _responseGatewayVO);
        return sbf.toString();
    }

    public boolean equalsMessageGatewayVO(MessageGatewayVO gatewayVO) {
        boolean flag = false;

        if (gatewayVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            if (gatewayVO.getRequestGatewayVO() != null && gatewayVO.getRequestGatewayVO().equals(this.getRequestGatewayVO())) {
                if (gatewayVO.getResponseGatewayVO() != null && gatewayVO.getResponseGatewayVO().equalsResponseGatewayVO(this.getResponseGatewayVO())) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public native int hashCode();

    public String logInfo() {
        StringBuffer sbf = new StringBuffer(300);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Gateway Type");
        sbf.append(middleSeperator);
        sbf.append(this.getGatewayType());

        sbf.append(startSeperator);
        sbf.append("Gateway Sub Type");
        sbf.append(middleSeperator);
        sbf.append(this.getGatewaySubType());

        sbf.append(startSeperator);
        sbf.append("Host");
        sbf.append(middleSeperator);
        sbf.append(this.getHost());

        sbf.append(startSeperator);
        sbf.append("Protocol");
        sbf.append(middleSeperator);
        sbf.append(this.getProtocol());

        sbf.append(startSeperator);
        sbf.append("Handler Class");
        sbf.append(middleSeperator);
        sbf.append(this.getHandlerClass());

        sbf.append(startSeperator);
        sbf.append("Network Code");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkCode());

        sbf.append(startSeperator);
        sbf.append("Binary Message Allowed");
        sbf.append(middleSeperator);
        sbf.append(this.getBinaryMsgAllowed());

        sbf.append(startSeperator);
        sbf.append("Plain Message Allowed");
        sbf.append(middleSeperator);
        sbf.append(this.getPlainMsgAllowed());

        sbf.append(startSeperator);
        sbf.append("Access From");
        sbf.append(middleSeperator);
        sbf.append(this.getAccessFrom());

        sbf.append(startSeperator);
        sbf.append("Gateway Sub Type Name");
        sbf.append(middleSeperator);
        sbf.append(this.getGatewaySubTypeName());

        return sbf.toString();

    }

    public String differences(MessageGatewayVO gatewayVO) {

        StringBuffer sbf = new StringBuffer(300);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.isNullString(this.getGatewayType()) && !gatewayVO.getGatewayType().equals(this.getGatewayType())) {
            sbf.append(startSeperator);
            sbf.append("Gateway Type");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getGatewayType());
            sbf.append(middleSeperator);
            sbf.append(this.getGatewayType());
        }

        if (!BTSLUtil.isNullString(this.getGatewaySubType()) && !gatewayVO.getGatewaySubType().equals(this.getGatewaySubType())) {
            sbf.append(startSeperator);
            sbf.append("Gateway Sub Type");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getGatewaySubType());
            sbf.append(middleSeperator);
            sbf.append(this.getGatewaySubType());
        }

        if (!BTSLUtil.isNullString(this.getHost()) && !gatewayVO.getHost().equals(this.getHost())) {
            sbf.append(startSeperator);
            sbf.append("Host");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getHost());
            sbf.append(middleSeperator);
            sbf.append(this.getHost());
        }

        if (!BTSLUtil.isNullString(this.getProtocol()) && !gatewayVO.getProtocol().equals(this.getProtocol())) {
            sbf.append(startSeperator);
            sbf.append("Protocol");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getProtocol());
            sbf.append(middleSeperator);
            sbf.append(this.getProtocol());
        }

        if (!BTSLUtil.isNullString(this.getHandlerClass()) && !gatewayVO.getHandlerClass().equals(this.getHandlerClass())) {
            sbf.append(startSeperator);
            sbf.append("Handler Class");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getHandlerClass());
            sbf.append(middleSeperator);
            sbf.append(this.getHandlerClass());
        }

        if (!BTSLUtil.isNullString(this.getNetworkCode()) && !gatewayVO.getNetworkCode().equals(this.getNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("Network Code");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkCode());
        }

        if (!BTSLUtil.isNullString(this.getBinaryMsgAllowed()) && !gatewayVO.getBinaryMsgAllowed().equals(this.getBinaryMsgAllowed())) {
            sbf.append(startSeperator);
            sbf.append("Binary Message Allowed");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getBinaryMsgAllowed());
            sbf.append(middleSeperator);
            sbf.append(this.getBinaryMsgAllowed());
        }
        if (!BTSLUtil.isNullString(this.getPlainMsgAllowed()) && !gatewayVO.getPlainMsgAllowed().equals(this.getPlainMsgAllowed())) {
            sbf.append(startSeperator);
            sbf.append("Plain Message Allowed");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getPlainMsgAllowed());
            sbf.append(middleSeperator);
            sbf.append(this.getPlainMsgAllowed());
        }

        if (!BTSLUtil.isNullString(this.getAccessFrom()) && !gatewayVO.getAccessFrom().equals(this.getAccessFrom())) {
            sbf.append(startSeperator);
            sbf.append("Access From");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getAccessFrom());
            sbf.append(middleSeperator);
            sbf.append(this.getAccessFrom());
        }

        if (!BTSLUtil.isNullString(this.getGatewaySubTypeName()) && !gatewayVO.getGatewaySubTypeName().equals(this.getGatewaySubTypeName())) {
            sbf.append(startSeperator);
            sbf.append("Sub Type Name");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getGatewaySubTypeName());
            sbf.append(middleSeperator);
            sbf.append(this.getGatewaySubTypeName());
        }

        return sbf.toString();
    }

   
	@Override
	public native boolean equals(Object obj);

	public ResponseGatewayVO getAlternateGatewayVO() {
        return _alternateGatewayVO;
    }

    public void setAlternateGatewayVO(ResponseGatewayVO alternateGatewayVO) {
        _alternateGatewayVO = alternateGatewayVO;
    }

    public Timestamp getModifiedOnTimestamp() {
        return _modifiedOnTimestamp;
    }

    public void setModifiedOnTimestamp(Timestamp modifiedOnTimestamp) {
        _modifiedOnTimestamp = modifiedOnTimestamp;
    }

    public String getAccessFrom() {
        return _accessFrom;
    }

    public void setAccessFrom(String accessFrom) {
        _accessFrom = accessFrom;
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
}
