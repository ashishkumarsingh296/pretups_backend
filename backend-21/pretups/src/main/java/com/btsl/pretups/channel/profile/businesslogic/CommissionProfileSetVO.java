/**
 * @(#)CommissionProfileSetVO.java
 *                                 Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 * 
 *                                 <description>
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 avinash.kamthan Aug 3, 2005 Initital Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 * 
 */

package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.user.businesslogic.UserDAO;

/**
 * @author avinash.kamthan
 * 
 */
public class CommissionProfileSetVO implements Serializable {
    private String _commProfileSetId;
    private String _commProfileSetName;
    private String _categoryCode;
    private String _categoryName;
    private String _networkName;
    private String _networkCode;
    private String _commLastVersion;
    private String _modifiedBy;
    private String _createdBy;
    private Date _modifiedOn;
    private long _lastModifiedOn;
    private Date _createdOn;
    private String _shortCode;
    private String _status;
    private String _language1Message = null;
    private String _language2Message = null;
    private String _defaultProfile; // To load the default commission profile.
    // (change done by ashishT)

    private String _setVersion; // added for batch modify commission profile
    // (road map 5.8)
    private Date _applicableFrom;
    private String _batch_ID;

    private String _grphDomainCode;
    private String _grphDomainName;
    private String _gradeCode;
    private String _gradeName;
    private String _gatewayCode = null;
    private String _additionalCommissionTimeSlab;
    protected String label = null; // name

    protected String value = null; // code

    protected String codeName = null;

    protected String otherInfo = null;

    protected String otherInfo2 = null;

    // Commission profile cache
    private String _commProfileVersion = null;
    
    private String dualCommissionType;
	 
	 public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}
	
	


    public String getBatch_ID() {
        return _batch_ID;
    }

    public void setBatch_ID(String batch_ID) {
        _batch_ID = batch_ID;
    }

    public String getSetVersion() {
        return _setVersion;
    }

    public void setSetVersion(String setVersion) {
        _setVersion = setVersion;
    }

    public Date getApplicableFrom() {
        return _applicableFrom;
    }

    public void setApplicableFrom(Date applicableFrom) {
        _applicableFrom = applicableFrom;
    }

    public String toString() {
        if (UserDAO.flag == true) {
            final StringBuffer sb = new StringBuffer("ListValueBean[");
            sb.append(_commProfileSetId);
            sb.append(", ");
            sb.append(_commProfileSetName);
            sb.append("]");
            return (sb.toString());
        }

        else {
            final StringBuffer sb = new StringBuffer("CommissionProfileSetVO Data ");
            sb.append("_commProfileSetId=" + _commProfileSetId + ",");
            sb.append("_commProfileSetName=" + _commProfileSetName + ",");
            sb.append("_categoryCode=" + _categoryCode + ",");
            sb.append("_categoryName=" + _categoryName + ",");
            sb.append("_networkName=" + _networkName + ",");
            sb.append("_networkCode=" + _networkCode + ",");
            sb.append("_commLastVersion=" + _commLastVersion + ",");
            sb.append("_createdBy=" + _createdBy + ",");
            sb.append("_createdOn=" + _createdOn + ",");
            sb.append("_modifiedBy=" + _modifiedBy + ",");
            sb.append("_modifiedOn=" + _modifiedOn + ",");
            sb.append("_lastModifiedOn=" + _lastModifiedOn + ",");
            sb.append("_shortCode=" + _shortCode + ",");
            sb.append("_status=" + _status + ",");
            sb.append("_gradeCode=" + _gradeCode + ",");
            sb.append("_grphDomainCode=" + _grphDomainCode + ",");
            sb.append("_language1Message=" + _language1Message + ",");
            sb.append("_language2Message=" + _language2Message + ",");
            return sb.toString();

        }

    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getCommLastVersion() {
        return _commLastVersion;
    }

    public void setCommLastVersion(String commLastVersion) {
        _commLastVersion = commLastVersion;
    }

    public String getCommProfileSetId() {
        return _commProfileSetId;
    }

    public void setCommProfileSetId(String commProfileSetId) {

        _commProfileSetId = commProfileSetId;
    }

    public String getCommProfileSetName() {
        return _commProfileSetName;
    }

    public void setCommProfileSetName(String commProfileSetName) {
        _commProfileSetName = commProfileSetName;
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

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getShortCode() {
        return _shortCode;
    }

    public void setShortCode(String shortCode) {
        _shortCode = shortCode;
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
        if (language1Message != null) {
            _language1Message = language1Message.trim();
        }
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
        if (language2Message != null) {
            _language2Message = language2Message.trim();
        }
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

    public String getDefaultProfile() {
        return _defaultProfile;
    }

    public void setDefaultProfile(String defaultProfile) {
        _defaultProfile = defaultProfile;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    public void setGrphDomainCode(String gcode) {
        _grphDomainCode = gcode;
    }

    public String getGradeCode() {
        return _gradeCode;
    }

    public void setGradeCode(String code) {
        _gradeCode = code;
    }

    public void setGatewayCode(String gatewayCode) {
        if (gatewayCode != null) {
            _gatewayCode = gatewayCode.trim();
        }
    }

    public String getGatewayCode() {
        return _gatewayCode;
    }

    public String getAdditionalCommissionTimeSlab() {
        return _additionalCommissionTimeSlab;
    }

    public void setAdditionalCommissionTimeSlab(String time) {
        _additionalCommissionTimeSlab = time;
    }

    public String getCombinedKey() {
        return _commProfileSetId + ":" + _gradeCode;
    }

    public CommissionProfileSetVO(
                    String label, String value) {
        this.label = label;
        this.value = value;
        this.codeName = value + "|" + label;
    }

    public CommissionProfileSetVO(
                    String codeName, String otherInfo, String otherInfo2) {
        this.codeName = codeName;
        this.otherInfo = otherInfo;
        this.otherInfo2 = otherInfo2;
    }

    public CommissionProfileSetVO() {
    }

    public void setGradeName(String gradeCode) {
        _gradeName = gradeCode;
    }

    public String getGradeName() {
        return _gradeName;
    }

    public String getGrphDomainName() {
        return _grphDomainName;
    }

    public void setGrphDomainName(String gName) {
        _grphDomainName = gName;
    }

    public String getCommProfileVersion() {
        return _commProfileVersion;
    }

    public void setCommProfileVersion(String profileVersion) {
        _commProfileVersion = profileVersion;
    }
    
    public static CommissionProfileSetVO getInstance(){
    	return new CommissionProfileSetVO();
    }

}
