/*
 * #MessageGatewayMappingVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 9, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.gateway.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class MessageGatewayMappingVO implements Serializable {

    private String _requestGatewayName;
    private String _requestGatewayCode;
    private String _responseGatewayCode;
    private String _altresponseGatewayCode;
    private String _responseGatewayName;
    private String _altresponseGatewayName;
    private String _modifyFlag;
    private Date _modifiedOn;
    private String _multiBox;
    private long _lastModified;

    public MessageGatewayMappingVO() {
    }

    public MessageGatewayMappingVO(MessageGatewayMappingVO p_mappingVO) {
        this._requestGatewayName = p_mappingVO.getRequestGatewayName();
        this._requestGatewayCode = p_mappingVO.getRequestGatewayCode();
        this._responseGatewayCode = p_mappingVO.getResponseGatewayCode();
        this._altresponseGatewayCode = p_mappingVO.getAltresponseGatewayCode();
        this._responseGatewayName = p_mappingVO.getResponseGatewayName();
        this._altresponseGatewayName = p_mappingVO.getAltresponseGatewayName();
        this._modifiedOn = p_mappingVO.getModifiedOn();
        this._lastModified = p_mappingVO.getLastModified();
    }

    public String toString() {
        StringBuffer strBuff = new StringBuffer("\nRequest Gateway Code=" + _requestGatewayCode);
        strBuff.append("\n Response Gateway Code=" + _responseGatewayCode);
        strBuff.append("\n Alt Response Gateway Code=" + _altresponseGatewayCode);
        strBuff.append("\n Modify Flag=" + _modifyFlag);
        strBuff.append("\n Modified On=" + _modifiedOn);
        strBuff.append("\n MultiBox=" + _multiBox);
        strBuff.append("\n Last Modified=" + _lastModified);
        return strBuff.toString();
    }

    public long getLastModified() {
        return _lastModified;
    }

    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    public String getMultiBox() {
        return _multiBox;
    }

    public void setMultiBox(String multiBox) {
        _multiBox = multiBox;
    }

    public String getAltresponseGatewayCode() {
        return _altresponseGatewayCode;
    }

    public void setAltresponseGatewayCode(String altresponseGatewayCode) {
        _altresponseGatewayCode = altresponseGatewayCode;
    }

    public String getAltresponseGatewayName() {
        return _altresponseGatewayName;
    }

    public void setAltresponseGatewayName(String altresponseGatewayName) {
        _altresponseGatewayName = altresponseGatewayName;
    }

    public String getRequestGatewayCode() {
        return _requestGatewayCode;
    }

    public void setRequestGatewayCode(String requestGatewayCode) {
        _requestGatewayCode = requestGatewayCode;
    }

    public String getRequestGatewayName() {
        return _requestGatewayName;
    }

    public void setRequestGatewayName(String requestGatewayName) {
        _requestGatewayName = requestGatewayName;
    }

    public String getResponseGatewayCode() {
        return _responseGatewayCode;
    }

    public void setResponseGatewayCode(String responseGatewayCode) {
        _responseGatewayCode = responseGatewayCode;
    }

    public String getResponseGatewayName() {
        return _responseGatewayName;
    }

    public void setResponseGatewayName(String responseGatewayName) {
        _responseGatewayName = responseGatewayName;
    }

    public String getModifyFlag() {
        return _modifyFlag;
    }

    public void setModifyFlag(String modifyFlag) {
        _modifyFlag = modifyFlag;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }
}
