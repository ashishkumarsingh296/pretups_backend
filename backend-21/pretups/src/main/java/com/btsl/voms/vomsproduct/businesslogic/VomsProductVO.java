package com.btsl.voms.vomsproduct.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/*
 * @(#)VomsProductVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Singh 22/06/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */

public class VomsProductVO implements Serializable,Comparable {
    @Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
    	return 	this.getProductName().compareTo(((VomsProductVO)o).getProductName());
	}

    // Instanse variables
    private String _productID;
    private String _productName;
    private String _shortName;
    private String _categoryID;// It is for storing the categoryID of selected
                               // mrp by jsp addNewProduct.jsp
    private String _mrpStr;
    private String _status;
    private String _statusDesc;
    private String _description;
    private int _productCode;
    private long _minReqQuantity;
    private long _maxReqQuantity;
    private double _multipleFactor;
    private long _expiryPeriod;
    private String _individualEntity;
    private String _attribute1;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;
    private String _serviceCode;
    private String _noOfArguments;
    private double _talkTime;
    private String _talkTimeStr;
    private long _validity;
    private String _validityStr;
    private Date _applicableFrom;
    private String _applicableFromStr;
    private String _networkCode;
    private String _activeProductID;
    private String _categoryName;
    private String _oldProductName;
    private String _oldProductID;

    private ArrayList _productVOList;
    private double _mrp = 0.0;
    private long _multipleOf = 0;
    private long _maximumQuantity = 0;
    private long _minimumQuantity = 0;
    private String _profile;
    private String _subCategoryID;
    private String _subCategoryName;
    private String _individualEntityStr;
    private ArrayList _productionLocationList;
    private ArrayList _userLocationList;
    private String _checkBoxVal;
    private String _type = null;
    private String _label;
    private String _voucherType;
    private String _service;
    private String _subService;
    private String _serialNo;

	private String _pinNo;
    private Date _expiryDate;
    private String _txnID;
    private String _subID;
	private String _voucherQuantity;
	private int availableGenerateVoucher;
	private String segment;
	private String segmentDesc;
	private String serviceName;
	//Added for voms_hcpt by niharika
	private String _itemCode;
    private String _secondaryPrefixCode;

	public int getAvailableGenerateVoucher() {
		return availableGenerateVoucher;
	}
	public void setAvailableGenerateVoucher(int availableGenerateVoucher) {
		this.availableGenerateVoucher = availableGenerateVoucher;
	}

	private String voucherAutoGenerate = "N";
	public String getVoucherAutoGenerate() {
		return voucherAutoGenerate;
		}
	public void setVoucherAutoGenerate(String voucherAutoGenerate) {
			this.voucherAutoGenerate = voucherAutoGenerate;
		}
	private String voucherGenerateQuantity = null;
    private String voucherThreshold = null;
    public String getVoucherGenerateQuantity() {
		return voucherGenerateQuantity;
	}
	public void setVoucherGenerateQuantity(String voucherGenerateQuantity) {
		this.voucherGenerateQuantity = voucherGenerateQuantity;
	}
	public String getVoucherThreshold() {
		return voucherThreshold;
	}
	public void setVoucherThreshold(String voucherThreshold) {
		this.voucherThreshold = voucherThreshold;
	}
 
