/*
 * #GradeVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 3, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.domain.businesslogic;

import java.io.Serializable;
import java.util.Date;


public class GradeVO implements Serializable {
    private String _gradeCode;
    private String _gradeName;
    private String _categoryCode;
    private String _categoryName;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private int _radioIndex;
    private long _lastModifiedTime;
    private String _status;
    private String _defaultGrade;
    //2FA
    private String _twoFAallowed;
    
   
    // added by akanksha for batch grade management
    // By Lalit for batch grade management
    private String _parentCategoryCode;
    private String _parentCategoryName;
    private String _categoryUserId;
    private String _categoryUserName;
    private String _statusDesciption;
    private String _recordNumber;
    private String _ownerCategoryCode;
    private String _ownerCategoryName;
    private String _assocGradeCode;
    private String _serviceAllowed;

    public String toString() {
        StringBuffer strBuff = new StringBuffer("\n Grade Code=" + _gradeCode);
        strBuff.append("\n Grade Name=" + _gradeName);
        strBuff.append("\n Category Code=" + _categoryCode);
        strBuff.append("\n Created On=" + _createdOn);
        strBuff.append("\n Created By=" + _createdBy);
        strBuff.append("\n Modified On=" + _modifiedOn);
        strBuff.append("\n Modified By=" + _modifiedBy);
        strBuff.append("\n Last Modified=" + _lastModifiedTime);
        strBuff.append("\n Radio Index=" + _radioIndex);
        strBuff.append("\n Status=" + _status);
        strBuff.append("\n TwoFaAllowed=" + _twoFAallowed);
        // added by akanksha for batch grade management
        strBuff.append("\n ParentCategoryCode=" + _parentCategoryCode);
        strBuff.append("\n CategoryUserId=" + _categoryUserId);
        strBuff.append("\n CategoryUserName=" + _categoryUserName);
        strBuff.append("\n StatusDesciption=" + _statusDesciption);
        strBuff.append("\n OwnerCategoryCode=" + _ownerCategoryCode);
        strBuff.append("\n OwnerCategoryName=" + _ownerCategoryName);
        strBuff.append("\n AssocGradeCode=" + _assocGradeCode);
        return strBuff.toString();
    }

    public String getCombinedKey() {
        return _categoryCode + ":" + _gradeCode;
    }
    
    
    //2FA start
    public String getTwoFAallowed() {
        return _twoFAallowed;
    }

    public void setTwoFAallowed(String twoFAallowed) {
        this._twoFAallowed = twoFAallowed;
    }
    

    /**
     * To get the value of status field
     * 
     * @return status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * To set the value of status field
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * To get the value of radioIndex field
     * 
     * @return radioIndex.
     */
    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * To set the value of radioIndex field
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    /**
     * To get the value of categoryCode field
     * 
     * @return categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * To set the value of categoryCode field
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * To get the value of createdBy field
     * 
     * @return createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * To set the value of createdBy field
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * To get the value of createdOn field
     * 
     * @return createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * To set the value of createdOn field
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * To get the value of gradeCode field
     * 
     * @return gradeCode.
     */
    public String getGradeCode() {
        return _gradeCode;
    }

    /**
     * To set the value of gradeCode field
     */
    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }

    /**
     * To get the value of gradeName field
     * 
     * @return gradeName.
     */
    public String getGradeName() {
        return _gradeName;
    }

    /**
     * To set the value of gradeName field
     */
    public void setGradeName(String gradeName) {
        _gradeName = gradeName;
    }

    /**
     * To get the value of modifiedBy field
     * 
     * @return modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * To set the value of modifiedBy field
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * To get the value of modifiedOn field
     * 
     * @return modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * To get the value of lastModified field
     * 
     * @return lastModified.
     */
    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    /**
     * To set the value of lastModified field
     */
    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    // added by nilesh
    /**
     * @return _defaultgrade.
     */
    public String getDefaultGrade() {
        return _defaultGrade;
    }

    /**
     * To set the value of defaultgrade field
     */
    public void setDefaultGrade(String defaultGrade) {
        _defaultGrade = defaultGrade;
    }

    // added by akanksha for batch grade mangement

    public void setParentCategoryCode(String categoryCode) {
        _parentCategoryCode = categoryCode;
    }

    public String getParentCategoryCode() {
        return _parentCategoryCode;
    }

    public String getParentCategoryName() {
        return _parentCategoryName;
    }

    public void setParentCategoryName(String categoryName) {
        _parentCategoryName = categoryName;
    }

    public String getCategoryUserId() {
        return _categoryUserId;
    }

    public void setCategoryUserId(String userId) {
        _categoryUserId = userId;
    }

    public String getCategoryUserName() {
        return _categoryUserName;
    }

    public void setCategoryUserName(String userName) {
        _categoryUserName = userName;
    }

    public String getStatusDesciption() {
        return _statusDesciption;
    }

    public void setStatusDesciption(String desciption) {
        _statusDesciption = desciption;
    }

    public String getRecordNumber() {
        return _recordNumber;
    }

    public void setRecordNumber(String number) {
        _recordNumber = number;
    }

    public String getOwnerCategoryCode() {
        return _ownerCategoryCode;
    }

    public void setOwnerCategoryCode(String categoryCode) {
        _ownerCategoryCode = categoryCode;
    }

    public String getOwnerCategoryName() {
        return _ownerCategoryName;
    }

    public void setOwnerCategoryName(String categoryName) {
        _ownerCategoryName = categoryName;
    }

    public String getAssocGradeCode() {
        return _assocGradeCode;
    }

    public void setAssocGradeCode(String gradeCode) {
        _assocGradeCode = gradeCode;
    }

    public String getServiceAllowed() {
        return _serviceAllowed;
    }

    /**
     * @param serviceAllowed
     *            The serviceAllowed to set.
     */
    public void setServiceAllowed(String serviceAllowed) {
        _serviceAllowed = serviceAllowed;
    }

    /**
     * To get the value of categoryName field
     * 
     * @return categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * To set the value of categoryName field
     */
    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

}
