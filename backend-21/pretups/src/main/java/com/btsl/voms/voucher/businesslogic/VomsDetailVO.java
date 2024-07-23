package com.btsl.voms.voucher.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;

/**
 * @(#)RightelVoucherVO.java
 *                          Copyright(c) 2006, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Sanjay Kumar Bind1 June 29, 2018 Initial Creation
 * 
 */

public class VomsDetailVO implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private String _fileName = null;
    private String _filePath = null;
    private int _noOfRecordsInFile = 0;
    private int _maxNoOfRecordsAllowed = 0;
    private String _productID = null;
    private ChannelUserVO _channelUserVO = null;
    private Date _currentDate = null;
    private String _fromSerialNumber = null;
    private String _toSerialNumber = null;
    private int _actualNoOfRecords = 0;
    private ArrayList _voucherArrayList = null;
    public static final String _MANUALPROCESSTYPE = "MANUAL";
    public static final String _AUTOPROCESSTYPE = "AUTO";
    private String _processType = _MANUALPROCESSTYPE; // Dont set if process
                                                      // type is manual else set
                                                      // AUTO
    private ArrayList _errorArrayList = null; // Added to save information about
                                              // errors in voucher file
    //Added by Sanjay
    private String _branchCode = null;
    private String _cityCode = null;
    private String _terminalType = null;
    private String _terminalCode = null;
    private Date _creditSaleDate = null;
    private String _serialNumber = null;
    private String _serialNoCdigit = null;
    private String _paymentIdNumber = null;
    private String _PaymentType = null;
    private long _txnId = 0;
    private long _cardNumber = 0;
    private String _status = null;
    private String _remarks = null;
    private String _externalCode = null;
    private String _voucherStatus = null;
    private String _productId = null;

    private String _batchId = null;
    private String _userId = null;
    private String _networkCode = null;
    private String _networkCodeFor = null;
    private String _companyName = null;
    private String _operatorCode = null;
    private String _bankCode = null;
    private String _batchFileName = null;
    private long _totalRecord = 0;
    private long _totalAmount = 0;
    private Date _batchDate = null;
    private String _createdBy = null;
    private Date _createdOn = null;
    private String _modifiedBy = null;
    private Date _modifiedOn = null;
    
    
    public String getFileName() {
		return _fileName;
	}
	public void setFileName(String fileName) {
		_fileName = fileName;
	}
	public String getFilePath() {
		return _filePath;
	}
	public void setFilePath(String filePath) {
		_filePath = filePath;
	}
	public int getNoOfRecordsInFile() {
		return _noOfRecordsInFile;
	}
	public void setNoOfRecordsInFile(int noOfRecordsInFile) {
		_noOfRecordsInFile = noOfRecordsInFile;
	}
	public int getMaxNoOfRecordsAllowed() {
		return _maxNoOfRecordsAllowed;
	}
	public void setMaxNoOfRecordsAllowed(int maxNoOfRecordsAllowed) {
		_maxNoOfRecordsAllowed = maxNoOfRecordsAllowed;
	}
	public String getProductID() {
		return _productID;
	}
	public void setProductID(String productID) {
		_productID = productID;
	}
	public ChannelUserVO getChannelUserVO() {
		return _channelUserVO;
	}
	public void setChannelUserVO(ChannelUserVO channelUserVO) {
		_channelUserVO = channelUserVO;
	}
	public Date getCurrentDate() {
		return _currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		_currentDate = currentDate;
	}
	public String getFromSerialNo() {
		return _fromSerialNumber;
	}
	public void setFromSerialNo(String fromSerialNumber) {
		_fromSerialNumber = fromSerialNumber;
	}
	public String getToSerialNo() {
		return _toSerialNumber;
	}
	public void setToSerialNo(String toSerialNumber) {
		_toSerialNumber = toSerialNumber;
	}
	public int getActualNoOfRecords() {
		return _actualNoOfRecords;
	}
	public void setActualNoOfRecords(int actualNoOfRecords) {
		_actualNoOfRecords = actualNoOfRecords;
	}
	public ArrayList getVoucherArrayList() {
		return _voucherArrayList;
	}
	public void setVoucherArrayList(ArrayList voucherArrayList) {
		_voucherArrayList = voucherArrayList;
	}
	public String getProcessType() {
		return _processType;
	}
	public void setProcessType(String processType) {
		_processType = processType;
	}
	public ArrayList getErrorArrayList() {
		return _errorArrayList;
	}
	public void setErrorArrayList(ArrayList errorArrayList) {
		_errorArrayList = errorArrayList;
	}
	public String getBranchCode() {
		return _branchCode;
	}
	public void setBranchCode(String branchCode) {
		_branchCode = branchCode;
	}
	public String getCityCode() {
		return _cityCode;
	}
	public void setCityCode(String cityCode) {
		_cityCode = cityCode;
	}
	public String getTerminalType() {
		return _terminalType;
	}
	public void setTerminalType(String terminalType) {
		_terminalType = terminalType;
	}
	public String getTerminalCode() {
		return _terminalCode;
	}
	public void setTerminalCode(String terminalCode) {
		_terminalCode = terminalCode;
	}
	public Date getCreditSaleDate() {
		return _creditSaleDate;
	}
	public void setCreditSaleDate(Date creditSaleDate) {
		_creditSaleDate = creditSaleDate;
	}
	public String getSerialNumber() {
		return _serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		_serialNumber = serialNumber;
	}
	public String getSerialNoCdigit() {
		return _serialNoCdigit;
	}
	public void setSerialNoCdigit(String serialNoCdigit) {
		_serialNoCdigit = serialNoCdigit;
	}
	public String getPaymentIdNumber() {
		return _paymentIdNumber;
	}
	public void setPaymentIdNumber(String paymentIdNumber) {
		_paymentIdNumber = paymentIdNumber;
	}
	public String getPaymentType() {
		return _PaymentType;
	}
	public void setPaymentType(String PaymentType) {
		_PaymentType = PaymentType;
	}
	public long getTxnId() {
		return _txnId;
	}
	public void setTxnId(long txnId) {
		_txnId = txnId;
	}
	public long getCardNumber() {
		return _cardNumber;
	}
	public void setCardNumber(long cardNumber) {
		_cardNumber = cardNumber;
	}
	public String getStatus() {
		return _status;
	}
	public void setStatus(String status) {
		_status = status;
	}
	public String getRemarks() {
		return _remarks;
	}
	public void setRemarks(String remarks) {
		_remarks = remarks;
	}
	public String getExternalCode() {
		return _externalCode;
	}
	public void setExternalCode(String externalCode) {
		_externalCode = externalCode;
	}
	public String getVoucherStatus() {
		return _voucherStatus;
	}
	public void setVoucherStatus(String voucherStatus) {
		_voucherStatus = voucherStatus;
	}
	public String getProductId() {
		return _productId;
	}
	public void setProductId(String productId) {
		_productId = productId;
	}
	public String getBatchId() {
		return _batchId;
	}
	public void setBatchId(String batchId) {
		_batchId = batchId;
	}
	public String getUserId() {
		return _userId;
	}
	public void setUserId(String userId) {
		_userId = userId;
	}
	public String getNetworkCode() {
		return _networkCode;
	}
	public void setNetworkCode(String networkCode) {
		_networkCode = networkCode;
	}
	public String getNetworkCodeFor() {
		return _networkCodeFor;
	}
	public void setNetworkCodeFor(String networkCodeFor) {
		_networkCodeFor = networkCodeFor;
	}
	public String getCompanyName() {
		return _companyName;
	}
	public void setCompanyName(String companyName) {
		_companyName = companyName;
	}
	public String getOperatorCode() {
		return _operatorCode;
	}
	public void setOperatorCode(String operatorCode) {
		_operatorCode = operatorCode;
	}
	public String getBankCode() {
		return _bankCode;
	}
	public void setBankCode(String bankCode) {
		_bankCode = bankCode;
	}
	public String getBatchFileName() {
		return _batchFileName;
	}
	public void setBatchFileName(String batchFileName) {
		_batchFileName = batchFileName;
	}
	public long getTotalRecord() {
		return _totalRecord;
	}
	public void setTotalRecord(long totalRecord) {
		_totalRecord = totalRecord;
	}
	public long getTotalAmount() {
		return _totalAmount;
	}
	public void setTotalAmount(long totalAmount) {
		_totalAmount = totalAmount;
	}
	public Date getBatchDate() {
		return _batchDate;
	}
	public void setBatchDate(Date batchDate) {
		_batchDate = batchDate;
	}
	public String getCreatedBy() {
		return _createdBy;
	}
	public void setCreatedBy(String createdBy) {
		_createdBy = createdBy;
	}
	public Date getCreatedOn() {
		return _createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		_createdOn = createdOn;
	}
	public String getModifiedBy() {
		return _modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		_modifiedBy = modifiedBy;
	}
	public Date getModifiedOn() {
		return _modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		_modifiedOn = modifiedOn;
	}
	
}
