package com.btsl.pretups.cardgroup.businesslogic;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * @(#)CardGroupSetVersionVO.java
 *                                Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 * 
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Mohit Goel 26/08/2005 Initial Creation
 * 
 *                                This class is used for CardGroupSetVersion
 *                                Data
 * 
 */

public class CardGroupSetVersionVO implements Serializable {

    private String _cardGroupSetID;
    private String _version;
    private Date _applicableFrom;
    private String _createdBy;
    private Date _creadtedOn;
    private String _modifiedBy;
    private Date _modifiedOn;

    // used while updating the record
    private long _oldApplicableFrom;

    public String toString() {
        final StringBuffer sb = new StringBuffer("CardGroupSetVO Data ");
        sb.append("_cardGroupSetID=" + _cardGroupSetID + ",");
        sb.append("_version=" + _version + ",");
        sb.append("_applicableFrom=" + _applicableFrom + ",");
        sb.append("_createdOn=" + _creadtedOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy + ",");
        sb.append("_oldApplicableFrom=" + _oldApplicableFrom + ",");

        return sb.toString();
    }

    /**
     * @return Returns the applicableFrom.
     */
    public Date getApplicableFrom() {
        return _applicableFrom;
    }

    /**
     * @param applicableFrom
     *            The applicableFrom to set.
     */
    public void setApplicableFrom(Date applicableFrom) {
        _applicableFrom = applicableFrom;
    }

    public String getApplicableFromAsString() {
        if (_applicableFrom != null) {
            try {
                return BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(_applicableFrom));
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }

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
     * @return Returns the version.
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        _version = version;
    }

    /**
     * @return Returns the creadtedOn.
     */
    public Date getCreadtedOn() {
        return _creadtedOn;
    }

    /**
     * @param creadtedOn
     *            The creadtedOn to set.
     */
    public void setCreadtedOn(Date creadtedOn) {
        _creadtedOn = creadtedOn;
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
     * @return Returns the oldApplicableFrom.
     */
    public long getOldApplicableFrom() {
        return _oldApplicableFrom;
    }

    /**
     * @param oldApplicableFrom
     *            The oldApplicableFrom to set.
     */
    public void setOldApplicableFrom(long oldApplicableFrom) {
        _oldApplicableFrom = oldApplicableFrom;
    }

    public String getCardGroupSetCombinedID() {
        return _cardGroupSetID + ":" + _version;
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
    public String getCreatedOnAsString() {
        if (_applicableFrom != null) {
            try {
                return BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(_creadtedOn));
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }

    }

    public String getModifiedOnAsString() {
        if (_applicableFrom != null) {
            try {
                return BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(_modifiedOn));
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }

    }

}
