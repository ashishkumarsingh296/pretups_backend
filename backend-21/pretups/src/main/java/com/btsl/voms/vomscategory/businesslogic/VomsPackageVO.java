package com.btsl.voms.vomscategory.businesslogic;

import java.io.Serializable;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;

public class VomsPackageVO implements Serializable{
//	toad.VOMS_BUNDLE_ID, toad.BUNDLE_NAME, toad.BUNDLE_PREFIX, toad.RETAIL_PRICE, toad.LAST_BUNDLE_SEQUENCE, toad.CREATED_ON, toad.CREATED_BY, toad.MODIFIED_ON, toad.MODIFIED_BY, toad.STATUS
	private long bundleID;
	private String bundleName;
	private String bundlePrefix;
	private double retailPrice;
	private double profileMRP;
	private String profileQuantity;
	private int rowIndex;
	private double packageAmount;	
    private ChannelTransferItemsVO _channelTransferItemsVO;
    
	private java.util.Date modifiedDate;
    private java.util.Date modifiedOn;
    private java.util.Date createdDate;
    private java.util.Date createdOn;
    private String toUserID = null; 
    private String extTxnNo;
    private String createdBy;
    private String networkCode = null;
    private String voucherType = null;
    private String bundleLabel = null; //display bundle name on confirm and final pages
    private String remarks;
    private long maxProfileQuantity = 0;


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VomsPackageVO [bundleID=" + bundleID);
		sb.append(", bundleName=" + bundleName);
		sb.append(", bundlePrefix=" + bundlePrefix);
		sb.append(", retailPrice=" + retailPrice);
		sb.append(", profileMRP=" + profileMRP);
		sb.append(", profileQuantity=" + profileQuantity);
		sb.append(", rowIndex=" + rowIndex);
		sb.append(", packageAmount=" + packageAmount);
		sb.append(", _channelTransferItemsVO=" + _channelTransferItemsVO);
		sb.append(", modifiedDate=" + modifiedDate);
		sb.append(", modifiedOn=" + modifiedOn);
		sb.append(", createdDate=" + createdDate);
		sb.append(", createdOn=" + createdOn);
		sb.append(", toUserID=" + toUserID);
		sb.append(", extTxnNo=" + extTxnNo);
		sb.append(", createdBy=" + createdBy);
		sb.append(", networkCode=" + networkCode);
		sb.append(", voucherType=" + voucherType);
		sb.append(", bundleLabel=" + bundleLabel);
		sb.append(", remarks=" + remarks);
		sb.append(", maxProfileQuantity=" + maxProfileQuantity);
		
		return sb.toString();
	}

	public long getBundleID() {
		return bundleID;
	}

	public void setBundleID(long bundleID) {
		this.bundleID = bundleID;
	}

	public String getBundleName() { //will be set in this format: "bundleID:retailPrice:bundleName"
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getBundlePrefix() {
		return bundlePrefix;
	}

	public void setBundlePrefix(String bundlePrefix) {
		this.bundlePrefix = bundlePrefix;
	}


	public double getProfileMRP() {
		return profileMRP;
	}

	public void setProfileMRP(double profileMRP) {
		this.profileMRP = profileMRP;
	}

	public String getProfileQuantity() {
		return profileQuantity;
	}

	public void setProfileQuantity(String profileQuantity) {
		this.profileQuantity = profileQuantity;
	}
	
	  public double getPackageAmount() {
		return packageAmount;
	}

	public void setPackageAmount(double packageAmount) {
		this.packageAmount = packageAmount;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

    public ChannelTransferItemsVO getChannelTransferItemsVO() {
        return _channelTransferItemsVO;
    }

    public void setChannelTransferItemsVO(ChannelTransferItemsVO channelTransferItemsVO) {
        _channelTransferItemsVO = new ChannelTransferItemsVO();
        _channelTransferItemsVO = channelTransferItemsVO;
    }
    
    public java.util.Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(java.util.Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public java.util.Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(java.util.Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public java.util.Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(java.util.Date createdDate) {
		this.createdDate = createdDate;
	}

	public java.util.Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(java.util.Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getToUserID() {
		return toUserID;
	}

	public void setToUserID(String toUserID) {
		this.toUserID = toUserID;
	}

	public String getExtTxnNo() {
		return extTxnNo;
	}

	public void setExtTxnNo(String extTxnNo) {
		this.extTxnNo = extTxnNo;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getBundleLabel() {
		return bundleLabel;
	}

	public void setBundleLabel(String bundleLabel) {
		this.bundleLabel = bundleLabel;
	}
	public double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getMaxProfileQuantity() {
		return maxProfileQuantity;
	}

	public void setMaxProfileQuantity(long maxProfileQuantity) {
		this.maxProfileQuantity = maxProfileQuantity;
	}
}
