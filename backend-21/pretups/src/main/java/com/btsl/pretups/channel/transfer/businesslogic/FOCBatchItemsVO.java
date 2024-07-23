/*
 * @# FOCBatchItemsVO.java
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
import java.util.ArrayList;
import java.util.Date;

public class FOCBatchItemsVO implements Serializable {
    private String _batchId = null;
    private String _batchDetailId = null;
    private String _categoryCode = null;
    private String _msisdn = null;
    private String _userId = null;
    private String _userName = null;
    private String _status = null;
    private String _modifiedBy = null;
    private Date _modifiedOn = null;
    private String _userGradeCode = null;
    private String _referenceNo = null;
    private String _extTxnNo = null;
    private Date _extTxnDate = null;
    private String _extTxnDateStr = null;
    private Date _transferDate = null;
    private String _transferDateStr = null;
    private String _txnProfile = null;
    private String _commissionProfileSetId = null;
    private String _commissionProfileVer = null;
    private String _commissionProfileDetailId = null;
    private String _commissionType = null;
    private double _commissionRate;
    private long _commissionValue;
    private String _tax1Type = null;
    private double _tax1Rate;
    private long _tax1Value;
    private String _tax2Type = null;
    private double _tax2Rate;
    private long _tax2Value;
    private String _tax3Type = null;
    private double _tax3Rate;
    private long _tax3Value;
    private long _requestedQuantity;
    private long _transferMrp;
    private String _initiatorRemarks = null;
    private String _firstApproverRemarks = null;
    private String _secondApproverRemarks = null;
    private String _thirdApproverRemarks = null;
    private String _firstApprovedBy = null;
    private Date _firstApprovedOn = null;
    private String _secondApprovedBy = null;
    private Date _secondApprovedOn = null;
    private String _thirdApprovedBy = null;
    private Date _thirdApprovedOn = null;
    private String _cancelledBy = null;
    private Date _cancelledOn = null;
    private String _rcrdStatus = null;
    private String _categoryName = null;
    private String _gradeName = null;
    private String _loginID = null;
    private String _initiatedBy = null;
    private Date _initiatedOn = null;
    private int _recordNumber = 0;
    private String _externalCode = null;
    private String _gradeCode = null;
    private String _initiaterName = null;
    private String _firstApproverName = null;
    private String _secondApproverName = null;
    // lohit for direct payoyut
    private String _bonusType = null;
    private ArrayList _bonusTypeList;
    private long _requiredQuantity = 0L;
    private String _ownerName = null;
    private String _ownerMSISDN = null;
    private String error=null;
    private String preBalance=null;
    private String postBalance=null;
    private String commWalletType=null;
    
    /** START: Birendra: 29JAN2015 */
    private String walletCode = null;
    private String _newStatus = null;
    
    public String getNewStatus() {
		return _newStatus;
	}

	public void setNewStatus(String newStatus) {
		this._newStatus = newStatus;
	}
    public String getWalletCode() {
        return walletCode;
    }

    public void setWalletCode(String walletCode) {
        this.walletCode = walletCode;
    }

    /** STOP: Birendra: 29JAN2015 */
	private String _pointAction = null;
	private String extTXNNumber;     
    private String extTXNDate;     
    private String extCode;     
    private String dualCommissionType;
	 
	 public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append("_batchId =" + _batchId);
        sbf.append(",_batchDetailId =" + _batchDetailId);
        sbf.append(",_categoryCode =" + _categoryCode);
        sbf.append(",_msisdn =" + _msisdn);
        sbf.append(",_userId =" + _userId);
        sbf.append(",_status =" + _status);
        sbf.append(",_modifiedBy =" + _modifiedBy);
        sbf.append(",_modifiedOn =" + _modifiedOn);
        sbf.append(",_userGradeCode =" + _userGradeCode);
        sbf.append(",_referenceNo =" + _referenceNo);
        sbf.append(",_extTxnNo =" + _extTxnNo);
        sbf.append(",_extTxnDate =" + _extTxnDate);
        sbf.append(",_transferDate =" + _transferDate);
        sbf.append(",_txnProfile =" + _txnProfile);
        sbf.append(",_commissionProfileSetId =" + _commissionProfileSetId);
        sbf.append(",_commissionProfileVer =" + _commissionProfileVer);
        sbf.append(",_commissionProfileDetailId =" + _commissionProfileDetailId);
        sbf.append(",_commissionType =" + _commissionType);
        sbf.append(",_commissionRate =" + _commissionRate);
        sbf.append(",_commissionValue =" + _commissionValue);
        sbf.append(",_tax1Type =" + _tax1Type);
        sbf.append(",_tax1Rate =" + _tax1Rate);
        sbf.append(",_tax1Value =" + _tax1Value);
        sbf.append(",_tax2Type =" + _tax2Type);
        sbf.append(",_tax2Rate =" + _tax2Rate);
        sbf.append(",_tax2Value =" + _tax2Value);
        sbf.append(",_tax3Type =" + _tax3Type);
        sbf.append(",_tax3Rate =" + _tax3Rate);
        sbf.append(",_tax3Value =" + _tax3Value);
        sbf.append(",_requestedQuantity =" + _requestedQuantity);
        sbf.append(",_transferMrp =" + _transferMrp);
        sbf.append(",_initiatorRemarks =" + _initiatorRemarks);
        sbf.append(",_firstApproverRemarks =" + _firstApproverRemarks);
        sbf.append(",_secondApproverRemarks =" + _secondApproverRemarks);
        sbf.append(",_thirdApproverRemarks =" + _thirdApproverRemarks);
        sbf.append(",_firstApprovedBy =" + _firstApprovedBy);
        sbf.append(",_firstApprovedOn =" + _firstApprovedOn);
        sbf.append(",_secondApprovedBy =" + _secondApprovedBy);
        sbf.append(",_secondApprovedOn =" + _secondApprovedOn);
        sbf.append(",_thirdApprovedBy =" + _thirdApprovedBy);
        sbf.append(",_thirdApprovedOn =" + _thirdApprovedOn);
        sbf.append(",_cancelledBy =" + _cancelledBy);
        sbf.append(",_cancelledOn =" + _cancelledOn);
        sbf.append(",_rcrdStatus =" + _rcrdStatus);
        sbf.append(",_categoryName =" + _categoryName);
        sbf.append(",_gradeName =" + _gradeName);
        sbf.append(",_loginID =" + _loginID);
        sbf.append(",_externalCode =" + _externalCode);
        sbf.append(",_gradeCode =" + _gradeCode);
        sbf.append(",_initiaterName =" + _initiaterName);
        sbf.append(",_firstApproverName =" + _firstApproverName);
        sbf.append(",_secondApproverName =" + _secondApproverName);
        sbf.append(",_bonusType =" + _bonusType);
        sbf.append(",_requiredQuantity =" + _requiredQuantity);
        sbf.append(",_ownerName=" + _ownerName);
        sbf.append(",_ownerMSISDN=" + _ownerMSISDN);
        return sbf.toString();
    }

    /**
     * @return Returns the fistApproverName.
     */
    public String getFirstApproverName() {
        return _firstApproverName;
    }

    /**
     * @param fistApproverName
     *            The fistApproverName to set.
     */
    public void setFirstApproverName(String firstApproverName) {
        _firstApproverName = firstApproverName;
    }

    /**
     * @return Returns the initiaterName.
     */
    public String getInitiaterName() {
        return _initiaterName;
    }

    /**
     * @param initiaterName
     *            The initiaterName to set.
     */
    public void setInitiaterName(String initiaterName) {
        _initiaterName = initiaterName;
    }

    /**
     * @return Returns the secondApproverName.
     */
    public String getSecondApproverName() {
        return _secondApproverName;
    }

    /**
     * @param secondApproverName
     *            The secondApproverName to set.
     */
    public void setSecondApproverName(String secondApproverName) {
        _secondApproverName = secondApproverName;
    }

    /**
     * @return Returns the gradeCode.
     */
    public String getGradeCode() {
        return _gradeCode;
    }

    /**
     * @param gradeCode
     *            The gradeCode to set.
     */
    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }

    /**
     * @return Returns the externalCode.
     */
    public String getExternalCode() {
        return _externalCode;
    }

    /**
     * @param externalCode
     *            The externalCode to set.
     */
    public void setExternalCode(String externalCode) {
        _externalCode = externalCode;
    }

    /**
     * @return Returns the extTxnDateStr.
     */
    public String getExtTxnDateStr() {
        return _extTxnDateStr;
    }

    /**
     * @param extTxnDateStr
     *            The extTxnDateStr to set.
     */
    public void setExtTxnDateStr(String extTxnDateStr) {
        _extTxnDateStr = extTxnDateStr;
    }

    /**
     * @return Returns the userName.
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * @param userName
     *            The userName to set.
     */
    public void setUserName(String userName) {
        _userName = userName;
    }

    /**
     * @return Returns the transferDateStr.
     */
    public String getTransferDateStr() {
        return _transferDateStr;
    }

    /**
     * @param transferDateStr
     *            The transferDateStr to set.
     */
    public void setTransferDateStr(String transferDateStr) {
        _transferDateStr = transferDateStr;
    }

    /**
     * @return Returns the batchDetailId.
     */
    public String getBatchDetailId() {
        return _batchDetailId;
    }

    /**
     * @param batchDetailId
     *            The batchDetailId to set.
     */
    public void setBatchDetailId(String batchDetailId) {
        _batchDetailId = batchDetailId;
    }

    /**
     * @return Returns the cancelledBy.
     */
    public String getCancelledBy() {
        return _cancelledBy;
    }

    /**
     * @param cancelledBy
     *            The cancelledBy to set.
     */
    public void setCancelledBy(String cancelledBy) {
        _cancelledBy = cancelledBy;
    }

    /**
     * @return Returns the cancelledOn.
     */
    public Date getCancelledOn() {
        return _cancelledOn;
    }

    /**
     * @param cancelledOn
     *            The cancelledOn to set.
     */
    public void setCancelledOn(Date cancelledOn) {
        _cancelledOn = cancelledOn;
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return Returns the commissionProfileDetailId.
     */
    public String getCommissionProfileDetailId() {
        return _commissionProfileDetailId;
    }

    /**
     * @param commissionProfileDetailId
     *            The commissionProfileDetailId to set.
     */
    public void setCommissionProfileDetailId(String commissionProfileDetailId) {
        _commissionProfileDetailId = commissionProfileDetailId;
    }

    /**
     * @return Returns the commissionProfileSetId.
     */
    public String getCommissionProfileSetId() {
        return _commissionProfileSetId;
    }

    /**
     * @param commissionProfileSetId
     *            The commissionProfileSetId to set.
     */
    public void setCommissionProfileSetId(String commissionProfileSetId) {
        _commissionProfileSetId = commissionProfileSetId;
    }

    /**
     * @return Returns the commissionProfileVer.
     */
    public String getCommissionProfileVer() {
        return _commissionProfileVer;
    }

    /**
     * @param commissionProfileVer
     *            The commissionProfileVer to set.
     */
    public void setCommissionProfileVer(String commissionProfileVer) {
        _commissionProfileVer = commissionProfileVer;
    }

    /**
     * @return Returns the commissionRate.
     */
    public double getCommissionRate() {
        return _commissionRate;
    }

    /**
     * @param commissionRate
     *            The commissionRate to set.
     */
    public void setCommissionRate(double commissionRate) {
        _commissionRate = commissionRate;
    }

    /**
     * @return Returns the commissionType.
     */
    public String getCommissionType() {
        return _commissionType;
    }

    /**
     * @param commissionType
     *            The commissionType to set.
     */
    public void setCommissionType(String commissionType) {
        _commissionType = commissionType;
    }

    /**
     * @return Returns the commissionValue.
     */
    public long getCommissionValue() {
        return _commissionValue;
    }

    /**
     * @param commissionValue
     *            The commissionValue to set.
     */
    public void setCommissionValue(long commissionValue) {
        _commissionValue = commissionValue;
    }

    /**
     * @return Returns the extTxnDate.
     */
    public Date getExtTxnDate() {
        return _extTxnDate;
    }

    /**
     * @param extTxnDate
     *            The extTxnDate to set.
     */
    public void setExtTxnDate(Date extTxnDate) {
        _extTxnDate = extTxnDate;
    }

    /**
     * @return Returns the extTxnNo.
     */
    public String getExtTxnNo() {
        return _extTxnNo;
    }

    /**
     * @param extTxnNo
     *            The extTxnNo to set.
     */
    public void setExtTxnNo(String extTxnNo) {
        _extTxnNo = extTxnNo;
    }

    /**
     * @return Returns the firstApprovedBy.
     */
    public String getFirstApprovedBy() {
        return _firstApprovedBy;
    }

    /**
     * @param firstApprovedBy
     *            The firstApprovedBy to set.
     */
    public void setFirstApprovedBy(String firstApprovedBy) {
        _firstApprovedBy = firstApprovedBy;
    }

    /**
     * @return Returns the firstApprovedOn.
     */
    public Date getFirstApprovedOn() {
        return _firstApprovedOn;
    }

    /**
     * @param firstApprovedOn
     *            The firstApprovedOn to set.
     */
    public void setFirstApprovedOn(Date firstApprovedOn) {
        _firstApprovedOn = firstApprovedOn;
    }

    /**
     * @return Returns the firstApproverRemarks.
     */
    public String getFirstApproverRemarks() {
        return _firstApproverRemarks;
    }

    /**
     * @param firstApproverRemarks
     *            The firstApproverRemarks to set.
     */
    public void setFirstApproverRemarks(String firstApproverRemarks) {
        _firstApproverRemarks = firstApproverRemarks;
    }

    /**
     * @return Returns the initiatorRemarks.
     */
    public String getInitiatorRemarks() {
        return _initiatorRemarks;
    }

    /**
     * @param initiatorRemarks
     *            The initiatorRemarks to set.
     */
    public void setInitiatorRemarks(String initiatorRemarks) {
        _initiatorRemarks = initiatorRemarks;
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
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the rcrdStatus.
     */
    public String getRcrdStatus() {
        return _rcrdStatus;
    }

    /**
     * @param rcrdStatus
     *            The rcrdStatus to set.
     */
    public void setRcrdStatus(String rcrdStatus) {
        _rcrdStatus = rcrdStatus;
    }

    /**
     * @return Returns the referenceNo.
     */
    public String getReferenceNo() {
        return _referenceNo;
    }

    /**
     * @param referenceNo
     *            The referenceNo to set.
     */
    public void setReferenceNo(String referenceNo) {
        _referenceNo = referenceNo;
    }

    /**
     * @return Returns the requestedQuantity.
     */
    public long getRequestedQuantity() {
        return _requestedQuantity;
    }

    /**
     * @param requestedQuantity
     *            The requestedQuantity to set.
     */
    public void setRequestedQuantity(long requestedQuantity) {
        _requestedQuantity = requestedQuantity;
    }

    /**
     * @return Returns the secondApprovedBy.
     */
    public String getSecondApprovedBy() {
        return _secondApprovedBy;
    }

    /**
     * @param secondApprovedBy
     *            The secondApprovedBy to set.
     */
    public void setSecondApprovedBy(String secondApprovedBy) {
        _secondApprovedBy = secondApprovedBy;
    }

    /**
     * @return Returns the secondApprovedOn.
     */
    public Date getSecondApprovedOn() {
        return _secondApprovedOn;
    }

    /**
     * @param secondApprovedOn
     *            The secondApprovedOn to set.
     */
    public void setSecondApprovedOn(Date secondApprovedOn) {
        _secondApprovedOn = secondApprovedOn;
    }

    /**
     * @return Returns the secondApproverRemarks.
     */
    public String getSecondApproverRemarks() {
        return _secondApproverRemarks;
    }

    /**
     * @param secondApproverRemarks
     *            The secondApproverRemarks to set.
     */
    public void setSecondApproverRemarks(String secondApproverRemarks) {
        _secondApproverRemarks = secondApproverRemarks;
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
     * @return Returns the tax1Rate.
     */
    public double getTax1Rate() {
        return _tax1Rate;
    }

    /**
     * @param tax1Rate
     *            The tax1Rate to set.
     */
    public void setTax1Rate(double tax1Rate) {
        _tax1Rate = tax1Rate;
    }

    /**
     * @return Returns the tax1Type.
     */
    public String getTax1Type() {
        return _tax1Type;
    }

    /**
     * @param tax1Type
     *            The tax1Type to set.
     */
    public void setTax1Type(String tax1Type) {
        _tax1Type = tax1Type;
    }

    /**
     * @return Returns the tax1Value.
     */
    public long getTax1Value() {
        return _tax1Value;
    }

    /**
     * @param tax1Value
     *            The tax1Value to set.
     */
    public void setTax1Value(long tax1Value) {
        _tax1Value = tax1Value;
    }

    /**
     * @return Returns the tax2Rate.
     */
    public double getTax2Rate() {
        return _tax2Rate;
    }

    /**
     * @param tax2Rate
     *            The tax2Rate to set.
     */
    public void setTax2Rate(double tax2Rate) {
        _tax2Rate = tax2Rate;
    }

    /**
     * @return Returns the tax2Type.
     */
    public String getTax2Type() {
        return _tax2Type;
    }

    /**
     * @param tax2Type
     *            The tax2Type to set.
     */
    public void setTax2Type(String tax2Type) {
        _tax2Type = tax2Type;
    }

    /**
     * @return Returns the tax2Value.
     */
    public long getTax2Value() {
        return _tax2Value;
    }

    /**
     * @param tax2Value
     *            The tax2Value to set.
     */
    public void setTax2Value(long tax2Value) {
        _tax2Value = tax2Value;
    }

    /**
     * @return Returns the tax3Rate.
     */
    public double getTax3Rate() {
        return _tax3Rate;
    }

    /**
     * @param tax3Rate
     *            The tax3Rate to set.
     */
    public void setTax3Rate(double tax3Rate) {
        _tax3Rate = tax3Rate;
    }

    /**
     * @return Returns the tax3Type.
     */
    public String getTax3Type() {
        return _tax3Type;
    }

    /**
     * @param tax3Type
     *            The tax3Type to set.
     */
    public void setTax3Type(String tax3Type) {
        _tax3Type = tax3Type;
    }

    /**
     * @return Returns the tax3Value.
     */
    public long getTax3Value() {
        return _tax3Value;
    }

    /**
     * @param tax3Value
     *            The tax3Value to set.
     */
    public void setTax3Value(long tax3Value) {
        _tax3Value = tax3Value;
    }

    /**
     * @return Returns the thirdApprovedBy.
     */
    public String getThirdApprovedBy() {
        return _thirdApprovedBy;
    }

    /**
     * @param thirdApprovedBy
     *            The thirdApprovedBy to set.
     */
    public void setThirdApprovedBy(String thirdApprovedBy) {
        _thirdApprovedBy = thirdApprovedBy;
    }

    /**
     * @return Returns the thirdApprovedOn.
     */
    public Date getThirdApprovedOn() {
        return _thirdApprovedOn;
    }

    /**
     * @param thirdApprovedOn
     *            The thirdApprovedOn to set.
     */
    public void setThirdApprovedOn(Date thirdApprovedOn) {
        _thirdApprovedOn = thirdApprovedOn;
    }

    /**
     * @return Returns the thirdApproverRemarks.
     */
    public String getThirdApproverRemarks() {
        return _thirdApproverRemarks;
    }

    /**
     * @param thirdApproverRemarks
     *            The thirdApproverRemarks to set.
     */
    public void setThirdApproverRemarks(String thirdApproverRemarks) {
        _thirdApproverRemarks = thirdApproverRemarks;
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

    /**
     * @return Returns the transferMrp.
     */
    public long getTransferMrp() {
        return _transferMrp;
    }

    /**
     * @param transferMrp
     *            The transferMrp to set.
     */
    public void setTransferMrp(long transferMrp) {
        _transferMrp = transferMrp;
    }

    /**
     * @return Returns the txnProfile.
     */
    public String getTxnProfile() {
        return _txnProfile;
    }

    /**
     * @param txnProfile
     *            The txnProfile to set.
     */
    public void setTxnProfile(String txnProfile) {
        _txnProfile = txnProfile;
    }

    /**
     * @return Returns the userGradeCode.
     */
    public String getUserGradeCode() {
        return _userGradeCode;
    }

    /**
     * @param userGradeCode
     *            The userGradeCode to set.
     */
    public void setUserGradeCode(String userGradeCode) {
        _userGradeCode = userGradeCode;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        _userId = userId;
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
     * @return Returns the gradeName.
     */
    public String getGradeName() {
        return _gradeName;
    }

    /**
     * @param gradeName
     *            The gradeName to set.
     */
    public void setGradeName(String gradeName) {
        _gradeName = gradeName;
    }

    /**
     * @return Returns the loginID.
     */
    public String getLoginID() {
        return _loginID;
    }

    /**
     * @param loginID
     *            The loginID to set.
     */
    public void setLoginID(String loginID) {
        _loginID = loginID;
    }

    /**
     * @return Returns the initiatedBy.
     */
    public String getInitiatedBy() {
        return _initiatedBy;
    }

    /**
     * @param initiatedBy
     *            The initiatedBy to set.
     */
    public void setInitiatedBy(String initiatedBy) {
        _initiatedBy = initiatedBy;
    }

    /**
     * @return Returns the initiatedOn.
     */
    public Date getInitiatedOn() {
        return _initiatedOn;
    }

    /**
     * @param initiatedOn
     *            The initiatedOn to set.
     */
    public void setInitiatedOn(Date initiatedOn) {
        _initiatedOn = initiatedOn;
    }

    /**
     * @return Returns the recordNumber.
     */
    public int getRecordNumber() {
        return _recordNumber;
    }

    /**
     * @param recordNumber
     *            The recordNumber to set.
     */
    public void setRecordNumber(int recordNumber) {
        _recordNumber = recordNumber;
    }

    /**
     * @return Returns the bonusType.
     */
    public String getBonusType() {
        return _bonusType;
    }

    /**
     * @param bonusType
     *            The bonusType to set.
     */
    public void setBonusType(String bonusType) {
        _bonusType = bonusType;
    }

    /**
     * @return Returns the bonusTypeList.
     */
    public ArrayList getBonusTypeList() {
        return _bonusTypeList;
    }

    /**
     * @param set
     *            bonusTypeList
     */
    public void setBonusTypeList(ArrayList typeList) {
        _bonusTypeList = typeList;
    }

    public String getOwnerName() {
        return _ownerName;

    }

    public void setOwnerName(String ownername) {
        _ownerName = ownername;
    }

    public String getOwnerMSISDN() {
        return _ownerMSISDN;

    }

    public void setOwnerMSISDN(String ownermsisdn) {
        _ownerMSISDN = ownermsisdn;
    }
    public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getPreBalance() {
		return preBalance;
	}

	public void setPreBalance(String balance) {
		preBalance = balance;
	}

	public String getPostBalance() {
		return postBalance;
	}

	public void setPostBalance(String balance) {
		postBalance = balance;
	}
	
	/**
	 * @return the pointAction
	 */
	public String getPointAction() {
		return _pointAction;
	}

	/**
	 * @param pointAction the pointAction to set
	 */
	public void setPointAction(String pointAction) {
		_pointAction = pointAction;
	}

	/**
	 * @return the extTXNNumber
	 */
	public String getExtTXNNumber() {
		return extTXNNumber;
	}

	/**
	 * @param extTXNNumber the extTXNNumber to set
	 */
	public void setExtTXNNumber(String extTXNNumber) {
		this.extTXNNumber = extTXNNumber;
	}

	/**
	 * @return the extTXNDate
	 */
	public String getExtTXNDate() {
		return extTXNDate;
	}

	/**
	 * @param extTXNDate the extTXNDate to set
	 */
	public void setExtTXNDate(String extTXNDate) {
		this.extTXNDate = extTXNDate;
	}

	/**
	 * @return the extCode
	 */
	public String getExtCode() {
		return extCode;
	}

	/**
	 * @param extCode the extCode to set
	 */
	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}
	
	public static FOCBatchItemsVO getInstance(){
		return new FOCBatchItemsVO();
	}

	public String getCommWalletType() {
		return commWalletType;
	}

	public void setCommWalletType(String commWalletType) {
		this.commWalletType = commWalletType;
	}
}
