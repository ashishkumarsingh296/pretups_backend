/*
 * #SimServiceCategoriesVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 10, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

public class SimServiceCategoriesVO implements Serializable {

    private int _serviceID;
    private String _categoryCode;
    private String _majorVersion;
    private String _minorVersion;
    private String _serviceSetId;

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuilder sbf = new StringBuilder();
    	 sbf.append("Category Code=").append(_categoryCode);
    	 sbf.append(" Service Set Id=").append(_serviceSetId);
    	 sbf.append(" Service Id=").append(_serviceID);
    	 sbf.append(" Major version=").append(_majorVersion);
    	 sbf.append(" Minor Version=").append(_minorVersion);
    	
    	 
        return sbf.toString();
    }

    /**
     * To get the value of serviceSetId field
     * 
     * @return serviceSetId.
     */
    public String getServiceSetId() {
        return _serviceSetId;
    }

    /**
     * To set the value of serviceSetId field
     */
    public void setServiceSetId(String serviceSetId) {
        _serviceSetId = serviceSetId;
    }

    /**
     * To get the value of majorVersion field
     * 
     * @return majorVersion.
     */
    public String getMajorVersion() {
        return _majorVersion;
    }

    /**
     * To set the value of majorVersion field
     */
    public void setMajorVersion(String majorVersion) {
        _majorVersion = majorVersion;
    }

    /**
     * To get the value of minorVersion field
     * 
     * @return minorVersion.
     */
    public String getMinorVersion() {
        return _minorVersion;
    }

    /**
     * To set the value of minorVersion field
     */
    public void setMinorVersion(String minorVersion) {
        _minorVersion = minorVersion;
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
     * To get the value of serviceID field
     * 
     * @return serviceID.
     */
    public int getServiceID() {
        return _serviceID;
    }

    /**
     * To set the value of serviceID field
     */
    public void setServiceID(int serviceID) {
        _serviceID = serviceID;
    }
}