private String _expiryDateString;
    /**
     * @return the label
     */
    public String getLabel() {
        return _label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        _label = label;
    }

    /**
     * Method toString.
     * This method is used to display all of the information of
     * the object of the VomsProductVO class.
     * 
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" _categoryID=" + _categoryID);
        sb.append(" _productName=" + _productName);
        sb.append(" _description=" + _description);
        sb.append(" _shortName=" + _shortName);
        sb.append(" _mrp=" + _mrp);
        sb.append(" _status=" + _status);
        sb.append(" _mrpStr=" + _mrpStr);
        sb.append(" _productCode=" + _productCode);
        sb.append(" _minReqQuantity=" + _minReqQuantity);
        sb.append(" _maxReqQuantity=" + _maxReqQuantity);
        sb.append(" _multipleFactor=" + _multipleFactor);
        sb.append(" _expiryPeriod=" + _expiryPeriod);
        sb.append(" _individualEntity=" + _individualEntity);
        sb.append(" _attribute1=" + _attribute1);
        sb.append(" _createdBy=" + _createdBy);
        sb.append(" _createdOn=" + _createdOn);
        sb.append(" _modifiedBy=" + _modifiedBy);
        sb.append(" _modifiedOn=" + _modifiedOn);
        sb.append(" _serviceCode=" + _serviceCode);
        sb.append(" _noOfArguments=" + _noOfArguments);
        sb.append(" _talkTime=" + _talkTime);
        sb.append(" _voucherType=" + _voucherType);
        sb.append(" segment=" + segment);
        sb.append(" _expiryDateString=" + _expiryDateString);
		sb.append(" voucherAutoGenerate=" + voucherAutoGenerate);
        sb.append(" serviceName=" + serviceName);
		//Added for voms_hcpt by niharika
		sb.append(" _itemCode=" + _itemCode);
		sb.append(" _secondaryPrefixCode=" + _secondaryPrefixCode);
        return sb.toString();
    }

    /**
     * @return Returns the checkBoxVal.
     */
    public String getCheckBoxVal() {
        return _checkBoxVal;
    }

    /**
     * @param checkBoxVal
     *            The checkBoxVal to set.
     */
    public void setCheckBoxVal(String checkBoxVal) {
        _checkBoxVal = checkBoxVal;
    }

    /**
     * @return Returns the oldProductID.
     */
    public String getOldProductID() {
        return _oldProductID;
    }

    /**
     * @param oldProductID
     *            The oldProductID to set.
     */
    public void setOldProductID(String oldProductID) {
        _oldProductID = oldProductID;
    }

    /**
     * @return Returns the oldProductName.
     */
    public String getOldProductName() {
        return _oldProductName;
    }

    /**
     * @param oldProductName
     *            The oldProductName to set.
     */
    public void setOldProductName(String oldProductName) {
        _oldProductName = oldProductName;
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
     * @return Returns the statusDesc.
     */
    public String getStatusDesc() {
        return _statusDesc;
    }

    /**
     * @param statusDesc
     *            The statusDesc to set.
     */
    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
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
     * @return Returns the talkTimeStr.
     */
    public String getTalkTimeStr() {
        return _talkTimeStr;
    }

    /**
     * @param talkTimeStr
     *            The talkTimeStr to set.
     */
    public void setTalkTimeStr(String talkTimeStr) {
        _talkTimeStr = talkTimeStr;
    }

    /**
     * @return Returns the validityStr.
     */
    public String getValidityStr() {
        return _validityStr;
    }

    /**
     * @param validityStr
     *            The validityStr to set.
     */
    public void setValidityStr(String validityStr) {
        _validityStr = validityStr;
    }

    /**
     * @return Returns the productVOList.
     */
    public ArrayList getProductVOList() {
        return _productVOList;
    }

    /**
     * @param ProductVOList
     *            The ProductVOList to set.
     */
    public void setProductVOList(ArrayList productVOList) {
        this._productVOList = productVOList;
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
     * @return Returns the attribute1.
     */
    public String getAttribute1() {
        return _attribute1;
    }

    /**
     * @param attribute1
     *            The attribute1 to set.
     */
    public void setAttribute1(String attribute1) {
        _attribute1 = attribute1;
    }

    /**
     * @return Returns the categoryID.
     */
    public String getCategoryID() {
        return _categoryID;
    }

    /**
     * @param categoryID
     *            The categoryID to set.
     */
    public void setCategoryID(String categoryID) {
        _categoryID = categoryID;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        _description = description;
    }

    /**
     * @return Returns the expiryPeriod.
     */
    public long getExpiryPeriod() {
        return _expiryPeriod;
    }

    /**
     * @param expiryPeriod
     *            The expiryPeriod to set.
     */
    public void setExpiryPeriod(long expiryPeriod) {
        _expiryPeriod = expiryPeriod;
    }

    /**
     * @return Returns the individualEntity.
     */
    public String getIndividualEntity() {
        return _individualEntity;
    }

    /**
     * @param individualEntity
     *            The individualEntity to set.
     */
    public void setIndividualEntity(String individualEntity) {
        _individualEntity = individualEntity;
    }

    /**
     * @return Returns the maxReqQuantity.
     */
    public long getMaxReqQuantity() {
        return _maxReqQuantity;
    }

    /**
     * @param maxReqQuantity
     *            The maxReqQuantity to set.
     */
    public void setMaxReqQuantity(long maxReqQuantity) {
        _maxReqQuantity = maxReqQuantity;
    }

    /**
     * @return Returns the minReqQuantity.
     */
    public long getMinReqQuantity() {
        return _minReqQuantity;
    }

    /**
     * @param minReqQuantity
     *            The minReqQuantity to set.
     */
    public void setMinReqQuantity(long minReqQuantity) {
        _minReqQuantity = minReqQuantity;
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
     * @return Returns the mrp.
     */
    public double getMrp() {
        return _mrp;
    }

    /**
     * @param mrp
     *            The mrp to set.
     */
    public void setMrp(double mrp) {
        _mrp = mrp;
    }

    /**
     * @return Returns the mrpStr.
     */
    public String getMrpStr() {
        return _mrpStr;
    }

    /**
     * @param mrpStr
     *            The mrpStr to set.
     */
    public void setMrpStr(String mrpStr) {
        _mrpStr = mrpStr;
    }

    /**
     * @return Returns the multipleFactor.
     */
    public double getMultipleFactor() {
        return _multipleFactor;
    }

    /**
     * @param multipleFactor
     *            The multipleFactor to set.
     */
    public void setMultipleFactor(double multipleFactor) {
        _multipleFactor = multipleFactor;
    }

    /**
     * @return Returns the noOfArguments.
     */
    public String getNoOfArguments() {
        return _noOfArguments;
    }

    /**
     * @param noOfArguments
     *            The noOfArguments to set.
     */
    public void setNoOfArguments(String noOfArguments) {
        _noOfArguments = noOfArguments;
    }

    /**
     * @return Returns the productID.
     */
    public String getProductID() {
        return _productID;
    }

    /**
     * @param productID
     *            The productID to set.
     */
    public void setProductID(String productID) {
        _productID = productID;
    }

    /**
     * @return Returns the productName.
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     *            The productName to set.
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    /**
     * @return Returns the serviceCode.
     */
    public String getServiceCode() {
        return _serviceCode;
    }

    /**
     * @param serviceCode
     *            The serviceCode to set.
     */
    public void setServiceCode(String serviceCode) {
        _serviceCode = serviceCode;
    }

    /**
     * @return Returns the shortName.
     */
    public String getShortName() {
        return _shortName;
    }

    /**
     * @param shortName
     *            The shortName to set.
     */
    public void setShortName(String shortName) {
        _shortName = shortName;
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
     * @return Returns the talkTime.
     */
    public double getTalkTime() {
        return _talkTime;
    }

    /**
     * @param talkTime
     *            The talkTime to set.
     */
    public void setTalkTime(double talkTime) {
        _talkTime = talkTime;
    }

    /**
     * @return Returns the validity.
     */
    public long getValidity() {
        return _validity;
    }

    /**
     * @param validity
     *            The validity to set.
     */
    public void setValidity(long validity) {
        _validity = validity;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    /**
     * @return Returns the individualEntityStr.
     */
    public String getIndividualEntityStr() {
        return _individualEntityStr;
    }

    /**
     * @param individualEntityStr
     *            The individualEntityStr to set.
     */
    public void setIndividualEntityStr(String individualEntityStr) {
        _individualEntityStr = individualEntityStr;
    }

    /**
     * @return Returns the maximumQuantity.
     */
    public long getMaximumQuantity() {
        return _maximumQuantity;
    }

    /**
     * @param maximumQuantity
     *            The maximumQuantity to set.
     */
    public void setMaximumQuantity(long maximumQuantity) {
        _maximumQuantity = maximumQuantity;
    }

    /**
     * @return Returns the minimumQuantity.
     */
    public long getMinimumQuantity() {
        return _minimumQuantity;
    }

    /**
     * @param minimumQuantity
     *            The minimumQuantity to set.
     */
    public void setMinimumQuantity(long minimumQuantity) {
        _minimumQuantity = minimumQuantity;
    }

    /**
     * @return Returns the multipleOf.
     */
    public long getMultipleOf() {
        return _multipleOf;
    }

    /**
     * @param multipleOf
     *            The multipleOf to set.
     */
    public void setMultipleOf(long multipleOf) {
        _multipleOf = multipleOf;
    }

    /**
     * @return Returns the productionLocationList.
     */
    public ArrayList getProductionLocationList() {
        return _productionLocationList;
    }

    /**
     * @param productionLocationList
     *            The productionLocationList to set.
     */
    public void setProductionLocationList(ArrayList productionLocationList) {
        _productionLocationList = productionLocationList;
    }

    /**
     * @return Returns the profile.
     */
    public String getProfile() {
        return _profile;
    }

    /**
     * @param profile
     *            The profile to set.
     */
    public void setProfile(String profile) {
        _profile = profile;
    }

    /**
     * @return Returns the subCategoryID.
     */
    public String getSubCategoryID() {
        return _subCategoryID;
    }

    /**
     * @param subCategoryID
     *            The subCategoryID to set.
     */
    public void setSubCategoryID(String subCategoryID) {
        _subCategoryID = subCategoryID;
    }

    /**
     * @return Returns the subCategoryName.
     */
    public String getSubCategoryName() {
        return _subCategoryName;
    }

    /**
     * @param subCategoryName
     *            The subCategoryName to set.
     */
    public void setSubCategoryName(String subCategoryName) {
        _subCategoryName = subCategoryName;
    }

    /**
     * @return Returns the userLocationList.
     */
    public ArrayList getUserLocationList() {
        return _userLocationList;
    }

    /**
     * @param userLocationList
     *            The userLocationList to set.
     */
    public void setUserLocationList(ArrayList userLocationList) {
        _userLocationList = userLocationList;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(int productCode) {
        _productCode = productCode;
    }

    // Added by Anjali
    /**
     * @return the type
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        _type = type;
    }

    // End

    public String getVoucherType() {
        return _voucherType;
    }

    public void setVoucherType(String type) {
        _voucherType = type;
    }

    public String getService() {
        return _service;
    }

    public void setService(String service) {
        _service = service;
    }

    public String getSubService() {
        return _subService;
    }

    public void setSubService(String service) {
        _subService = service;
    }

    public String getSerialNo() {
        return _serialNo;
    }

    public void setSerialNo(String no) {
        _serialNo = no;
    }

    public String getPinNo() {
        return _pinNo;
    }

    public void setPinNo(String no) {
        _pinNo = no;
    }

    public Date getExpiryDate() {
        return _expiryDate;
    }

    public void setExpiryDate(Date date) {
        _expiryDate = date;
    }

    public String getTxnID() {
        return _txnID;
    }

    public void setTxnID(String _txnid) {
        _txnID = _txnid;
    }

    public String getSubID() {
        return _subID;
    }

    public void setSubID(String _subid) {
        _subID = _subid;
    }

  	public String getVoucherQuantity() {
		return _voucherQuantity;
	}
  	
	public void setVoucherQuantity(String _voucherQuantity) {
		this._voucherQuantity = _voucherQuantity;
	}

	public String getExpiryDateString() {
  		return _expiryDateString;
  	}

  	public void setExpiryDateString(String expiryDateString) {
  		this._expiryDateString = expiryDateString;
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
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	//Added for voms_hcpt by niharika
	public String getItemCode() {
		return _itemCode;
	}

	public void setItemCode(String itemCode) {
		this._itemCode = itemCode;
	}

	public String getSecondaryPrefixCode() {
		return _secondaryPrefixCode;
	}

	public void setSecondaryPrefixCode(String secondaryPrefixCode) {
		this._secondaryPrefixCode = secondaryPrefixCode;
	}	
}
