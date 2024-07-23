/**
 * @# WhiteListVO
 *    This class is the controller class of the Channel user Module.
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    March 28, 2006 Ankit Zindal Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.whitelist.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.selftopup.common.ListValueVO;

public class WhiteListVO implements Serializable {

    private String _networkCode;
    private String _msisdn;
    private String _accountID;
    private Date _entryDate;
    private String _entryDateStr;
    private String _accountStatus;
    private String _serviceClassCode;
    private String _serviceClassName;
    private String _movementCode;
    private String _movementName;
    private long _creditLimit;
    private String _creditLimitStr;
    private String _interfaceID;
    private String _interfaceName;
    private String _externalInterfaceCode;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private Date _activatedOn;
    private String _activatedOnStr;
    private String _activatedBy;
    private String _status;
    private String _statusStr;
    private String _imsi;
    private String _language;
    private String _country;

    // For logging in process;
    private String _errorCode;
    private String _ohterInfo;
    private Date _startDate;
    private Date _endDate;
    private int _totalRecords;
    private int _processedRecords;
    private int _unProccessedRecords;
    private String _insert;
    private String _delete;
    private String _update;
    private String _requestString;

    private ListValueVO _listValueVO;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_networkCode  =" + _networkCode);
        sbf.append(",_msisdn  =" + _msisdn);
        sbf.append(",_accountID =" + _accountID);
        sbf.append(",_entryDate =" + _entryDate);
        sbf.append(",_accountStatus =" + _accountStatus);
        sbf.append(",_creditLimit =" + _creditLimit);
        sbf.append(",_interfaceID =" + _interfaceID);
        sbf.append(",_externalInterfaceCode =" + _externalInterfaceCode);
        sbf.append(",_createdOn =" + _createdOn);
        sbf.append(",_createdBy =" + _createdBy);
        sbf.append(", _status =" + _status);
        sbf.append(", _imsi =" + _imsi);
        sbf.append(", _language =" + _language);
        sbf.append(", _country =" + _country);
        sbf.append(",_errorCode=" + _errorCode);
        sbf.append(",_requestString=" + _requestString);
        return sbf.toString();
    }

    /**
     * @return Returns the requestString.
     */
    public String getRequestString() {
        return _requestString;
    }

    /**
     * @param requestString
     *            The requestString to set.
     */
    public void setRequestString(String requestString) {
        _requestString = requestString;
    }

    /**
     * @return Returns the totalRecords.
     */
    public int getTotalRecords() {
        return _totalRecords;
    }

    /**
     * @param totalRecords
     *            The totalRecords to set.
     */
    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
    }

    /**
     * @return Returns the endDate.
     */
    public Date getEndDate() {
        return _endDate;
    }

    /**
     * @param endDate
     *            The endDate to set.
     */
    public void setEndDate(Date endDate) {
        _endDate = endDate;
    }

    /**
     * @return Returns the startDate.
     */
    public Date getStartDate() {
        return _startDate;
    }

    /**
     * @param startDate
     *            The startDate to set.
     */
    public void setStartDate(Date startDate) {
        _startDate = startDate;
    }

    /**
     * @return Returns the errorCode.
     */
    public String getErrorCode() {
        return _errorCode;
    }

    /**
     * @param errorCode
     *            The errorCode to set.
     */
    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    /**
     * @return Returns the ohterInfo.
     */
    public String getOhterInfo() {
        return _ohterInfo;
    }

    /**
     * @param ohterInfo
     *            The ohterInfo to set.
     */
    public void setOhterInfo(String ohterInfo) {
        _ohterInfo = ohterInfo;
    }

    /**
     * @return Returns the delete.
     */
    public String getDelete() {
        return _delete;
    }

    /**
     * @param delete
     *            The delete to set.
     */
    public void setDelete(String delete) {
        _delete = delete;
    }

    /**
     * @return Returns the insert.
     */
    public String getInsert() {
        return _insert;
    }

    /**
     * @param insert
     *            The insert to set.
     */
    public void setInsert(String insert) {
        _insert = insert;
    }

    /**
     * @return Returns the update.
     */
    public String getUpdate() {
        return _update;
    }

    /**
     * @param update
     *            The update to set.
     */
    public void setUpdate(String update) {
        _update = update;
    }

    /**
     * @return Returns the processedRecords.
     */
    public int getProcessedRecords() {
        return _processedRecords;
    }

    /**
     * @param processedRecords
     *            The processedRecords to set.
     */
    public void setProcessedRecords(int processedRecords) {
        _processedRecords = processedRecords;
    }

    /**
     * @return Returns the unProccessedRecords.
     */
    public int getUnProccessedRecords() {
        return _unProccessedRecords;
    }

    /**
     * @param unProccessedRecords
     *            The unProccessedRecords to set.
     */
    public void setUnProccessedRecords(int unProccessedRecords) {
        _unProccessedRecords = unProccessedRecords;
    }

    /**
     * @return Returns the entryDateStr.
     */
    public String getEntryDateStr() {
        return _entryDateStr;
    }

    /**
     * @param entryDateStr
     *            The entryDateStr to set.
     */
    public void setEntryDateStr(String entryDateStr) {
        _entryDateStr = entryDateStr;
    }

    /**
     * @return Returns the statusStr.
     */
    public String getStatusStr() {
        return _statusStr;
    }

    /**
     * @param statusStr
     *            The statusStr to set.
     */
    public void setStatusStr(String statusStr) {
        _statusStr = statusStr;
    }

    /**
     * @return Returns the activatedOnStr.
     */
    public String getActivatedOnStr() {
        return _activatedOnStr;
    }

    /**
     * @param activatedOnStr
     *            The activatedOnStr to set.
     */
    public void setActivatedOnStr(String activatedOnStr) {
        _activatedOnStr = activatedOnStr;
    }

    /**
     * @return Returns the movementName.
     */
    public String getMovementName() {
        return _movementName;
    }

    /**
     * @param movementName
     *            The movementName to set.
     */
    public void setMovementName(String movementName) {
        _movementName = movementName;
    }

    /**
     * @return Returns the serviceClassName.
     */
    public String getServiceClassName() {
        return _serviceClassName;
    }

    /**
     * @param serviceClassName
     *            The serviceClassName to set.
     */
    public void setServiceClassName(String serviceClassName) {
        _serviceClassName = serviceClassName;
    }

    /**
     * @return Returns the activatedBy.
     */
    public String getActivatedBy() {
        return _activatedBy;
    }

    /**
     * @param activatedBy
     *            The activatedBy to set.
     */
    public void setActivatedBy(String activatedBy) {
        _activatedBy = activatedBy;
    }

    /**
     * @return Returns the activatedOn.
     */
    public Date getActivatedOn() {
        return _activatedOn;
    }

    /**
     * @param activatedOn
     *            The activatedOn to set.
     */
    public void setActivatedOn(Date activatedOn) {
        _activatedOn = activatedOn;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the movementCode.
     */
    public String getMovementCode() {
        return _movementCode;
    }

    /**
     * @param movementCode
     *            The movementCode to set.
     */
    public void setMovementCode(String movementCode) {
        _movementCode = movementCode;
    }

    /**
     * @return Returns the serviceClassCode.
     */
    public String getServiceClassCode() {
        return _serviceClassCode;
    }

    /**
     * @param serviceClassCode
     *            The serviceClassCode to set.
     */
    public void setServiceClassCode(String serviceClassCode) {
        _serviceClassCode = serviceClassCode;
    }

    /**
     * @return Returns the interfaceName.
     */
    public String getInterfaceName() {
        return _interfaceName;
    }

    /**
     * @param interfaceName
     *            The interfaceName to set.
     */
    public void setInterfaceName(String interfaceName) {
        _interfaceName = interfaceName;
    }

    /**
     * @return Returns the creditLimitStr.
     */
    public String getCreditLimitStr() {
        return _creditLimitStr;
    }

    /**
     * @param creditLimitStr
     *            The creditLimitStr to set.
     */
    public void setCreditLimitStr(String creditLimitStr) {
        _creditLimitStr = creditLimitStr;
    }

    /**
     * @return Returns the accountID.
     */
    public String getAccountID() {
        return _accountID;
    }

    /**
     * @param accountID
     *            The accountID to set.
     */
    public void setAccountID(String accountID) {
        _accountID = accountID;
    }

    /**
     * @return Returns the accountStatus.
     */
    public String getAccountStatus() {
        return _accountStatus;
    }

    /**
     * @param accountStatus
     *            The accountStatus to set.
     */
    public void setAccountStatus(String accountStatus) {
        _accountStatus = accountStatus;
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the creditLimit.
     */
    public long getCreditLimit() {
        return _creditLimit;
    }

    /**
     * @param creditLimit
     *            The creditLimit to set.
     */
    public void setCreditLimit(long creditLimit) {
        _creditLimit = creditLimit;
    }

    /**
     * @return Returns the entryDate.
     */
    public Date getEntryDate() {
        return _entryDate;
    }

    /**
     * @param entryDate
     *            The entryDate to set.
     */
    public void setEntryDate(Date entryDate) {
        _entryDate = entryDate;
    }

    /**
     * @return Returns the externalInterfaceCode.
     */
    public String getExternalInterfaceCode() {
        return _externalInterfaceCode;
    }

    /**
     * @param externalInterfaceCode
     *            The externalInterfaceCode to set.
     */
    public void setExternalInterfaceCode(String externalInterfaceCode) {
        _externalInterfaceCode = externalInterfaceCode;
    }

    /**
     * @return Returns the interfaceID.
     */
    public String getInterfaceID() {
        return _interfaceID;
    }

    /**
     * @param interfaceID
     *            The interfaceID to set.
     */
    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the listValueVO.
     */
    public ListValueVO getListValueVO() {
        return _listValueVO;
    }

    /**
     * @param listValueVO
     *            The listValueVO to set.
     */
    public void setListValueVO(ListValueVO listValueVO) {
        _listValueVO = listValueVO;
    }

    /**
     * @return Returns the imsi.
     */
    public String getImsi() {
        return _imsi;
    }

    /**
     * @param imsi
     *            The imsi to set.
     */
    public void setImsi(String imsi) {
        _imsi = imsi;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return _language;
    }

    /**
     * @param language
     *            The language to set.
     */
    public void setLanguage(String language) {
        _language = language;
    }

}
