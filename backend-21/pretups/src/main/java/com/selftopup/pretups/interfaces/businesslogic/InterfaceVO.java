/*
 * @# InterfaceVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 10, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.interfaces.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class InterfaceVO implements Serializable {
    private String _interfaceName;
    private String _interfaceCategory;
    private String _interfaceCategoryCode;
    private String _interfaceId;
    private String _externalId;
    private String _interfaceDescription;
    private String _interfaceTypeId;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _status;
    private String _statusCode;
    private Date _closureDate;
    private String _language1Message;
    private String _language2Message;
    private int _concurrentConnection;
    private String _singleStateTransaction;
    // private long _validationTimeout;
    // private long _updateTimeOut;
    private int _radioIndex;
    private long _lastModified;
    private String _handlerClass;
    private String _statusType;// Will be either M or A.
    private long _valExpiryTime;
    private long _topUpExpiryTime;

    public String toString() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("\n Interface Id=" + _interfaceId);
        strBuff.append("\n External Id=" + _externalId);
        strBuff.append("\n Interface Description=" + _interfaceDescription);
        strBuff.append("\n Interface Type Id=" + _interfaceTypeId);
        strBuff.append("\n Status =" + _status);
        strBuff.append("\n Status Code=" + _statusCode);
        strBuff.append("\n Language1 Message=" + _language1Message);
        strBuff.append("\n Language2 Message=" + _language2Message);
        strBuff.append("\n Concurrent Connection=" + _concurrentConnection);
        strBuff.append("\n Single Stage transaction=" + _singleStateTransaction);
        // strBuff.append("\n Validation Time Out=" +_validationTimeout );
        // strBuff.append("\n Update Time Out=" + _updateTimeOut);
        strBuff.append("\n _handlerClass=" + _handlerClass);
        strBuff.append("\n _statusType=" + _statusType);
        strBuff.append("\n Validation Expiry Time=" + _valExpiryTime);
        strBuff.append("\n Update Topup Time=" + _topUpExpiryTime);
        return strBuff.toString();
    }

    // **********Getter and Setter of Status Type of Interface***************

    public String getStatusType() {
        return _statusType;
    }

    public void setStatusType(String statusType) {
        _statusType = statusType;
    }

    public String getHandlerClass() {
        return _handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        _handlerClass = handlerClass;
    }

    public InterfaceVO() {
    }

    // **********Getter and Setter of interfaceCategoryCode***************

    public String getInterfaceCategoryCode() {
        return _interfaceCategoryCode;
    }

    public void setInterfaceCategoryCode(String interfaceCategoryCode) {
        _interfaceCategoryCode = interfaceCategoryCode;
    }

    // *************************************************************************

    // *************Getter and Setter of statusCode***************************

    public String getStatusCode() {
        return _statusCode;
    }

    public void setStatusCode(String statusCode) {
        _statusCode = statusCode;
    }

    // ***************************************************************************

    // ******************Getter and Setter of interfaceName*********************

    public String getInterfaceName() {
        return _interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        _interfaceName = interfaceName;
    }

    // ***************************************************************************

    // ***************Getter and Setter of interfaceCategory********************

    public String getInterfaceCategory() {
        return _interfaceCategory;
    }

    public void setInterfaceCategory(String interfaceCategory) {
        _interfaceCategory = interfaceCategory;
    }

    // ***************************************************************************

    // ***************Getter and Setter of closureDate**************************

    public Date getClosureDate() {
        return _closureDate;
    }

    public void setClosureDate(Date closureDate) {
        _closureDate = closureDate;
    }

    // ***************************************************************************

    // **************Getter and Setter of concurrentConnection******************

    public int getConcurrentConnection() {
        return _concurrentConnection;
    }

    public void setConcurrentConnection(int concurrentConnection) {
        _concurrentConnection = concurrentConnection;
    }

    // ***************************************************************************

    // *************Getter and Setter of
    // createdBy*********************************

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    // ***************************************************************************

    // ********************Getter and Setter of
    // createdOn*************************

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    // ***************************************************************************

    // **************Getter and setter of
    // externalId******************************

    public String getExternalId() {
        return _externalId;
    }

    public void setExternalId(String externalId) {
        _externalId = externalId;
    }

    // ***************************************************************************

    // ****************Getter and Setter of
    // interfaceDescription*****************

    public String getInterfaceDescription() {
        return _interfaceDescription;
    }

    public void setInterfaceDescription(String interfaceDescription) {
        _interfaceDescription = interfaceDescription;
    }

    // ***************************************************************************

    // **************Getter and Setter of
    // interfaceId*****************************

    public String getInterfaceId() {
        return _interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        _interfaceId = interfaceId;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // interfaceTypeId***********************

    public String getInterfaceTypeId() {
        return _interfaceTypeId;
    }

    public void setInterfaceTypeId(String interfaceTypeId) {
        _interfaceTypeId = interfaceTypeId;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // language1Message***********************

    public String getLanguage1Message() {
        return _language1Message;
    }

    public void setLanguage1Message(String language1Message) {
        _language1Message = language1Message;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // language2Message***********************

    public String getLanguage2Message() {
        return _language2Message;
    }

    public void setLanguage2Message(String language2Message) {
        _language2Message = language2Message;
    }

    // ***************************************************************************

    // ***************Getter and Setter of modifiedBy***********************

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    // ***************************************************************************

    // ***************Getter and Setter of modifiedOn***********************

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // singleStateTransaction***********************

    public String getSingleStateTransaction() {
        return _singleStateTransaction;
    }

    public void setSingleStateTransaction(String singleStateTransaction) {
        _singleStateTransaction = singleStateTransaction;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // status*********************************

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // updateTimeOut**************************

    /*
     * public long getUpdateTimeOut()
     * {
     * return _updateTimeOut;
     * }
     * 
     * public void setUpdateTimeOut(long updateTimeOut)
     * {
     * _updateTimeOut = updateTimeOut;
     * }
     * 
     * //************************************************************************
     * ***
     * 
     * //***************Getter and Setter of
     * validationTimeout**********************
     * 
     * public long getValidationTimeout()
     * {
     * return _validationTimeout;
     * }
     * 
     * public void setValidationTimeout(long validationTimeout)
     * {
     * _validationTimeout = validationTimeout;
     * }
     */

    // ***************************************************************************

    // ***************Getter and Setter of
    // radioIndex******************************

    public int getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // lastModified******************************

    public long getLastModified() {
        return _lastModified;
    }

    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    // ***************************************************************************

    public long getTopUpExpiryTime() {
        return _topUpExpiryTime;
    }

    public void setTopUpExpiryTime(long topUpExpiryTime) {
        _topUpExpiryTime = topUpExpiryTime;
    }

    public long getValExpiryTime() {
        return _valExpiryTime;
    }

    public void setValExpiryTime(long valExpiryTime) {
        _valExpiryTime = valExpiryTime;
    }
}