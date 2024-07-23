package com.btsl.user.businesslogic;

/**
 * @(#)BatchOPTUserVO.java
 *                         Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         Travelling object for channel user
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Shishupal Singh 22/03/2007 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */

public class BatchOPTUserVO extends UserVO {

   
	private static final long serialVersionUID = 1L;
	private String _divdept_id;
    private String _divdept_name;

    private String[] _geographyArrList;
    private String[] _rolesArrList;
    private String[] _productArrList;
    private String[] _domainArrList;
    private String[] _servicesArrList;
    private String _recordNumber;
    private String[] _vouchertypeArrList;
    private String[] segmentArrList;

    public String getDivdeptID() {
        return _divdept_id;
    }

    public void setDivdeptID(String divdept_id) {
        _divdept_id = divdept_id;
    }

    public String getDivdeptName() {
        return _divdept_name;
    }

    public void setDivdeptName(String divdept_name) {
        _divdept_name = divdept_name;
    }

    public String[] getGeographyArrList() {
        return _geographyArrList;
    }

    public void setGeographyArrList(String[] geographyArrList) {
        _geographyArrList = geographyArrList;
    }

    public String[] getRolesArrList() {
        return _rolesArrList;
    }

    public void setRolesArrList(String[] rolesArrList) {
        _rolesArrList = rolesArrList;
    }

    public String[] getProductArrList() {
        return _productArrList;
    }

    public void setProductArrList(String[] productArrList) {
        _productArrList = productArrList;
    }

    public String[] getDomainArrList() {
        return _domainArrList;
    }

    public void setDomainArrList(String[] domainArrList) {
        _domainArrList = domainArrList;
    }

    public String[] getServicesArrList() {
        return _servicesArrList;
    }

    public void setServicesArrList(String[] servicesArrList) {
        _servicesArrList = servicesArrList;
    }

    public String getRecordNumber() {
        return _recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        _recordNumber = recordNumber;
    }
    
    public String[] getVouchertypeArrList() {
        return _vouchertypeArrList;
    }

    public void setVouchertypeArrList(String[] vouchertypeArrList) {
        _vouchertypeArrList = vouchertypeArrList;
    }

	public String[] getSegmentArrList() {
		return segmentArrList;
	}

	public void setSegmentArrList(String[] segmentArrList) {
		this.segmentArrList = segmentArrList;
	}

}
