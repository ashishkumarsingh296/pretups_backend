package com.selftopup.pretups.cardgroup.businesslogic;

import java.io.Serializable;
import java.util.Date;

/**
 * @(#)CardGroupSetVO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Mohit Goel 26/08/2005 Initial Creation
 * 
 *                         This class is used for Card Group Set Data
 * 
 */

public class CardGroupSetVO implements Serializable {
    private String _cardGroupSetID;
    private String _cardGroupSetName;
    private String _networkCode;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _lastVersion;
    private String _moduleCode;
    private String _status;
    private String _language1Message;
    private String _language2Message;
    private String _subServiceType;
    private String _subServiceTypeDescription;
    private long _lastModifiedOn;
    private String _serviceType;
    private String _serviceTypeDesc;
    private String _setType;
    private String _setTypeName;

    // added For default cardgroup
    private String _defaultCardGroup;
    private String _previousDefaultCardGroup;

    public String toString() {
        StringBuffer sb = new StringBuffer("CardGroupSetVO Data ");
        sb.append("_cardGroupSetID=" + _cardGroupSetID + ",");
        sb.append("_cardGroupSetName=" + _cardGroupSetName + ",");
        sb.append("_networkCode=" + _networkCode + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy + ",");
        sb.append("_lastVersion=" + _lastVersion + ",");
        sb.append("_moduleCode=" + _moduleCode + ",");
        sb.append("_status=" + _status + ",");
        sb.append("_lastModifiedOn=" + _lastModifiedOn + ",");
        sb.append("_language1Message=" + _language1Message + ",");
        sb.append("_language2Message=" + _language2Message + ",");
        sb.append("_subServiceType=" + _subServiceType + ",");
        sb.append("_serviceType=" + _serviceType + ",");
        sb.append("_setType=" + _setType + ",");
        sb.append("_defaultCardGroup=" + _defaultCardGroup + ",");
        sb.append("_previousDefaultCardGroup=" + _previousDefaultCardGroup + ",");

        return sb.toString();
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
     * @return Returns the cardGroupSetID.
     */
    public String getCardGroupSetID() {
        return _cardGroupSetID;
    }

    /**
     * @param cardGroupSetID
     *            The cardGroupSetID to set.
     */
    public void setCardGroupSetID(String cardGroupSetID) {
        _cardGroupSetID = cardGroupSetID;
    }

    /**
     * @return Returns the cardGroupSetName.
     */
    public String getCardGroupSetName() {
        return _cardGroupSetName;
    }

    /**
     * @param cardGroupSetName
     *            The cardGroupSetName to set.
     */
    public void setCardGroupSetName(String cardGroupSetName) {
        _cardGroupSetName = cardGroupSetName;
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
     * @return Returns the lastVersion.
     */
    public String getLastVersion() {
        return _lastVersion;
    }

    /**
     * @param lastVersion
     *            The lastVersion to set.
     */
    public void setLastVersion(String lastVersion) {
        _lastVersion = lastVersion;
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
     * @return Returns the lastModifiedOn.
     */
    public long getLastModifiedOn() {
        return _lastModifiedOn;
    }

    /**
     * @param lastModifiedOn
     *            The lastModifiedOn to set.
     */
    public void setLastModifiedOn(long lastModifiedOn) {
        _lastModifiedOn = lastModifiedOn;
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
        if (language1Message != null)
            _language1Message = language1Message.trim();
        else
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
        if (language2Message != null)
            _language2Message = language2Message.trim();
        else
            _language2Message = language2Message;
    }

    /**
     * @return Returns the subServiceType.
     */
    public String getSubServiceType() {
        return _subServiceType;
    }

    /**
     * @param subServiceType
     *            The subServiceType to set.
     */
    public void setSubServiceType(String subServiceType) {
        _subServiceType = subServiceType;
    }

    /**
     * @return Returns the subServiceTypeDescription.
     */
    public String getSubServiceTypeDescription() {
        return _subServiceTypeDescription;
    }

    /**
     * @param subServiceTypeDescription
     *            The subServiceTypeDescription to set.
     */
    public void setSubServiceTypeDescription(String subServiceTypeDescription) {
        _subServiceTypeDescription = subServiceTypeDescription;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the serviveTypeDesc.
     */
    public String getServiceTypeDesc() {
        return _serviceTypeDesc;
    }

    /**
     * @param serviveTypeDesc
     *            The serviveTypeDesc to set.
     */
    public void setServiceTypeDesc(String serviveTypeDesc) {
        _serviceTypeDesc = serviveTypeDesc;
    }

    /**
     * @return Returns the setType.
     */
    public String getSetType() {
        return _setType;
    }

    /**
     * @param setType
     *            The setType to set.
     */
    public void setSetType(String setType) {
        _setType = setType;
    }

    /**
     * @return Returns the setTypeName.
     */
    public String getSetTypeName() {
        return _setTypeName;
    }

    /**
     * @param setTypeName
     *            The setTypeName to set.
     */
    public void setSetTypeName(String setTypeName) {
        _setTypeName = setTypeName;
    }

    /**
     * @return Returns the defaultCardGroup.
     */
    public String getDefaultCardGroup() {
        return _defaultCardGroup;
    }

    /**
     * @param defaultCardGroup
     *            The defaultCardGroup to set.
     */
    public void setDefaultCardGroup(String defaultCardGroup) {
        _defaultCardGroup = defaultCardGroup;
    }

    /**
     * @return Returns the previousDefaultCardGroup.
     */
    public String getPreviousDefaultCardGroup() {
        return _previousDefaultCardGroup;
    }

    /**
     * @param previousDefaultCardGroup
     *            The previousDefaultCardGroup to set.
     */
    public void setPreviousDefaultCardGroup(String previousDefaultCardGroup) {
        _previousDefaultCardGroup = previousDefaultCardGroup;
    }
}
