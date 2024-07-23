package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.util.Constants;

/**
 * @(#)ProfileSetVO.java
 *                       Copyright(c) 2008, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 *                       This class refers to the Profile set in the system.
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       ankit.singhal 09/02/2009 Initital Creation
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 */

public class ProfileSetVO implements Serializable {
    private String _profileType;
    private String _setId;
    private String _setName;
    private String _lastVersion;
    private String _status;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private ArrayList _profileSetVersion;
    private ProfileSetVersionVO _profileLastVersion;

    // added by rahul
    private String _shortCode;
    private String _networkCode;
    private String _userID;
    private String _allowAction;
    private boolean _disableAllow;

    /**
     * toString() method writes the parameters value to the console or log
     * 
     * @return String
     */
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("_profileType=" + _profileType + ",");
        sb.append("_setId=" + _setId + ",");
        sb.append("_setName=" + _setName + ",");
        sb.append("_lastVersion=" + _lastVersion + ",");
        sb.append("_status=" + _status + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy);
        sb.append("_profileSetVersion=" + _profileSetVersion);
        sb.append("_profileLastVersion=" + _profileLastVersion);
        sb.append("_shortCode=" + _shortCode);
        sb.append("_networkCode=" + _networkCode);
        sb.append("_userID=" + _userID);
        sb.append("_allowAction=" + _allowAction);
        sb.append("_disableAllow=" + _disableAllow);
        return sb.toString();
    }

    /**
     * This method is used to log information.
     * 
     * @return String
     */
    public String logInfo() {
        final StringBuffer sbf = new StringBuffer(10);

        final String startSeperator = Constants.getProperty("startSeperatpr");
        final String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator + "Profile type" + middleSeperator + this.getProfileType());
        sbf.append(startSeperator + "Set ID" + middleSeperator + this.getSetId());
        sbf.append(startSeperator + "Set name" + middleSeperator + this.getSetName());
        sbf.append(startSeperator + "Last version" + middleSeperator + this.getLastVersion());
        sbf.append(startSeperator + "Status" + middleSeperator + this.getStatus());
        return sbf.toString();
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

    public String getLastVersion() {
        return _lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        _lastVersion = lastVersion;
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

    public String getSetId() {
        return _setId;
    }

    public void setSetId(String setId) {
        _setId = setId;
    }

    public String getSetName() {
        return _setName;
    }

    public void setSetName(String setName) {
        _setName = setName;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getProfileType() {
        return _profileType;
    }

    public void setProfileType(String profileType) {
        _profileType = profileType;
    }

    public String getShortCode() {
        return _shortCode;
    }

    public void setShortCode(String code) {
        _shortCode = code;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    public ProfileSetVersionVO getProfileLastVersion() {
        return _profileLastVersion;
    }

    public void setProfileLastVersion(ProfileSetVersionVO profileLastVersion) {
        _profileLastVersion = profileLastVersion;
    }

    public ArrayList getProfileSetVersion() {
        return _profileSetVersion;
    }

    public void setProfileSetVersion(ArrayList profileSetVersion) {
        _profileSetVersion = profileSetVersion;
    }

    /**
     * This id for set the value in other
     * association in user_oth_profile table
     * 
     * @return the _userID
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param _userid
     *            the _userID to set
     */
    public void setUserID(String _userid) {
        _userID = _userid;
    }

    public String getAllowAction() {
        return _allowAction;
    }

    public void setAllowAction(String allowAction) {
        _allowAction = allowAction;
    }

    public boolean getDisableAllow() {
        return _disableAllow;
    }

    public void setDisableAllow(boolean disableAllow) {
        _disableAllow = disableAllow;
    }
}
