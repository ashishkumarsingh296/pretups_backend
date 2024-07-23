package com.btsl.pretups.user.businesslogic;

/**
 * @(#)UserCategoryVO.java
 *                         Copyright(c) 2010, Comviva Technologies Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Mahindra Comviva OCT 10,2010 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */
public class UserCategoryVO {
    private String _categoryCode;
    private String _categoryName;
    private String _domainCode;
    private int _sequenceNo;
    private String _grphDomainType;
    private String _userIdPrefix;
    private String _lowBalAlertAllow;

    /**
     * @return the categoryCode
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            the categoryCode to set
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param categoryName
     *            the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    /**
     * @return the domainCode
     */
    public String getDomainCode() {
        return _domainCode;
    }

    /**
     * @param domainCode
     *            the domainCode to set
     */
    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    /**
     * @return the sequenceNo
     */
    public int getSequenceNo() {
        return _sequenceNo;
    }

    /**
     * @param sequenceNo
     *            the sequenceNo to set
     */
    public void setSequenceNo(int sequenceNo) {
        _sequenceNo = sequenceNo;
    }

    /**
     * @return the grphDomainType
     */
    public String getGrphDomainType() {
        return _grphDomainType;
    }

    /**
     * @param grphDomainType
     *            the grphDomainType to set
     */
    public void setGrphDomainType(String grphDomainType) {
        _grphDomainType = grphDomainType;
    }

    /**
     * @return the userIdPrefix
     */
    public String getUserIdPrefix() {
        return _userIdPrefix;
    }

    /**
     * @param userIdPrefix
     *            the userIdPrefix to set
     */
    public void setUserIdPrefix(String userIdPrefix) {
        _userIdPrefix = userIdPrefix;
    }

    /**
     * @return the lowBalAlertAllow
     */
    public String getLowBalAlertAllow() {
        return _lowBalAlertAllow;
    }

    /**
     * @param lowBalAlertAllow
     *            the lowBalAlertAllow to set
     */
    public void setLowBalAlertAllow(String lowBalAlertAllow) {
        _lowBalAlertAllow = lowBalAlertAllow;
    }
}
