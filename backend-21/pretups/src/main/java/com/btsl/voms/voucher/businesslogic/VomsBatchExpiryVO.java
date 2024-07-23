package com.btsl.voms.voucher.businesslogic;

import java.io.Serializable;
import java.util.Date;


/**
 * @author karun.sood
 *
 */
public class VomsBatchExpiryVO implements Serializable {

	private String _batchNo;
	private long _noOfVoucher;
	private String _fromSerialNo;
    private String _toSerialNo;
    private long _successCount;
    private long _failCount;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;
    private Date _expiryDate;
    private String _executionStatus;
    private String _filename;
    private String _status;
    private String _voucherType;
    
    
	public String getVoucherType() {
		return _voucherType;
	}
	public void setVoucherType(String _voucherType) {
		this._voucherType = _voucherType;
	}
    
	public String getBatchNo() {
		return _batchNo;
	}
	public long getNoOfVoucher() {
		return _noOfVoucher;
	}
	public String getFromSerialNo() {
		return _fromSerialNo;
	}
	public String getToSerialNo() {
		return _toSerialNo;
	}
	public long getSuccessCount() {
		return _successCount;
	}
	public long getFailCount() {
		return _failCount;
	}
	public String getCreatedBy() {
		return _createdBy;
	}
	public Date getCreatedOn() {
		return _createdOn;
	}
	public String getModifiedBy() {
		return _modifiedBy;
	}
	public Date getModifiedOn() {
		return _modifiedOn;
	}
	public Date getExpiryDate() {
		return _expiryDate;
	}
	public String getExecutionStatus() {
		return _executionStatus;
	}
	public void setBatchNo(String _batchNo) {
		this._batchNo = _batchNo;
	}
	public void setNoOfVoucher(long _noOfVoucher) {
		this._noOfVoucher = _noOfVoucher;
	}
	public void setFromSerialNo(String _fromSerialNo) {
		this._fromSerialNo = _fromSerialNo;
	}
	public void setToSerialNo(String _toSerialNo) {
		this._toSerialNo = _toSerialNo;
	}
	public void setSuccessCount(long _successCount) {
		this._successCount = _successCount;
	}
	public void setFailCount(long _failCount) {
		this._failCount = _failCount;
	}
	public void setCreatedBy(String _createdBy) {
		this._createdBy = _createdBy;
	}
	public void setCreatedOn(Date _createdOn) {
		this._createdOn = _createdOn;
	}
	public void setModifiedBy(String _modifiedBy) {
		this._modifiedBy = _modifiedBy;
	}
	public void setModifiedOn(Date _modifiedOn) {
		this._modifiedOn = _modifiedOn;
	}
	public void setExpiryDate(Date _expiryDate) {
		this._expiryDate = _expiryDate;
	}
	public void setExecutionStatus(String _executionStatus) {
		this._executionStatus = _executionStatus;
	}
	public String getFilename() {
		return _filename;
	}
	public void setFilename(String _filename) {
		this._filename = _filename;
	}
	public String getStatus() {
		return _status;
	}
	public void setStatus(String _status) {
		this._status = _status;
	}
	@Override
	public String toString() {
		return "VomsBatchExpiryVO [_batchNo=" + _batchNo + ", _noOfVoucher=" + _noOfVoucher + ", _fromSerialNo="
				+ _fromSerialNo + ", _toSerialNo=" + _toSerialNo + ", _successCount=" + _successCount + ", _failCount="
				+ _failCount + ", _createdBy=" + _createdBy + ", _createdOn=" + _createdOn + ", _modifiedBy="
				+ _modifiedBy + ", _modifiedOn=" + _modifiedOn + ", _expiryDate=" + _expiryDate + ", _executionStatus="
				+ _executionStatus + ", _filename=" + _filename + ", _status=" + _status + "]";
	}

}
