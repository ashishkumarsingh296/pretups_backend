package com.btsl.voms.vomsproduct.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/*
 * @(#)VomsActiveProductVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Singh 10/07/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */

public class VomsActiveProductVO implements Serializable {
    // Instanse variables
    private String _activeProductID;
    private String _networkCode;
    private Date _applicableFrom;
    private String _applicableFromStr;
    private String _status;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;
    private String _voucherType;
    private ArrayList _productList;
    private String segment;
    private String segmentDesc;
    private String productID=null;
    private String type=null;
    
	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	/**
     * Method toString.
     * This method is used to display all of the information of
     * the object of the VomsActiveProductVO class.
     * 
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" _activeProductID=" + _activeProductID);
        sb.append(" _networkCode=" + _networkCode);
        sb.append(" _applicableFrom=" + _applicableFrom);
        sb.append(" _status=" + _status);
        sb.append(" _createdBy=" + _createdBy);
        sb.append(" _createdOn=" + _createdOn);
        sb.append(" _modifiedBy=" + _modifiedBy);
        sb.append(" _modifiedOn=" + _modifiedOn);
        sb.append("type=" + type);

        return sb.toString();
    }

    /**
     * @return Returns the productList.
     */
    public ArrayList getProductList() {
        return _productList;
    }

    /**
     * @param productList
     *            The productList to set.
     */
    public void setProductList(ArrayList productList) {
        _productList = productList;
    }

    /**
     * @return Returns the applicableFromStr.
     */
    public String getApplicableFromStr() {
        return _applicableFromStr;
    }

    /**
     * @param applicableFromStr
     *            The applicableFromStr to set.
     */
    public void setApplicableFromStr(String applicableFromStr) {
        _applicableFromStr = applicableFromStr;
    }

    /**
     * @return Returns the activeProductID.
     */
    public String getActiveProductID() {
        return _activeProductID;
    }

    /**
     * @param activeProductID
     *            The activeProductID to set.
     */
    public void setActiveProductID(String activeProductID) {
        _activeProductID = activeProductID;
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
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
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

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
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

    public void setVoucherType(String type) {
        _voucherType = type;
    }

    public String getVoucherType() {
        return _voucherType;
    }

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getSegmentDesc() {
		return segmentDesc;
	}

	public void setSegmentDesc(String segmentDesc) {
		this.segmentDesc = segmentDesc;
	}
	 public String getType() {
			return type;
	}

	public void setType(String type) {
			this.type = type;
	}

}
