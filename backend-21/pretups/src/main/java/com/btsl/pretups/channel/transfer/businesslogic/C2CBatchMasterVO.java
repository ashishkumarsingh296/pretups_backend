/*
 * @# FOCBatchMasterVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 22, 2006 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class C2CBatchMasterVO implements Serializable {
	//Operator BatchID
    private String _OptBatchId= null;
    private ChannelUserVO channelUserVO;
    private String _batchId = null;
    private String _networkCode = null;
    private String _networkCodeFor = null;
    private String _batchName = null;
    private String _status = null;
    private String _statusDesc = null;
    private String _domainCode = null;
    private String _domainCodeDesc = null;
    private String _productCode = null;
    private String _productCodeDesc = null;
    private String _batchFileName = null;
    private int _batchTotalRecord;
    private Date _batchDate = null;
    private String _batchDateStr = null;
    private String _createdBy = null;
    private Date _createdOn = null;
    private String _modifiedBy = null;
    private Date _modifiedOn = null;

    private int _newRecords = 0;
    private int _approvedRecords = 0;
    private int _rejectedRecords = 0;
    private int _closedRecords = 0;
    private String _productName = null;
    private long _productMrp;
    private String _productMrpStr;
    private C2CBatchItemsVO _c2cBatchItemsVO = null;
    private String _productType = null;
    private String _productShortName = null;

    // fields to store c2c notification message
    private String _defaultLang = null;
    private String _secondLang = null;
    private String _userId = null;
    private String _transferType = null;
    private String _transferSubType = null;

    private String _categoryCode = null;

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append("_batchId =" + _batchId);
        sbf.append(",_networkCode=" + _networkCode);
        sbf.append(",_networkCodeFor=" + _networkCodeFor);
        sbf.append(",_batchName=" + _batchName);
        sbf.append(",_status=" + _status);
        sbf.append(",_domainCode=" + _domainCode);
        sbf.append(",_productCode=" + _productCode);
        sbf.append(",_batchFileName=" + _batchFileName);
        sbf.append(",_batchTotalRecord=" + _batchTotalRecord);
        sbf.append(",_batchDate=" + _batchDate);
        sbf.append(",_createdBy=" + _createdBy);
        sbf.append(",_createdOn=" + _createdOn);
        sbf.append(",_userId=" + _userId);
        sbf.append(",_modifiedBy=" + _modifiedBy);
        sbf.append(",_modifiedOn=" + _modifiedOn);
        sbf.append(",_newRecords=" + _newRecords);
        sbf.append(",_approvedRecords=" + _approvedRecords);
        sbf.append(",_rejectedRecords=" + _rejectedRecords);
        sbf.append(",_closedRecords=" + _closedRecords);
        sbf.append(",_productName=" + _productName);
        sbf.append(",_productMrp=" + _productMrp);
        sbf.append(",_productMrpStr=" + _productMrpStr);
        sbf.append(",_c2cBatchItemsVO=" + _c2cBatchItemsVO);
        sbf.append(",_productType=" + _productType);
        sbf.append(",_productShortName=" + _productShortName);
        return sbf.toString();
    }

    /**
     * @return Returns the defaultLang.
     */
    public String getDefaultLang() {
        return _defaultLang;
    }

    /**
     * @param defaultLang
     *            The defaultLang to set.
     */
    public void setDefaultLang(String defaultLang) {
        _defaultLang = defaultLang;
    }

    /**
     * @return Returns the secondLang.
     */
    public String getSecondLang() {
        return _secondLang;
    }

    /**
     * @param secondLang
     *            The secondLang to set.
     */
    public void setSecondLang(String secondLang) {
        _secondLang = secondLang;
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
     * @return Returns the batchDateStr.
     */
    public String getBatchDateStr() {
        return _batchDateStr;
    }

    /**
     * @param batchDateStr
     *            The batchDateStr to set.
     */
    public void setBatchDateStr(String batchDateStr) {
        _batchDateStr = batchDateStr;
    }

    /**
     * @return Returns the productShortName.
     */
    public String getProductShortName() {
        return _productShortName;
    }

    /**
     * @param productShortName
     *            The productShortName to set.
     */
    public void setProductShortName(String productShortName) {
        _productShortName = productShortName;
    }

    /**
     * @return Returns the productType.
     */
    public String getProductType() {
        return _productType;
    }

    /**
     * @param productType
     *            The productType to set.
     */
    public void setProductType(String productType) {
        _productType = productType;
    }

    /**
     * @return Returns the productCodeDesc.
     */
    public String getProductCodeDesc() {
        return _productCodeDesc;
    }

    /**
     * @param productCodeDesc
     *            The productCodeDesc to set.
     */
    public void setProductCodeDesc(String productCodeDesc) {
        _productCodeDesc = productCodeDesc;
    }

    /**
     * @return Returns the domainCodeDesc.
     */
    public String getDomainCodeDesc() {
        return _domainCodeDesc;
    }

    /**
     * @param domainCodeDesc
     *            The domainCodeDesc to set.
     */
    public void setDomainCodeDesc(String domainCodeDesc) {
        _domainCodeDesc = domainCodeDesc;
    }

    /**
     * @return Returns the C2CBatchItemsVO.
     */
    public C2CBatchItemsVO getC2cBatchItemsVO() {
        return _c2cBatchItemsVO;
    }

    /**
     * @param batchItemsVO
     *            The C2CBatchItemsVO to set.
     */
    public void setC2cBatchItemsVO(C2CBatchItemsVO batchItemsVO) {
        _c2cBatchItemsVO = batchItemsVO;
    }

    /**
     * @return Returns the batchDate.
     */
    public Date getBatchDate() {
        return _batchDate;
    }

    /**
     * @param batchDate
     *            The batchDate to set.
     */
    public void setBatchDate(Date batchDate) {
        _batchDate = batchDate;
    }

    /**
     * @return Returns the batchFileName.
     */
    public String getBatchFileName() {
        return _batchFileName;
    }

    /**
     * @param batchFileName
     *            The batchFileName to set.
     */
    public void setBatchFileName(String batchFileName) {
        _batchFileName = batchFileName;
    }

    /**
     * @return Returns the batchId.
     */
    public String getBatchId() {
        return _batchId;
    }

    /**
     * @param batchId
     *            The batchId to set.
     */
    public void setBatchId(String batchId) {
        _batchId = batchId;
    }

    /**
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return _batchName;
    }

    /**
     * @param batchName
     *            The batchName to set.
     */
    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    /**
     * @return Returns the batchTotalRecord.
     */
    public int getBatchTotalRecord() {
        return _batchTotalRecord;
    }

    /**
     * @param batchTotalRecord
     *            The batchTotalRecord to set.
     */
    public void setBatchTotalRecord(int batchTotalRecord) {
        _batchTotalRecord = batchTotalRecord;
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
     * @return Returns the domainCode.
     */
    public String getDomainCode() {
        return _domainCode;
    }

    /**
     * @param domainCode
     *            The domainCode to set.
     */
    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
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
     * @return Returns the networkCodeFor.
     */
    public String getNetworkCodeFor() {
        return _networkCodeFor;
    }

    /**
     * @param networkCodeFor
     *            The networkCodeFor to set.
     */
    public void setNetworkCodeFor(String networkCodeFor) {
        _networkCodeFor = networkCodeFor;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        _productCode = productCode;
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
     * @return Returns the ApprovedRecords.
     */
    public int getApprovedRecords() {
        return _approvedRecords;
    }

    /**
     * @param ApprovedRecords
     *            The ApprovedRecords to set.
     */
    public void setApprovedRecords(int ApprovedRecords) {
        _approvedRecords = ApprovedRecords;
    }

    /**
     * @return Returns the newRecords.
     */
    public int getNewRecords() {
        return _newRecords;
    }

    /**
     * @param newRecords
     *            The newRecords to set.
     */
    public void setNewRecords(int newRecords) {
        _newRecords = newRecords;
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
     * @return Returns the rejectedRecords.
     */
    public int getRejectedRecords() {
        return _rejectedRecords;
    }

    /**
     * @param rejectedRecords
     *            The rejectedRecords to set.
     */
    public void setRejectedRecords(int rejectedRecords) {
        _rejectedRecords = rejectedRecords;
    }

    /**
     * @return Returns the closedRecords.
     */
    public int getClosedRecords() {
        return _closedRecords;
    }

    /**
     * @param closedRecords
     *            The closedRecords to set.
     */
    public void setClosedRecords(int closedRecords) {
        _closedRecords = closedRecords;
    }

    /**
     * @return Returns the productMrp.
     */
    public long getProductMrp() {
        return _productMrp;
    }

    /**
     * @param productMrp
     *            The productMrp to set.
     */
    public void setProductMrp(long productMrp) {
        _productMrp = productMrp;
    }

    /**
     * @return Returns the productMrpStr.
     */
    public String getProductMrpStr() {
        return _productMrpStr;
    }

    /**
     * @param productMrpStr
     *            The productMrpStr to set.
     */
    public void setProductMrpStr(String productMrpStr) {
        _productMrpStr = productMrpStr;
    }

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public String getTransferSubType() {
        return _transferSubType;
    }

    public void setTransferSubType(String transferSubType) {
        _transferSubType = transferSubType;
    }

    public String getTransferType() {
        return _transferType;
    }

    public void setTransferType(String transferType) {
        _transferType = transferType;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String code) {
        _categoryCode = code;
    }
	
	public String getOptBatchId() {
		return _OptBatchId;
	}

	public void setOptBatchId(String optBatchId) {
		_OptBatchId = optBatchId;
	}

	public ChannelUserVO getChannelUserVO() {
		return channelUserVO;
	}

	public void setChannelUserVO(ChannelUserVO channelUserVO) {
		this.channelUserVO = channelUserVO;
	}
	
	//Depends only on sender UserID as Integer
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        String tempUserId =null;
        try {
			tempUserId = _userId.replaceAll("([A-Z])", "");
		} catch (Exception e) {
			tempUserId = _userId.replaceAll("([a-z])", "");
		}
        result = (prime * result) + Integer.parseInt(tempUserId); 
        return result;
    }
 
    //Compare only sender UserID 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        C2CBatchMasterVO other = (C2CBatchMasterVO) obj;
        if (_userId != other._userId)
            return false;
        return true;
    }
}
