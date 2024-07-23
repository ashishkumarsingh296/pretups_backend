package com.btsl.voms.voucherbundle.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class VoucherBundleVO implements Serializable {

	// Instanse variables
	private ArrayList<VoucherBundleVO> _voucherTypeList;
	private String _categoryID;
	private String _categoryName;
	private String _description;
	private String _parentID;
	private String _categoryType;
	private String _categoryShortName;
	private String _mrpStr;
	private String _mrpDesc;
	private String _global;
	private String _createdBy;
	private String _modifiedBy;
	private double _payAmount;
	private String _payAmountStr;
	private String _type;
	private String _typeDesc;
	private String serviceTypeMapping;
	private String _subService;
	private String _selectorName;
	private String _serviceType;
	private int _serviceID;
	private String networkCode;
	private String segmentDesc;
	private String serviceName;
	
	private String name;
	private String vomsBundleDetailID;
	private long lastBundleSequence;
	private String vomsBundleID;
	private String bundleName;
	private String prefixID;
	private double retailPrice;
	private double val;
	private int quantity;
	private double mrp;
	private String segment;
	private String _rowID;
	private String voucherType;
	private String denomination;
	private String voucherProfile;
	private String voucherProfileId;
	private Date _createdOn;
	private Date _modifiedOn;
	private String _status;
	private ArrayList segmentList;
	private ArrayList denominationList;
	private ArrayList voucherProfileList;
	private HashMap<String,String> voucherProfileMap;

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	/**
	 * @return the typeDesc
	 */
	public String getTypeDesc() {
		return _typeDesc;
	}

	/**
	 * @param typeDesc
	 *            the typeDesc to set
	 */
	public void setTypeDesc(String typeDesc) {
		_typeDesc = typeDesc;
	}

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

	public double getPayAmount() {
		return _payAmount;
	}

	public void setPayAmount(double amount) {
		_payAmount = amount;
	}

	public String getPayAmountStr() {
		return _payAmountStr;
	}

	public void setPayAmountStr(String payAmountStr) {
		_payAmountStr = payAmountStr;
	}

	/**
	 * Method toString. This method is used to display all of the information of
	 * the object of the VomsCategoryVO class.
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" _categoryID=" + _categoryID);
		sb.append(" _categoryName=" + _categoryName);
		sb.append(" _description=" + _description);
		sb.append(" _parentID=" + _parentID);
		sb.append(" _categoryType=" + _categoryType);
		sb.append(" _categoryShortName=" + _categoryShortName);
		sb.append(" mrp=" + mrp);
		sb.append(" _status=" + _status);
		sb.append(" _global=" + _global);
		sb.append(" _payAmount=" + _payAmount);
		sb.append(" voucherType=" + voucherType);
		sb.append(" name=" + name);
		sb.append(" serviceTypeMapping=" + serviceTypeMapping);
		sb.append(" segment=" + segment);
		sb.append(" serviceName=" + serviceName);
		sb.append(" voucherProfileId=" + voucherProfileId);		
		sb.append(" voucherProfileMap=" + voucherProfileMap);
		return sb.toString();
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
	 * @return Returns the mrpDesc.
	 */
	public String getMrpDesc() {
		return _mrpDesc;
	}

	/**
	 * @param mrpDesc
	 *            The mrpDesc to set.
	 */
	public void setMrpDesc(String mrpDesc) {
		_mrpDesc = mrpDesc;
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
	 * @return Returns the categoryName.
	 */
	public String getCategoryName() {
		return _categoryName;
	}

	/**
	 * @param categoryName
	 *            The categoryName to set.
	 */
	public void setCategoryName(String categoryName) {
		_categoryName = categoryName;
	}

	/**
	 * @return Returns the categoryShortName.
	 */
	public String getCategoryShortName() {
		return _categoryShortName;
	}

	/**
	 * @param categoryShortName
	 *            The categoryShortName to set.
	 */
	public void setCategoryShortName(String categoryShortName) {
		_categoryShortName = categoryShortName;
	}

	/**
	 * @return Returns the categoryType.
	 */
	public String getCategoryType() {
		return _categoryType;
	}

	/**
	 * @param categoryType
	 *            The categoryType to set.
	 */
	public void setCategoryType(String categoryType) {
		_categoryType = categoryType;
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
	 * @return Returns the global.
	 */
	public String getGlobal() {
		return _global;
	}

	/**
	 * @param global
	 *            The global to set.
	 */
	public void setGlobal(String global) {
		_global = global;
	}

	/**
	 * @return Returns the mrp.
	 */

	/**
	 * @return Returns the parentID.
	 */
	public String getParentID() {
		return _parentID;
	}

	/**
	 * @param parentID
	 *            The parentID to set.
	 */
	public void setParentID(String parentID) {
		_parentID = parentID;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceTypeMapping() {
		return serviceTypeMapping;
	}

	public void setServiceTypeMapping(String serviceTypeMapping) {
		this.serviceTypeMapping = serviceTypeMapping;
	}

	public ArrayList<VoucherBundleVO> getVoucherTypeList() {
		return _voucherTypeList;
	}

	public void setVoucherTypeList(ArrayList<VoucherBundleVO> typeList) {
		_voucherTypeList = typeList;
	}

	public int getVoucherTypeListSize() {
		if (_voucherTypeList != null) {
			return _voucherTypeList.size();
		} else {
			return 0;
		}
	}

	public String getSubService() {
		return _subService;
	}

	public void setSubService(String service) {
		_subService = service;
	}

	public String getSelectorName() {
		return _selectorName;
	}

	public void setSelectorName(String name) {
		_selectorName = name;
	}

	public String getServiceType() {
		return _serviceType;
	}

	public void setServiceType(String serviceType) {
		_serviceType = serviceType;
	}

	public int getServiceID() {
		return _serviceID;
	}

	public void setServiceID(int _serviceid) {
		_serviceID = _serviceid;
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

	public String getRowID() {
		return _rowID;
	}

	public void setRowID(String rowID) {
		_rowID = rowID;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getVoucherProfile() {
		return voucherProfile;
	}

	public void setVoucherProfile(String voucherProfile) {
		this.voucherProfile = voucherProfile;
	}

	public ArrayList getSegmentList() {
		return segmentList;
	}

	public void setSegmentList(ArrayList segmentList) {
		this.segmentList = segmentList;
	}

	public ArrayList getDenominationList() {
		return denominationList;
	}

	public void setDenominationList(ArrayList denominationList) {
		this.denominationList = denominationList;
	}

	public ArrayList getVoucherProfileList() {
		return voucherProfileList;
	}

	public void setVoucherProfileList(ArrayList voucherProfileList) {
		this.voucherProfileList = voucherProfileList;
	}

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getMrp() {
		return mrp;
	}

	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	public String getVomsBundleID() {
		return vomsBundleID;
	}

	public void setVomsBundleID(String vomsBundleID) {
		this.vomsBundleID = vomsBundleID;
	}

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getPrefixID() {
		return prefixID;
	}

	public void setPrefixID(String prefixID) {
		this.prefixID = prefixID;
	}

	public long getLastBundleSequence() {
		return lastBundleSequence;
	}

	public void setLastBundleSequence(long lastBundleSequence) {
		this.lastBundleSequence = lastBundleSequence;
	}

	public String getVomsBundleDetailID() {
		return vomsBundleDetailID;
	}

	public void setVomsBundleDetailID(String vomsBundleDetailID) {
		this.vomsBundleDetailID = vomsBundleDetailID;
	}

	public double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getVoucherProfileId() {
		return voucherProfileId;
	}

	public void setVoucherProfileId(String voucherProfileId) {
		this.voucherProfileId = voucherProfileId;
	}

	public HashMap<String, String> getVoucherProfileMap() {
		return voucherProfileMap;
	}

	public void setVoucherProfileMap(HashMap<String, String> voucherProfileMap) {
		this.voucherProfileMap = voucherProfileMap;
	}
	
	

}
