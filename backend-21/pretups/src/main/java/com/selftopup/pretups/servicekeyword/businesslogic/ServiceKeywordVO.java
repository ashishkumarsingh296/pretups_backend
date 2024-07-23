/*
 * @# ServiceKeywordVO.java
 * This class is used as a travelling object in the SERVICE MODULE.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 10, 2005 sandeep.goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.servicekeyword.businesslogic;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is used in the SERVICE MODULE as a travelling object.
 */
public class ServiceKeywordVO implements Serializable {
    /**
     * Field _serviceKeywordID. This field is used to map to service keyword ID
     */
    private String _serviceKeywordID = null;

    /**
     * Field _serviceType. This field is used to map to service type
     */
    private String _serviceType = null;

    /**
     * Field _keyword. This field is used to map to keyword
     */
    private String _keyword = null;

    /**
     * Field _interface. This field is used to map to request interface type
     */
    private String _interface = null;

    /**
     * Field _receivePort. This field is used to map to service port
     */
    private String _receivePort = null;

    /**
     * Field _responseCode. This field is used to map to response interface code
     */
    private String _responseCode = null;

    /**
     * Field _name. This field is used to map to name
     */
    private String _name = null;

    /**
     * Field _status. This field is used to map to status code
     */
    private String _status = null;

    /**
     * Field _menu. This field is used to map to menu
     */
    private String _menu = null;

    /**
     * Field _subMenu. This field is used to map to submenu
     */
    private String _subMenu = null;

    /**
     * Field _allowedVersion. This field is used to map to allowed version
     */
    private String _allowedVersion = null;

    /**
     * Field _modifyAllowed. This field is used to map to modified allowed
     */
    private String _modifyAllowed = null;

    /**
     * Field _createdBy. This field is used to map to created by
     */
    private String _createdBy = null;

    /**
     * Field _createdOn. This field is used to map to created on
     */
    private Date _createdOn = null;

    /**
     * Field _modifiedBy. This field is used to map to modified by
     */
    private String _modifiedBy = null;

    /**
     * Field _modifiedOn. This field is used to map to modified on
     */
    private Date _modifiedOn = null;

    /**
     * Field _lastModifiedTime. This field is used to check that the record is
     * modified during the transaction.
     */
    private long _lastModifiedTime;

    private String _statusDesc;
    private String _interfaceDesc;
    private String _moduleDesc;

    // CR 000009 Sub Keyword Fields and its applicablility
    private boolean _subKeywordApplicable = false;
    private String _subKeyword;

    // Key word added for suspend resume service By sanjeew date 25/07/07
    private String _moduleCode;
    private String _language1Message;
    private String _language2Message;
    private long _lastModified;
    private String _sender_network;

    /**
     * Constructor for ServiceVO.
     */
    public ServiceKeywordVO() {

    }

    /**
     * This method gives the value of allowedVersion
     * 
     * @return String
     */
    public String getAllowedVersion() {
        return _allowedVersion;
    }

    /**
     * This method is used to set the value of allowedVersion.
     * 
     * @param allowedVersion
     */
    public void setAllowedVersion(String allowedVersion) {
        _allowedVersion = allowedVersion;
    }

    /**
     * This method gives the value of interface
     * 
     * @return String
     */
    public String getInterface() {
        return _interface;
    }

    /**
     * This method is used to set the value of interface.
     * 
     * @param interface1
     */
    public void setInterface(String interface1) {
        _interface = interface1;
    }

    /**
     * This method gives the value of keyword
     * 
     * @return String
     */
    public String getKeyword() {
        return _keyword;
    }

    /**
     * This method is used to set the value of keyword.
     * 
     * @param keyword
     */
    public void setKeyword(String keyword) {
        _keyword = keyword;
    }

    /**
     * This method gives the value of menu
     * 
     * @return String
     */
    public String getMenu() {
        return _menu;
    }

    /**
     * This method is used to set the value of menu.
     * 
     * @param menu
     */
    public void setMenu(String menu) {
        _menu = menu;
    }

    /**
     * This method gives the value of modifyAllowed
     * 
     * @return String
     */
    public String getModifyAllowed() {
        return _modifyAllowed;
    }

    /**
     * This method is used to set the value of modifyAllowed.
     * 
     * @param modifyAllowed
     */
    public void setModifyAllowed(String modifyAllowed) {
        _modifyAllowed = modifyAllowed;
    }

    /**
     * This method gives the value of name
     * 
     * @return String
     */
    public String getName() {
        return _name;
    }

    /**
     * This method is used to set the value of name.
     * 
     * @param name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method gives the value of serviceType
     * 
     * @return String
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * This method is used to set the value of serviceType.
     * 
     * @param serviceType
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * This method gives the value of status
     * 
     * @return String
     */
    public String getStatus() {
        return _status;
    }

    /**
     * This method is used to set the value of status.
     * 
     * @param status
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * This method gives the value of subMenu
     * 
     * @return String
     */
    public String getSubMenu() {
        return _subMenu;
    }

    /**
     * This method is used to set the value of subMenu.
     * 
     * @param subMenu
     */
    public void setSubMenu(String subMenu) {
        _subMenu = subMenu;
    }

