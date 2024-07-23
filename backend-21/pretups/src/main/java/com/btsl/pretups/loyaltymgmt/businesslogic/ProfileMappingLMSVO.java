package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @(#)ProfileMappingVO.java
 *                           Copyright(c) 2009, Bharti Telesoft Ltd.
 *                           All Rights Reserved
 * 
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Amit Singh 02/03/2009 Initial Creation
 * 
 * 
 * 
 */
public class ProfileMappingLMSVO implements Serializable {
    private String _categoryCode;
    private String _profileType;
    private String _setID;
    private String _defaultProfile;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _networkCode;
    private String _setName;
    private String[] _associateProfiles;
    private ArrayList _profileList;
    private String _allowAction;
    private boolean _disableAllow;

    /**
     * toString() method writes the parameters value to the console or log
     * 
     * @return String
     */
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("_categoryCode=" + _categoryCode + ",");
        sb.append("_profileType=" + _profileType + ",");
        sb.append("_setID=" + _setID + ",");
        sb.append("_defaultProfile=" + _defaultProfile + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy + ",");
        sb.append("_networkCode=" + _networkCode + ",");
        sb.append("_setName=" + _setName + ",");
        sb.append("_associateProfiles=" + _associateProfiles);
        sb.append("_profileList=" + _profileList);
        sb.append("_allowAction=" + _allowAction);
        sb.append("_disableAllow=" + _disableAllow);
        return sb.toString();
    }

    /**
     * @return
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param code
     */
    public void setCategoryCode(String code) {
        _categoryCode = code;
    }

    /**
     * @return
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param by
     */
    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    /**
     * @return
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param on
     */
    public void setCreatedOn(Date on) {
        _createdOn = on;
    }

    /**
     * @return
     */
    public String getDefaultProfile() {
        return _defaultProfile;
    }

    /**
     * @param profile
     */
    public void setDefaultProfile(String profile) {
        _defaultProfile = profile;
    }

    /**
     * @return
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param by
     */
    public void setModifiedBy(String by) {
        _modifiedBy = by;
    }

    /**
     * @return
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param on
     */
    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    /**
     * @return
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param code
     */
    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    /**
     * @return
     */
    public ArrayList getProfileList() {
        return _profileList;
    }

    /**
     * @param list
     */
    public void setProfileList(ArrayList list) {
        _profileList = list;
    }

    /**
     * @return
     */
    public String getProfileType() {
        return _profileType;
    }

    /**
     * @param type
     */
    public void setProfileType(String type) {
        _profileType = type;
    }

    /**
     * @return
     */
    public String getSetID() {
        return _setID;
    }

    /**
     * @param _setid
     */
    public void setSetID(String _setid) {
        _setID = _setid;
    }

    /**
     * @return
     */
    public String getSetName() {
        return _setName;
    }

    /**
     * @param name
     */
    /**
     * @param name
     */
    public void setSetName(String name) {
        _setName = name;
    }

    public String[] getAssociateProfiles() {
        return _associateProfiles;
    }

    public void setAssociateProfiles(String[] profiles) {
        _associateProfiles = profiles;
    }

    /**
     * @return Returns the allowAction.
     */
    public String getAllowAction() {
        return _allowAction;
    }

    /**
     * @param allowAction
     *            The allowAction to set.
     */
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
