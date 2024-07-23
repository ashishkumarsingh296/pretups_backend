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
public class UserGeoDomainVO {
    private String _grphDomainCode;
    private String _networkCode;
    private String _grphDomainName;
    private String _parentGrphDomainCode;
    private String _grphDomainShortName;
    private String _status;
    private String _grphDomainType;

    /**
     * @return the grphDomainCode
     */
    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    /**
     * @param grphDomainCode
     *            the grphDomainCode to set
     */
    public void setGrphDomainCode(String grphDomainCode) {
        _grphDomainCode = grphDomainCode;
    }

    /**
     * @return the networkCode
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            the networkCode to set
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return the grphDomainName
     */
    public String getGrphDomainName() {
        return _grphDomainName;
    }

    /**
     * @param grphDomainName
     *            the grphDomainName to set
     */
    public void setGrphDomainName(String grphDomainName) {
        _grphDomainName = grphDomainName;
    }

    /**
     * @return the parentGrphDomainCode
     */
    public String getParentGrphDomainCode() {
        return _parentGrphDomainCode;
    }

    /**
     * @param parentGrphDomainCode
     *            the parentGrphDomainCode to set
     */
    public void setParentGrphDomainCode(String parentGrphDomainCode) {
        _parentGrphDomainCode = parentGrphDomainCode;
    }

    /**
     * @return the grphDomainShortName
     */
    public String getGrphDomainShortName() {
        return _grphDomainShortName;
    }

    /**
     * @param grphDomainShortName
     *            the grphDomainShortName to set
     */
    public void setGrphDomainShortName(String grphDomainShortName) {
        _grphDomainShortName = grphDomainShortName;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        _status = status;
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
}
