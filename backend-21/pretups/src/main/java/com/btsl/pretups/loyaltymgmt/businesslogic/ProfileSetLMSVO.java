package com.btsl.pretups.loyaltymgmt.businesslogic;

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

public class ProfileSetLMSVO implements Serializable {
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
    private ProfileSetVersionLMSVO _profileLastVersion;

    // added by rahul
    private String _shortCode;
    private String _networkCode;
    private String _userID;
    private String _allowAction;
    private boolean _disableAllow;
    private String _promotionType;
    private String _refBasedAllow;
    private String _referenceDate;

    private String _lang1;
    private String _lang2;

    private String _optInOut;

    public String getLang1() {
        return _lang1;
    }

    public void setLang1(String msg) {
        _lang1 = msg;
        // System.out.println("**********"+_lang1);
    }

    public String getLang2() {
        return _lang2;
    }

    public void setLang2(String msg) {
        _lang2 = msg;
    }

    public String getReferenceDate() {
        return _referenceDate;
    }

    public void setReferenceDate(String date) {
        _referenceDate = date;
    }

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
        sb.append("_promotionType=" + _promotionType);
        sb.append("_refBasedAllow=" + _refBasedAllow);
        sb.append("_message1=" + _lang1);
        sb.append("_message2=" + _lang2);
        sb.append("_lang1welcomemsg=" + _lang1welcomemsg);
        sb.append("_lang2welcomemsg=" + _lang2welcomemsg);
        sb.append("_lang1seccessmsg=" + _lang1seccessmsg);
        sb.append("_lang2seccessmsg=" + _lang2seccessmsg);
        sb.append("_lang1failuremsg=" + _lang1failuremsg);
        sb.append("_lang2failuremsg=" + _lang2failuremsg);
        sb.append("_optInOut=" + _optInOut);
        sb.append("_defaultMessage=" + _defaultMessage);
        sb.append("_endRange=" + _endRange);
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

    public ProfileSetVersionLMSVO getProfileLastVersion() {
        return _profileLastVersion;
    }

    public void setProfileLastVersion(ProfileSetVersionLMSVO profileLastVersion) {
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

    public String getPromotionType() {
        return _promotionType;
    }

    public void setPromotionType(String type) {
        _promotionType = type;
    }

    public String getRefBasedAllow() {
        return _refBasedAllow;
    }

    public void setRefBasedAllow(String basedAllow) {
        _refBasedAllow = basedAllow;
    }

    // brajesh
    private String _msgConfEnableFlag = null;

    public String getMsgConfEnableFlag() {
        return _msgConfEnableFlag;
    }

    public void setMsgConfEnableFlag(String str) {
        _msgConfEnableFlag = str;
    }

    // brajesh
    private String _messageCode = null;

    public String getMessageCode() {
        return _messageCode;
    }

    public void setMessageCode(String str) {
        _messageCode = str;
    }

    // brajesh
    private String _applicableToDate = null;

    public String getApplicableToDate() {
        return _applicableToDate;
    }

    public void setApplicableToDate(String str) {
        _applicableToDate = str;
    }

    // brajesh
    private String _applicableFromDate = null;

    public String getApplicableFromDate() {
        return _applicableFromDate;
    }

    public void setApplicableFromDate(String str) {
        _applicableFromDate = str;
    }

    // brajesh
    private String _message1 = null;

    public String getMessage1() {
        return _message1;
    }

    public void setMessage1(String str) {
        _message1 = str;
    }

    // brajesh
    private String _message2 = null;

    public String getMessage2() {
        return _message2;
    }

    public void setMessage2(String str) {
        _message2 = str;
    }

    // brajesh
    private String _endRange = null;

    public String getEndRange() {
        return _endRange;
    }

    public void setEndRange(String str) {
        _endRange = str;
    }

    // brajesh
    private String _lang1welcomemsg = null;
    private String _lang2welcomemsg = null;
    private String _lang1seccessmsg = null;
    private String _lang2seccessmsg = null;
    private String _lang1failuremsg = null;
    private String _lang2failuremsg = null;

    public String getLang1seccessmsg() {
        return _lang1seccessmsg;
    }

    public void setLang1seccessmsg(String _lang1seccessmsg) {
        this._lang1seccessmsg = _lang1seccessmsg;
    }

    public String getLang2seccessmsg() {
        return _lang2seccessmsg;
    }

    public void setLang2seccessmsg(String _lang2seccessmsg) {
        this._lang2seccessmsg = _lang2seccessmsg;
    }

    public String getLang1failuremsg() {
        return _lang1failuremsg;
    }

    public void setLang1failuremsg(String _lang1failuremsg) {
        this._lang1failuremsg = _lang1failuremsg;
    }

    public String getLang2failuremsg() {
        return _lang2failuremsg;
    }

    public void setLang2failuremsg(String _lang2failuremsg) {
        this._lang2failuremsg = _lang2failuremsg;
    }

    public String getLang1welcomemsg() {
        return _lang1welcomemsg;
    }

    public void setLang1welcomemsg(String _lang1welcomemsg) {
        this._lang1welcomemsg = _lang1welcomemsg;
    }

    public String getLang2welcomemsg() {
        return _lang2welcomemsg;
    }

    public void setLang2welcomemsg(String _lang2welcomemsg) {
        this._lang2welcomemsg = _lang2welcomemsg;
    }

    // brajesh
    private String _defaultMessage = null;

    public String getdefaultMessage() {
        return _defaultMessage;
    }

    public void setdefaultMessage(String str) {
        _defaultMessage = str;
    }

    public String getOptInOut() {
        return _optInOut;
    }

    public void setOptInOut(String optInOut) {
        _optInOut = optInOut;
    }

}
