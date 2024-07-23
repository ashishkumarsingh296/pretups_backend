package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ChannelVoucherItemsVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(ChannelVoucherItemsVO.class.getName());
	
    private String _transferId;
	private long _SNo;
    private Date _transferDate;
    private String _voucherType;
    private long _transferMrp;
    private String _productId;
    private long _requestedQuantity;
    private String _fromSerialNum;
	private String _toSerialNum;
	private String _actionType;
	private String _voucherTypeDesc;
	private String productName;
	private String segment;
	private String segmentDesc;
	private String networkCode;
	private String type;
	private String fromUser;
	private String toUser;
	private String modifiedOn;
	private long initiatedQuantity;
	private long firstLevelApprovedQuantity;
	private long secondLevelApprovedQuantity;
	private long bundleId;
	private String bundleName;
	private String bundleRemarks;
	
   
	
	public long getInitiatedQuantity() {
		return initiatedQuantity;
	}
	public void setInitiatedQuantity(long initiatedQuantity) {
		this.initiatedQuantity = initiatedQuantity;
	}
	public long getFirstLevelApprovedQuantity() {
		return firstLevelApprovedQuantity;
	}
	public void setFirstLevelApprovedQuantity(long firstLevelApprovedQuantity) {
		this.firstLevelApprovedQuantity = firstLevelApprovedQuantity;
	}
	public long getSecondLevelApprovedQuantity() {
		return secondLevelApprovedQuantity;
	}
	public void setSecondLevelApprovedQuantity(long secondLevelApprovedQuantity) {
		this.secondLevelApprovedQuantity = secondLevelApprovedQuantity;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getTransferId() {
		return _transferId;
	}
	public void setTransferId(String _transferId) {
		this._transferId = _transferId;
	}
	public long getSNo() {
		return _SNo;
	}
	public void setSNo(long SNo) {
		this._SNo = SNo;
	}
	public Date getTransferDate() {
		return _transferDate;
	}
	public void setTransferDate(Date _transferDate) {
		this._transferDate = _transferDate;
	}
	public String getVoucherType() {
		return _voucherType;
	}
	public void setVoucherType(String _voucherType) {
		this._voucherType = _voucherType;
	}
	
	public String getVoucherTypeDesc() {
		return _voucherTypeDesc;
	}
	public void setVoucherTypeDesc(String _voucherTypeDesc) {
		this._voucherTypeDesc = _voucherTypeDesc;
	}
	
	public long getTransferMrp() {
		return _transferMrp;
	}
	public void setTransferMRP(long _transferMrp) {
		this._transferMrp = _transferMrp;
	}
	public String getFromSerialNum() {
		return _fromSerialNum;
	}
	public void setFromSerialNum(String _fromSerialNum) {
		this._fromSerialNum = _fromSerialNum;
	}
	public String getToSerialNum() {
		return _toSerialNum;
	}
	public void setToSerialNum(String _toSerialNum) {
		this._toSerialNum = _toSerialNum;
	}
	public long getRequiredQuantity() {
		return _requestedQuantity;
	}
	public void setRequiredQuantity(long _requestedQuantity) {
		this._requestedQuantity = _requestedQuantity;
	}
	public String getProductId() {
		return _productId;
	}
	public void setProductId(String _productId) {
		this._productId = _productId;
	}
	public String getActionType() {
		return _actionType;
	}
	public void setActionType(String actionType) {
		this._actionType = actionType;
	}
	
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
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
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public long getBundleId() {
		return bundleId;
	}	
	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}
	public String getBundleName() {
		return bundleName;
	}
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	public String getBundleRemarks() {
		return bundleRemarks;
	}
	public void setBundleRemarks(String bundleRemarks) {
		this.bundleRemarks = bundleRemarks;
	}
	
	@Override
	public String toString() {
        final StringBuffer sbf = new StringBuffer(" transferID : " + _transferId);
        sbf.append(", S No : " + _SNo);
        sbf.append(", transfer Date : " + _transferDate);
        sbf.append(", voucher Type : " + _voucherType);
        sbf.append(", transfer Mrp : " + _transferMrp);
        sbf.append(", Product Id : " + _productId);
        sbf.append(", Requested Quantity : " + _requestedQuantity);
        sbf.append(", From Serial Number : " + _fromSerialNum);
        sbf.append(", To Serial Number : " + _toSerialNum);
        sbf.append(", Action Type : " + _actionType);
        sbf.append(", Voucher Type Desc : " + _voucherTypeDesc);
        sbf.append(", Product Name : " + productName);
        sbf.append(", Type : " + type);
        sbf.append(", From User : " + fromUser);
        sbf.append(", To User : " + toUser);
        sbf.append(", Modified On : " + modifiedOn);
        sbf.append(", Initiated Quantity : " + initiatedQuantity);
        sbf.append(", First Level Approved Quantity : " + firstLevelApprovedQuantity);
        sbf.append(", Second Level Approved Quantity : " + secondLevelApprovedQuantity);
        return sbf.toString();
	}
	
}
