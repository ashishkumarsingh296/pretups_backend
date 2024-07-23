/**
 * @(#)O2CBatchItemsVO.java
 *                          Copyright(c) 2011, Comviva Technologies Ltd.
 *                          All Rights Reserved
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Chhaya 01-NOV-2011 Initial Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 * 
 *                          This class is used for level 1 , level 2 and level 3
 *                          approval of initiated O2C order by batch.
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class O2CBatchItemsVO implements Serializable {

    private String _status = null;
    private Date _transferDate = null;
    private int _recordNumber = 0;
    private String _batchId = null;
    private String _batchDetailId = null;
    private String _msisdn = null;
    private String _loginId = null;
    private String _extTxnNo = null;
    private Date _extTxnDate = null;
    private long _requestedQuantity;
    private String _initiatorRemarks = null;
    private String _externalCode = null;
    private String _bonusType = null;

    private String _batchName = null;
    private String _productName = null;
    private long _productMrp;
    private String _productMrpStr;
    private int _batchTotalRecord;
    private int _newRecords = 0;
    private int _level1ApprovedRecords = 0;
    private int _level2ApprovedRecords = 0;
    private int _rejectedRecords = 0;
    private int _closedRecords = 0;
    private String _networkCode = null;
    private String _networkCodeFor = null;
    private String _productType = null;
    private String _createdBy = null;
    private Date _createdOn = null;
    private String _modifiedBy = null;
    private Date _modifiedOn = null;
    private String _productShortName = null;
    private String _domainCode = null;
    private Date _batchDate = null;
    private String _defaultLang = null;
    private String _secondLang = null;
    private String _productCode = null;
    private String _gradeName;

    private String _initiaterName;
    private Date _initiatedOn;
    private String _firstApproverName;
    private Date _firstApprovedOn;
    private String _secondApproverName;
    private Date _secondApprovedOn;
    private String _categoryName;
    private String _userId;
    private String _categoryCode;
    private String _commissionProfileSetId = null;

    private String _commissionType = null;
    private double _commissionRate;
    private long _commissionValue;
    private String _tax1Type = null;
    private double _tax1Rate;

    private String _tax2Type = null;
    private double _tax2Rate;

    private String _tax3Type = null;
    private double _tax3Rate;
    private long _tax3Value;
    private String _userGradeCode = null;

    private String _initiatedBy = null;
    private String _referenceNo = null;
    private String _gradeCode = null;
    private String _rcrdStatus = null;
    private Date _cancelledOn = null;
    private String _cancelledBy = null;
    private String _firstApproverRemarks = null;
    private String _secondApproverRemarks = null;
    private String _thirdApproverRemarks = null;
    private long _tax2Value;
    private long _tax1Value;
    private String _commissionProfileVer = null;
    private String _commissionProfileDetailId = null;
    private long _transferMrp;
    private String _firstApprovedBy = null;
    private String _secondApprovedBy = null;
    private String _thirdApprovedBy = null;
    private Date _thirdApprovedOn = null;

    private ChannelUserVO _channelUserVO = null;
    private ChannelTransferItemsVO _channelTransferItemsVO = null;
    private String _txnProfile = null;
    private String wallet_type = null;
    private String _autoc2callowed;
    private String sosAllowed = null;
    private long sosAllowedAmount = 0;
    private long sosThresholdLimit = 0;
    private String dualCommissionType; 

	public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}

	public long getLrMaxAmount() {
		return lrMaxAmount;
	}

	public void setLrMaxAmount(long lrMaxAmount) {
		this.lrMaxAmount = lrMaxAmount;
	}

	private String lrAllowed=null;
    public String getLrAllowed() {
		return lrAllowed;
	}

	public void setLrAllowed(String lrAllowed) {
		this.lrAllowed = lrAllowed;
	}

	private long lrMaxAmount = 0;

    // user life cycle
    private String _userStatus = null;

    /**
     * @return the _recordNumber
     */
    public int getRecordNumber() {
        return _recordNumber;
    }

    /**
     * @param number
     *            the _recordNumber to set
     */
    public void setRecordNumber(int number) {
        _recordNumber = number;
    }

    /**
     * @return the _batchId
     */
    public String getBatchId() {
        return _batchId;
    }

    /**
     * @param id
     *            the _batchId to set
     */
    public void setBatchId(String id) {
        _batchId = id;
    }

    /**
     * @return the _msisdn
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param _msisdn
     *            the _msisdn to set
     */
    public void setMsisdn(String _msisdn) {
        this._msisdn = _msisdn;
    }

    /**
     * @return the _extTxnNo
     */
    public String getExtTxnNo() {
        return _extTxnNo;
    }

    /**
     * @param txnNo
     *            the _extTxnNo to set
     */
    public void setExtTxnNo(String txnNo) {
        _extTxnNo = txnNo;
    }

    /**
     * @return the _extTxnDate
     */
    public Date getExtTxnDate() {
        return _extTxnDate;
    }

    /**
     * @param txnDate
     *            the _extTxnDate to set
     */
    public void setExtTxnDate(Date txnDate) {
        _extTxnDate = txnDate;
    }

    /**
     * @return the _requestedQuantity
     */
    public long getRequestedQuantity() {
        return _requestedQuantity;
    }

    /**
     * @param quantity
     *            the _requestedQuantity to set
     */
    public void setRequestedQuantity(long quantity) {
        _requestedQuantity = quantity;
    }

    /**
     * @return the _initiatorRemarks
     */
    public String getInitiatorRemarks() {
        return _initiatorRemarks;
    }

    /**
     * @param remarks
     *            the _initiatorRemarks to set
     */
    public void setInitiatorRemarks(String remarks) {
        _initiatorRemarks = remarks;
    }

    /**
     * @return the _externalCode
     */
    public String getExternalCode() {
        return _externalCode;
    }

    /**
     * @param code
     *            the _externalCode to set
     */
    public void setExternalCode(String code) {
        _externalCode = code;
    }

    /**
     * @return the _loginId
     */
    public String getLoginId() {
        return _loginId;
    }

    /**
     * @param id
     *            the _loginId to set
     */
    public void setLoginId(String id) {
        _loginId = id;
    }

    /**
     * @return the _channelUserVO
     */
    public ChannelUserVO getChannelUserVO() {
        return _channelUserVO;
    }

    /**
     * @param userVO
     *            the _channelUserVO to set
     */
    public void setChannelUserVO(ChannelUserVO userVO) {
        _channelUserVO = userVO;
    }

    /**
     * @return the _batchName
     */
    public String getBatchName() {
        return _batchName;
    }

    /**
     * @param name
     *            the _batchName to set
     */
    public void setBatchName(String name) {
        _batchName = name;
    }

    /**
     * @return the _productName
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param name
     *            the _productName to set
     */
    public void setProductName(String name) {
        _productName = name;
    }

    /**
     * @return the _productMrp
     */
    public long getProductMrp() {
        return _productMrp;
    }

    /**
     * @param mrp
     *            the _productMrp to set
     */
    public void setProductMrp(long mrp) {
        _productMrp = mrp;
    }

    /**
     * @return the _productMrpStr
     */
    public String getProductMrpStr() {
        return _productMrpStr;
    }

    /**
     * @param mrpStr
     *            the _productMrpStr to set
     */
    public void setProductMrpStr(String mrpStr) {
        _productMrpStr = mrpStr;
    }

    /**
     * @return the _batchTotalRecord
     */
    public int getBatchTotalRecord() {
        return _batchTotalRecord;
    }

    /**
     * @param totalRecord
     *            the _batchTotalRecord to set
     */
    public void setBatchTotalRecord(int totalRecord) {
        _batchTotalRecord = totalRecord;
    }

    /**
     * @return the _newRecords
     */
    public int getNewRecords() {
        return _newRecords;
    }

    /**
     * @param records
     *            the _newRecords to set
     */
    public void setNewRecords(int records) {
        _newRecords = records;
    }

    /**
     * @return the _level1ApprovedRecords
     */
    public int getLevel1ApprovedRecords() {
        return _level1ApprovedRecords;
    }

    /**
     * @param approvedRecords
     *            the _level1ApprovedRecords to set
     */
    public void setLevel1ApprovedRecords(int approvedRecords) {
        _level1ApprovedRecords = approvedRecords;
    }

    /**
     * @return the _level2ApprovedRecords
     */
    public int getLevel2ApprovedRecords() {
        return _level2ApprovedRecords;
    }

    /**
     * @param approvedRecords
     *            the _level2ApprovedRecords to set
     */
    public void setLevel2ApprovedRecords(int approvedRecords) {
        _level2ApprovedRecords = approvedRecords;
    }

    /**
     * @return the _rejectedRecords
     */
    public int getRejectedRecords() {
        return _rejectedRecords;
    }

    /**
     * @param records
     *            the _rejectedRecords to set
     */
    public void setRejectedRecords(int records) {
        _rejectedRecords = records;
    }

    /**
     * @return the _closedRecords
     */
    public int getClosedRecords() {
        return _closedRecords;
    }

    /**
     * @param records
     *            the _closedRecords to set
     */
    public void setClosedRecords(int records) {
        _closedRecords = records;
    }

    /**
     * @return the _networkCode
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param code
     *            the _networkCode to set
     */
    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    /**
     * @return the _networkCodeFor
     */
    public String getNetworkCodeFor() {
        return _networkCodeFor;
    }

    /**
     * @param codeFor
     *            the _networkCodeFor to set
     */
    public void setNetworkCodeFor(String codeFor) {
        _networkCodeFor = codeFor;
    }

    /**
     * @return the _productType
     */
    public String getProductType() {
        return _productType;
    }

    /**
     * @param type
     *            the _productType to set
     */
    public void setProductType(String type) {
        _productType = type;
    }

    /**
     * @return the _createdBy
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param by
     *            the _createdBy to set
     */
    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    /**
     * @return the _createdOn
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param on
     *            the _createdOn to set
     */
    public void setCreatedOn(Date on) {
        _createdOn = on;
    }

    /**
     * @return the _modifiedBy
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param by
     *            the _modifiedBy to set
     */
    public void setModifiedBy(String by) {
        _modifiedBy = by;
    }

    /**
     * @return the _modifiedOn
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param on
     *            the _modifiedOn to set
     */
    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    /**
     * @return the _productShortName
     */
    public String getProductShortName() {
        return _productShortName;
    }

    /**
     * @param shortName
     *            the _productShortName to set
     */
    public void setProductShortName(String shortName) {
        _productShortName = shortName;
    }

    /**
     * @return the _domainCode
     */
    public String getDomainCode() {
        return _domainCode;
    }

    /**
     * @param code
     *            the _domainCode to set
     */
    public void setDomainCode(String code) {
        _domainCode = code;
    }

    /**
     * @return the _batchDate
     */
    public Date getBatchDate() {
        return _batchDate;
    }

    /**
     * @param date
     *            the _batchDate to set
     */
    public void setBatchDate(Date date) {
        _batchDate = date;
    }

    /**
     * @return the _defaultLang
     */
    public String getDefaultLang() {
        return _defaultLang;
    }

    /**
     * @param lang
     *            the _defaultLang to set
     */
    public void setDefaultLang(String lang) {
        _defaultLang = lang;
    }

    /**
     * @return the _secondLang
     */
    public String getSecondLang() {
        return _secondLang;
    }

    /**
     * @param lang
     *            the _secondLang to set
     */
    public void setSecondLang(String lang) {
        _secondLang = lang;
    }

    /**
     * @return the _productCode
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param code
     *            the _productCode to set
     */
    public void setProductCode(String code) {
        _productCode = code;
    }

    public void setBatchDetailId(String batchDetailId) {
        // TODO Auto-generated method stub
        _batchDetailId = batchDetailId;

    }

    public String getBatchDetailId() {
        // TODO Auto-generated method stub
        return _batchDetailId;
    }

    public void setStatus(String status) {
        // TODO Auto-generated method stub
        _status = status;

    }

    public String getStatus() {
        // TODO Auto-generated method stub
        return _status;

    }

    /**
     * @return Returns the transferDate.
     */
    public Date getTransferDate() {
        return _transferDate;
    }

    /**
     * @param transferDate
     *            The transferDate to set.
     */
    public void setTransferDate(Date transferDate) {
        _transferDate = transferDate;
    }

    public String getCategoryName() {
        // TODO Auto-generated method stub
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        // TODO Auto-generated method stub
        _categoryName = categoryName;
    }

    public String getGradeName() {
        // TODO Auto-generated method stub
        return _gradeName;
    }

    public void setGradeName(String gradeName) {
        // TODO Auto-generated method stub
        _gradeName = gradeName;
    }

    public String getInitiaterName() {
        // TODO Auto-generated method stub
        return _initiaterName;
    }

    public Date getInitiatedOn() {
        // TODO Auto-generated method stub
        return _initiatedOn;
    }

    public void setInitiatedOn(Timestamp initiatedOn) {
        // TODO Auto-generated method stub
        _initiatedOn = initiatedOn;
    }

    public String getFirstApproverName() {
        // TODO Auto-generated method stub
        return _firstApproverName;
    }

    public void setFirstApproverName(String firstApproverName) {
        // TODO Auto-generated method stub
        _firstApproverName = firstApproverName;
    }

    public Date getFirstApprovedOn() {
        // TODO Auto-generated method stub
        return _firstApprovedOn;
    }

    public void getFirstApprovedOn(Date firstApprovedOn) {
        // TODO Auto-generated method stub
        _firstApprovedOn = firstApprovedOn;
    }

    public String getSecondApproverName() {
        // TODO Auto-generated method stub
        return _secondApproverName;
    }

    public void setSecondApproverName(String secondApproverName) {
        // TODO Auto-generated method stub
        _secondApproverName = secondApproverName;
    }

    public Date getSecondApprovedOn() {
        // TODO Auto-generated method stub
        return _secondApprovedOn;
    }

    public void setSecondApprovedOn(Date secondApprovedOn) {
        // TODO Auto-generated method stub
        _secondApprovedOn = secondApprovedOn;
    }

    public void setUserId(String userId) {
        // TODO Auto-generated method stub
        _userId = userId;
    }

    public String getUserId() {
        // TODO Auto-generated method stub
        return _userId;
    }

    public String getFirstApproverRemarks() {
        // TODO Auto-generated method stub
        return _firstApproverRemarks;
    }

    public String getSecondApproverRemarks() {
        // TODO Auto-generated method stub
        return _secondApproverRemarks;
    }

    public String getFirstApprovedBy() {
        // TODO Auto-generated method stub
        return _firstApprovedBy;
    }

    public String getSecondApprovedBy() {
        // TODO Auto-generated method stub
        return _secondApprovedBy;
    }

    public String getCancelledBy() {
        // TODO Auto-generated method stub
        return _cancelledBy;
    }

    public Date getCancelledOn() {
        // TODO Auto-generated method stub
        return _cancelledOn;
    }

    public String getInitiatedBy() {
        // TODO Auto-generated method stub
        return _initiatedBy;
    }

    public long getTransferMrp() {
        // TODO Auto-generated method stub
        return _transferMrp;
    }

    public void setFirstApproverRemarks(String remark) {
        // TODO Auto-generated method stub
        _firstApproverRemarks = remark;

    }

    public void setSecondApproverRemarks(String remark) {
        // TODO Auto-generated method stub
        _secondApproverRemarks = remark;

    }

    public void setThirdApproverRemarks(String remark) {
        // TODO Auto-generated method stub
        _thirdApproverRemarks = remark;
    }

    public void setCategoryCode(String categoryCode) {
        // TODO Auto-generated method stub
        _categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        // TODO Auto-generated method stub
        return _categoryCode;
    }

    public String getTxnProfile() {
        // TODO Auto-generated method stub
        return _txnProfile;
    }

    public void setTxnProfile(String txnProfile) {
        // TODO Auto-generated method stub
        _txnProfile = txnProfile;
    }

    public void setFirstApprovedBy(String p_userid) {
        // TODO Auto-generated method stub

        _firstApprovedBy = p_userid;
    }

    public void setFirstApprovedOn(Timestamp timestampFromUtilDate) {
        // TODO Auto-generated method stub
        _firstApprovedOn = timestampFromUtilDate;
    }

    public void setSecondApprovedBy(String p_userid) {
        // TODO Auto-generated method stub
        _secondApprovedBy = p_userid;
    }

    public void setThirdApprovedOn(Timestamp timestampFromUtilDate) {
        // TODO Auto-generated method stub
        _thirdApprovedOn = timestampFromUtilDate;
    }

    public void setThirdApprovedBy(String p_userid) {
        // TODO Auto-generated method stub
        _thirdApprovedBy = p_userid;
    }

    public String getThirdApproverRemarks() {
        // TODO Auto-generated method stub
        return _thirdApproverRemarks;
    }

    public String getCommissionProfileSetId() {
        // TODO Auto-generated method stub
        return _commissionProfileSetId;
    }

    public String getCommissionProfileVer() {
        // TODO Auto-generated method stub
        return _commissionProfileVer;
    }

    public String getGradeCode() {
        // TODO Auto-generated method stub
        return _gradeCode;
    }

    public Date getThirdApprovedOn() {
        // TODO Auto-generated method stub
        return _thirdApprovedOn;
    }

    public String getThirdApprovedBy() {
        // TODO Auto-generated method stub
        return _thirdApprovedBy;
    }

    public long getTax1Value() {
        // TODO Auto-generated method stub
        return _tax1Value;
    }

    public long getTax2Value() {
        // TODO Auto-generated method stub
        return _tax2Value;
    }

    public String getCommissionProfileDetailId() {
        // TODO Auto-generated method stub
        return _commissionProfileSetId;
    }

    public String getCommissionType() {
        // TODO Auto-generated method stub
        return _commissionType;
    }

    public double getCommissionRate() {
        // TODO Auto-generated method stub
        return _commissionRate;
    }

    public long getCommissionValue() {
        // TODO Auto-generated method stub
        return _commissionValue;
    }

    public double getTax1Rate() {
        // TODO Auto-generated method stub
        return _tax1Rate;
    }

    public String getTax1Type() {
        // TODO Auto-generated method stub
        return _tax1Type;
    }

    public double getTax2Rate() {
        // TODO Auto-generated method stub
        return _tax2Rate;
    }

    public String getTax2Type() {
        // TODO Auto-generated method stub
        return _tax2Type;
    }

    public double getTax3Rate() {
        // TODO Auto-generated method stub
        return _tax3Rate;
    }

    public long getTax3Value() {
        // TODO Auto-generated method stub
        return _tax3Value;
    }

    public String getTax3Type() {
        // TODO Auto-generated method stub
        return _tax3Type;
    }

    public String getUserGradeCode() {
        // TODO Auto-generated method stub
        return _userGradeCode;
    }

    public String getBonusType() {
        // TODO Auto-generated method stub
        return _bonusType;
    }

    public void setTransferMrp(long long1) {
        // TODO Auto-generated method stub
        _transferMrp = long1;

    }

    public void setInitiatedBy(String string) {
        // TODO Auto-generated method stub
        _initiatedBy = string;

    }

    public void setReferenceNo(String string) {
        // TODO Auto-generated method stub
        _referenceNo = string;

    }

    /**
     * @return the _channelTransferItemsVO
     */
    public ChannelTransferItemsVO getChannelTransferItemsVO() {
        return _channelTransferItemsVO;
    }

    /**
     * @param transferItemsVO
     *            the _channelTransferItemsVO to set
     */
    public void setChannelTransferItemsVO(ChannelTransferItemsVO transferItemsVO) {
        _channelTransferItemsVO = transferItemsVO;
    }

    /**
     * @param initiaterName
     *            the initiaterName to set
     */
    public void setInitiaterName(String initiaterName) {
        _initiaterName = initiaterName;
    }

    /**
     * @param initiatedOn
     *            the initiatedOn to set
     */
    public void setInitiatedOn(Date initiatedOn) {
        _initiatedOn = initiatedOn;
    }

    /**
     * @param firstApprovedOn
     *            the firstApprovedOn to set
     */
    public void setFirstApprovedOn(Date firstApprovedOn) {
        _firstApprovedOn = firstApprovedOn;
    }

    /**
     * @param commissionProfileSetId
     *            the commissionProfileSetId to set
     */
    public void setCommissionProfileSetId(String commissionProfileSetId) {
        _commissionProfileSetId = commissionProfileSetId;
    }

    /**
     * @param commissionProfileVer
     *            the commissionProfileVer to set
     */
    public void setCommissionProfileVer(String commissionProfileVer) {
        _commissionProfileVer = commissionProfileVer;
    }

    /**
     * @param commissionProfileDetailId
     *            the commissionProfileDetailId to set
     */
    public void setCommissionProfileDetailId(String commissionProfileDetailId) {
        _commissionProfileDetailId = commissionProfileDetailId;
    }

    /**
     * @param commissionType
     *            the commissionType to set
     */
    public void setCommissionType(String commissionType) {
        _commissionType = commissionType;
    }

    /**
     * @param commissionRate
     *            the commissionRate to set
     */
    public void setCommissionRate(double commissionRate) {
        _commissionRate = commissionRate;
    }

    /**
     * @param commissionValue
     *            the commissionValue to set
     */
    public void setCommissionValue(long commissionValue) {
        _commissionValue = commissionValue;
    }

    /**
     * @param tax1Type
     *            the tax1Type to set
     */
    public void setTax1Type(String tax1Type) {
        _tax1Type = tax1Type;
    }

    /**
     * @param tax1Rate
     *            the tax1Rate to set
     */
    public void setTax1Rate(double tax1Rate) {
        _tax1Rate = tax1Rate;
    }

    /**
     * @param tax1Value
     *            the tax1Value to set
     */
    public void setTax1Value(long tax1Value) {
        _tax1Value = tax1Value;
    }

    /**
     * @param tax2Type
     *            the tax2Type to set
     */
    public void setTax2Type(String tax2Type) {
        _tax2Type = tax2Type;
    }

    /**
     * @param tax2Rate
     *            the tax2Rate to set
     */
    public void setTax2Rate(double tax2Rate) {
        _tax2Rate = tax2Rate;
    }

    /**
     * @param tax2Value
     *            the tax2Value to set
     */
    public void setTax2Value(long tax2Value) {
        _tax2Value = tax2Value;
    }

    /**
     * @param tax3Type
     *            the tax3Type to set
     */
    public void setTax3Type(String tax3Type) {
        _tax3Type = tax3Type;
    }

    /**
     * @param tax3Rate
     *            the tax3Rate to set
     */
    public void setTax3Rate(double tax3Rate) {
        _tax3Rate = tax3Rate;
    }

    /**
     * @param tax3Value
     *            the tax3Value to set
     */
    public void setTax3Value(long tax3Value) {
        _tax3Value = tax3Value;
    }

    /**
     * @param cancelledBy
     *            The cancelledBy to set.
     */
    public void setCancelledBy(String cancelledBy) {
        _cancelledBy = cancelledBy;
    }

    /**
     * @param cancelledOn
     *            The cancelledOn to set.
     */
    public void setCancelledOn(Date cancelledOn) {
        _cancelledOn = cancelledOn;
    }

    /**
     * @param rcrdStatus
     *            The rcrdStatus to set.
     */
    public void setRcrdStatus(String rcrdStatus) {
        _rcrdStatus = rcrdStatus;
    }

    /**
     * @param gradeCode
     *            The gradeCode to set.
     */
    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }

    public String getWallet_type() {
        return wallet_type;
    }

    public void setWallet_type(String wallet_type) {
        this.wallet_type = wallet_type;
    }

    public String getAutoc2callowed() {
        return _autoc2callowed;
    }

    public void setAutoc2callowed(String autoc2callowed) {
        _autoc2callowed = autoc2callowed;
    }

    public String getUserStatus() {
        return _userStatus;
    }

    public void setUserStatus(String status) {
        _userStatus = status;
    }

	public String getSosAllowed() {
		return sosAllowed;
	}

	public void setSosAllowed(String sosAllowed) {
		this.sosAllowed = sosAllowed;
	}

	public long getSosAllowedAmount() {
		return sosAllowedAmount;
	}

	public void setSosAllowedAmount(long sosAllowedAmount) {
		this.sosAllowedAmount = sosAllowedAmount;
	}

	public long getSosThresholdLimit() {
		return sosThresholdLimit;
	}

	public void setSosThresholdLimit(long sosThresholdLimit) {
		this.sosThresholdLimit = sosThresholdLimit;
	}
    
}
