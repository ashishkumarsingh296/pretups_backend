package com.btsl.pretups.channel.profile.businesslogic;

/**
 * @(#)ProfileSetVersionVO.java
 *                              Copyright(c) 2008, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              This class refers to the Profile set version in
 *                              a profile set.
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              ankit.singhal 09/02/2009 Initital Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.util.Constants;

public class ProfileSetVersionVO implements Serializable {
    private String _setId;
    private String _version;
    private Date _applicableFrom;
    private long _oneTimeBonus = 0;
    private long _bonusDuration = 0;
    private String _productCode;
    private String _status;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private ArrayList _profileSetDetails;
    private ArrayList _lppGroupSetDetails;

    /**
     * toString() method writes the parameters value to the console or log
     * 
     * @return String
     */
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("_setId=" + _setId + ",");
        sb.append("_version=" + _version + ",");
        sb.append("_applicableFrom=" + _applicableFrom + ",");
        sb.append("_oneTimeBonus=" + _oneTimeBonus + ",");
        sb.append("_bonusDuration=" + _bonusDuration + ",");
        sb.append("_productCode=" + _productCode + ",");
        sb.append("_status=" + _status + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy);
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

        sbf.append(startSeperator + "Set ID" + middleSeperator + this.getSetId());
        sbf.append(startSeperator + "Version" + middleSeperator + this.getVersion());
        sbf.append(startSeperator + "Applicable from" + middleSeperator + this.getApplicableFrom());
        sbf.append(startSeperator + "One time bonus" + middleSeperator + this.getOneTimeBonus());
        sbf.append(startSeperator + "Bonus duration" + middleSeperator + this.getBonusDuration());
        sbf.append(startSeperator + "Product code" + middleSeperator + this.getProductCode());
        sbf.append(startSeperator + "Status" + middleSeperator + this.getStatus());
        return sbf.toString();
    }

    public Date getApplicableFrom() {
        return _applicableFrom;
    }

    public void setApplicableFrom(Date applicableFrom) {
        _applicableFrom = applicableFrom;
    }

    public long getBonusDuration() {
        return _bonusDuration;
    }

    public void setBonusDuration(long bonusDuration) {
        _bonusDuration = bonusDuration;
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

    public long getOneTimeBonus() {
        return _oneTimeBonus;
    }

    public void setOneTimeBonus(long oneTimeBonus) {
        _oneTimeBonus = oneTimeBonus;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getSetId() {
        return _setId;
    }

    public void setSetId(String setId) {
        _setId = setId;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public ArrayList getProfileSetDetails() {
        return _profileSetDetails;
    }

    public void setProfileSetDetails(ArrayList profileSetDetails) {
        _profileSetDetails = profileSetDetails;
    }

    public String getActSetCombinedID() {
        return _setId + ":" + _version + ":" + _applicableFrom;
    }

    public ArrayList getLppGroupSetDetails() {
        return _lppGroupSetDetails;
    }

    public void setLppGroupSetDetails(ArrayList lppGroupSetDetails) {
        _lppGroupSetDetails = lppGroupSetDetails;
    }
}