    /**
     * This method gives the value of lastModifiedOn
     * 
     * @return long
     */
    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    /**
     * This method is used to set the value of lastModifiedOn.
     * 
     * @param lastModifiedOn
     */
    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    /**
     * This method gives the value of receivePort
     * 
     * @return String
     */
    public String getReceivePort() {
        return _receivePort;
    }

    /**
     * This method is used to set the value of receivePort.
     * 
     * @param receivePort
     */
    public void setReceivePort(String receivePort) {
        _receivePort = receivePort;
    }

    /**
     * This method gives the value of responseCode
     * 
     * @return String
     */
    public String getResponseCode() {
        return _responseCode;
    }

    /**
     * This method is used to set the value of responseCode.
     * 
     * @param responseCode
     */
    public void setResponseCode(String responseCode) {
        _responseCode = responseCode;
    }

    /**
     * This method gives the value of createdBy
     * 
     * @return String
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * This method is used to set the value of createdBy.
     * 
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * This method gives the value of createdOn
     * 
     * @return Date
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * This method is used to set the value of createdOn.
     * 
     * @param createdOn
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * This method gives the value of modifiedBy
     * 
     * @return String
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * This method is used to set the value of modifiedBy.
     * 
     * @param modifiedBy
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * This method gives the value of modifiedOn
     * 
     * @return Date
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * This method is used to set the value of modifiedOn.
     * 
     * @param modifiedOn
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * Method toString. This method is used to display all of the information of
     * the object of the VO class.
     * 
     * @return String
     */
    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("serviceKeywordID=" + _serviceKeywordID);
        sbf.append(",serviceType=" + _serviceType);
        sbf.append(",keyword=" + _keyword);
        sbf.append(",receivePort=" + _receivePort);
        sbf.append(",responseCode=" + _responseCode);
        sbf.append(",interface=" + _interface);
        sbf.append(",name=" + _name);
        sbf.append(",status=" + _status);
        sbf.append(",menu" + _menu);
        sbf.append(",subMenu=" + _subMenu);
        sbf.append(",allowedVersion=" + _allowedVersion);
        sbf.append(",modifyAllowed=" + _modifyAllowed);
        sbf.append(",moduleCode=" + _moduleCode);
        sbf.append(",language1Message=" + _language1Message);
        sbf.append(",language2Message=" + _language2Message);
        sbf.append(",lastModified=" + _lastModified);
        sbf.append(",sender_network=" + _sender_network);
        return sbf.toString();
    }

    /**
     * This method gives the value of serviceKeywordID
     * 
     * @return String
     */
    public String getServiceKeywordID() {
        return _serviceKeywordID;
    }

    /**
     * This method is used to set the value of serviceKeywordID.
     * 
     * @param serviceKeywordID
     */
    public void setServiceKeywordID(String serviceKeywordID) {
        _serviceKeywordID = serviceKeywordID;
    }

    public String getInterfaceDesc() {
        return _interfaceDesc;
    }

    public void setInterfaceDesc(String interfaceDesc) {
        _interfaceDesc = interfaceDesc;
    }

    public String getStatusDesc() {
        return _statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    public String getModuleDesc() {
        return _moduleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        _moduleDesc = moduleDesc;
    }

    /**
     * Sub Keyword is allowed or not
     * 
     * @return
     */
    public boolean isSubKeywordApplicable() {
        return _subKeywordApplicable;
    }

    /**
     * Set the Sub Keyword Allowed Parameter
     * 
     * @param subKeywordApplicable
     */
    public void setSubKeywordApplicable(boolean subKeywordApplicable) {
        _subKeywordApplicable = subKeywordApplicable;
    }

    public String getSubKeyword() {
        return _subKeyword;
    }

    public void setSubKeyword(String subKeyword) {
        _subKeyword = subKeyword;
    }

    /**
     * @return Returns the language1Message.
     */
    public String getLanguage1Message() {
        return _language1Message;
    }

    /**
     * @param language1Message
     *            The language1Message to set.
     */
    public void setLanguage1Message(String language1Message) {
        _language1Message = language1Message;
    }

    /**
     * @return Returns the language2Message.
     */
    public String getLanguage2Message() {
        return _language2Message;
    }

    /**
     * @param language2Message
     *            The language2Message to set.
     */
    public void setLanguage2Message(String language2Message) {
        _language2Message = language2Message;
    }

    /**
     * @return Returns the lastModified.
     */
    public long getLastModified() {
        return _lastModified;
    }

    /**
     * @param lastModified
     *            The lastModified to set.
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    /**
     * @return Returns the moduleCode.
     */
    public String getModuleCode() {
        return _moduleCode;
    }

    /**
     * @param moduleCode
     *            The moduleCode to set.
     */
    public void setModuleCode(String moduleCode) {
        _moduleCode = moduleCode;
    }

    /**
     * @return Returns the sender_network.
     */
    public String getSender_network() {
        return _sender_network;
    }

    /**
     * @param sender_network
     *            The sender_network to set.
     */
    public void setSender_network(String sender_network) {
        _sender_network = sender_network;
    }
}
