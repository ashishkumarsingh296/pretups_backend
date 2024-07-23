/*
 * #ResponseInterfaceDetailVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 17, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.master.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class ResponseInterfaceDetailVO implements Serializable {
    private String _resInterfaceId;
    private String _resInterfaceCode;
    private String _resInterfaceName;
    private String _resInterfaceDesc;
    private String _networkCode;
    private String _smscType;
    private String _status;
    private String _destNo;
    private String _resInterfaceHost;
    private String _resInterfacePort;
    private String _resInterfaceType;
    private String _altResInterfaceHost;
    private String _altResInterfacePort;
    private String _altResInterfaceType;
    private String _protocol;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;

    private String _networkName;
    private int _radioIndex;
    private long _lastModified;

    public String toString() {
        StringBuffer strBuff = new StringBuffer("\nInterface Id=" + _resInterfaceId);
        strBuff.append("\nInterface Code=" + _resInterfaceCode);
        strBuff.append("\nInterface Name=" + _resInterfaceName);
        strBuff.append("\nInterface Desc=" + _resInterfaceDesc);
        strBuff.append("\nNetwork code=" + _networkCode);
        strBuff.append("\nNetwork Name=" + _networkName);
        strBuff.append("\nSmsc Type=" + _smscType);
        strBuff.append("\nStatus=" + _status);
        strBuff.append("\nDest No=" + _destNo);
        strBuff.append("\nInterface Host=" + _resInterfaceHost);
        strBuff.append("\nInterface Port=" + _resInterfacePort);
        strBuff.append("\nInterface Type=" + _resInterfaceType);
        strBuff.append("\nBack Up Host=" + _altResInterfaceHost);
        strBuff.append("\nBack Up Port=" + _altResInterfacePort);
        strBuff.append("\nBack Up Type=" + _altResInterfaceType);
        strBuff.append("\nProtocol=" + _protocol);
        strBuff.append("\nCreated On=" + _createdOn);
        strBuff.append("\nCreated By=" + _createdBy);
        strBuff.append("\nModified On=" + _modifiedOn);
        strBuff.append("\nModified By=" + _modifiedBy);
        strBuff.append("\nRadio Index=" + _radioIndex);
        strBuff.append("\nLast Modified=" + _lastModified);
        return strBuff.toString();
    }

    /**
     * To get the value of altResInterfaceHost field
     * 
     * @return altResInterfaceHost.
     */
    public String getAltResInterfaceHost() {
        return _altResInterfaceHost;
    }

    /**
     * To set the value of altResInterfaceHost field
     */
    public void setAltResInterfaceHost(String altResInterfaceHost) {
        _altResInterfaceHost = altResInterfaceHost;
    }

    /**
     * To get the value of altResInterfacePort field
     * 
     * @return altResInterfacePort.
     */
    public String getAltResInterfacePort() {
        return _altResInterfacePort;
    }

    /**
     * To set the value of altResInterfacePort field
     */
    public void setAltResInterfacePort(String altResInterfacePort) {
        _altResInterfacePort = altResInterfacePort;
    }

    /**
     * To get the value of altResInterfaceType field
     * 
     * @return altResInterfaceType.
     */
    public String getAltResInterfaceType() {
        return _altResInterfaceType;
    }

    /**
     * To set the value of altResInterfaceType field
     */
    public void setAltResInterfaceType(String altResInterfaceType) {
        _altResInterfaceType = altResInterfaceType;
    }

    /**
     * To get the value of createdBy field
     * 
     * @return createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * To set the value of createdBy field
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * To get the value of createdOn field
     * 
     * @return createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * To set the value of createdOn field
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * To get the value of destNo field
     * 
     * @return destNo.
     */
    public String getDestNo() {
        return _destNo;
    }

    /**
     * To set the value of destNo field
     */
    public void setDestNo(String destNo) {
        _destNo = destNo;
    }

    /**
     * To get the value of modifiedBy field
     * 
     * @return modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * To set the value of modifiedBy field
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * To get the value of modifiedOn field
     * 
     * @return modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * To get the value of networkCode field
     * 
     * @return networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * To set the value of networkCode field
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * To get the value of protocol field
     * 
     * @return protocol.
     */
    public String getProtocol() {
        return _protocol;
    }

    /**
     * To set the value of protocol field
     */
    public void setProtocol(String protocol) {
        _protocol = protocol;
    }

    /**
     * To get the value of resInterfaceCode field
     * 
     * @return resInterfaceCode.
     */
    public String getResInterfaceCode() {
        return _resInterfaceCode;
    }

    /**
     * To set the value of resInterfaceCode field
     */
    public void setResInterfaceCode(String resInterfaceCode) {
        _resInterfaceCode = resInterfaceCode;
    }

    /**
     * To get the value of resInterfaceDesc field
     * 
     * @return resInterfaceDesc.
     */
    public String getResInterfaceDesc() {
        return _resInterfaceDesc;
    }

    /**
     * To set the value of resInterfaceDesc field
     */
    public void setResInterfaceDesc(String resInterfaceDesc) {
        _resInterfaceDesc = resInterfaceDesc;
    }

    /**
     * To get the value of resInterfaceHost field
     * 
     * @return resInterfaceHost.
     */
    public String getResInterfaceHost() {
        return _resInterfaceHost;
    }

    /**
     * To set the value of resInterfaceHost field
     */
    public void setResInterfaceHost(String resInterfaceHost) {
        _resInterfaceHost = resInterfaceHost;
    }

    /**
     * To get the value of resInterfaceId field
     * 
     * @return resInterfaceId.
     */
    public String getResInterfaceId() {
        return _resInterfaceId;
    }

    /**
     * To set the value of resInterfaceId field
     */
    public void setResInterfaceId(String resInterfaceId) {
        _resInterfaceId = resInterfaceId;
    }

    /**
     * To get the value of resInterfaceName field
     * 
     * @return resInterfaceName.
     */
    public String getResInterfaceName() {
        return _resInterfaceName;
    }

    /**
     * To set the value of resInterfaceName field
     */
    public void setResInterfaceName(String resInterfaceName) {
        _resInterfaceName = resInterfaceName;
    }

    /**
     * To get the value of resInterfacePort field
     * 
     * @return resInterfacePort.
     */
    public String getResInterfacePort() {
        return _resInterfacePort;
    }

    /**
     * To set the value of resInterfacePort field
     */
    public void setResInterfacePort(String resInterfacePort) {
        _resInterfacePort = resInterfacePort;
    }

    /**
     * To get the value of resInterfaceType field
     * 
     * @return resInterfaceType.
     */
    public String getResInterfaceType() {
        return _resInterfaceType;
    }

    /**
     * To set the value of resInterfaceType field
     */
    public void setResInterfaceType(String resInterfaceType) {
        _resInterfaceType = resInterfaceType;
    }

    /**
     * To get the value of smscType field
     * 
     * @return smscType.
     */
    public String getSmscType() {
        return _smscType;
    }

    /**
     * To set the value of smscType field
     */
    public void setSmscType(String smscType) {
        _smscType = smscType;
    }

    /**
     * To get the value of status field
     * 
     * @return status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * To set the value of status field
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * To get the value of networkName field
     * 
     * @return networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * To set the value of networkName field
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    /**
     * To get the value of radioIndex field
     * 
     * @return radioIndex.
     */
    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * To set the value of radioIndex field
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    /**
     * To get the value of lastModified field
     * 
     * @return lastModified.
     */
    public long getLastModified() {
        return _lastModified;
    }

    /**
     * To set the value of lastModified field
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }
}
