package com.btsl.pretups.restrictedsubs.businesslogic;

/*
 * @(#)RestrictedSubscriberVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh 03/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */
import java.util.Date;

import com.btsl.pretups.subscriber.businesslogic.SubscriberVO;

public class RestrictedSubscriberVO extends SubscriberVO {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    // Instanse variables
    private String _subscriberID;
    private String _channelUserID;
    private String _channelUserCategory;
    private String _ownerID;
    private String _employeeCode;
    private String _employeeName;
    private long _amount;
    private long _minTxnAmount;
    private long _maxTxnAmount;
    private long _monthlyLimit;
    private String _blackListStatus;
    private String _blackListStatusDesc;
    private String _remarks;
    private String _approvedBy;
    private Date _approvedOn;
    private String _approvedOnStr;
    private String _associatedBy;
    private Date _associationDate;
    private long _totalTxnCount;
    private String _minTxnAmtForDisp;
    private String _maxTxnAmtForDisp;
    private String _monthlyLimitForDisp;
    private String _totalTxnCountForDisp;
    private String _statusDes;
    private String _amountForDisp;
    private String _totalTransferAmountForDisp;
    private String _checkBoxVal;
    private String _tempStatus;
    private String voucherType;
    private String voucherSegment;
    private String voucherProfile;
    private String voucherQuantity;
    private boolean isErrorFound;
    	
  

	// For Error Log
    private String _lineNumber;
    private String _errorCode;
    private long _failCount;
    private String _subscriberType;
    private String _language;
    private String _country;
    private long _lastModifiedTime;

    // Language code for request in ScheduleTopUp Process
    private String _languageCode;
    private String _blackListedStatus;

    private String _cp2pPayeeStatus = null;
    private String _c2sPayeeStatus = null;
    private String[] _errorCodeArgs = null;
    private String _subscriberDomainCode = null;
    private String _rechargeThroughParent;
    private String _cp2pPayerStatus = null;
    private String _cp2pWithInList = null;
    private String _cp2pListLevel = null;

    // Added for Corporate IAT Recharge
    private String _restrictedType = null;
    private int _countryCode = 0;
    private String _countryCodeError = null;
    private String _ntwrkPrfxError = null;

    public boolean isIsErrorFound() {
  		return isErrorFound;
  	}

  	public void setisErrorFound(boolean isErrorFound) {
  		this.isErrorFound = isErrorFound;
  	}
    public String getNtwrkPrfxError() {
        return _ntwrkPrfxError;
    }

    public void setNtwrkPrfxError(String ntwrkPrfxError) {
        _ntwrkPrfxError = ntwrkPrfxError;
    }

    public String getCountryCodeError() {
        return _countryCodeError;
    }

    public void setCountryCodeError(String countryCodeError) {
        _countryCodeError = countryCodeError;
    }

    public int getCountryCode() {
        return _countryCode;
    }

    public void setCountryCode(int countryCode) {
        _countryCode = countryCode;
    }

    public String getRestrictedType() {
        return _restrictedType;
    }

    public void setRestrictedType(String restrictedType) {
        _restrictedType = restrictedType;
    }

    /**
     * @return Returns the languageCode.
     */
    public String getLanguageCode() {
        return _languageCode;
    }

    /**
     * @param languageCode
     *            The languageCode to set.
     */
    public void setLanguageCode(String languageCode) {
        _languageCode = languageCode;
    }

    /**
     * @return Returns the tempStatus.
     */
    public String getTempStatus() {
        return _tempStatus;
    }

    /**
     * @param tempStatus
     *            The tempStatus to set.
     */
    public void setTempStatus(String tempStatus) {
        _tempStatus = tempStatus;
    }

    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append(" _subscriberID=").append(_subscriberID);
        strBuild.append(", _channelUserID=").append(_channelUserID);
        strBuild.append(",_channelUserCategory=").append(_channelUserCategory);
        strBuild.append(", _ownerID=").append(_ownerID);
        strBuild.append(", _employeeCode=").append(_employeeCode);
        strBuild.append(", _employeeName=").append(_employeeName);
        strBuild.append(", _amount=").append(_amount);
        strBuild.append(",_minTxnAmount=").append(_minTxnAmount);
        strBuild.append(",_maxTxnAmount=").append(_maxTxnAmount);
        strBuild.append(",_monthlyLimit=").append(_monthlyLimit);
        strBuild.append(",_blackListStatus=").append(_blackListStatus);
        strBuild.append(",_remarks=").append(_remarks);
        strBuild.append(",_approvedBy=").append(_approvedBy);
        strBuild.append(",_approvedOn=").append(_approvedOn);
        strBuild.append(",_associatedBy=").append(_associatedBy);
        strBuild.append(",_associationDate=").append(_associationDate);
        strBuild.append(",_totalTxnCount=").append(_totalTxnCount);
        strBuild.append(",_checkBoxVal=").append(_checkBoxVal);
        strBuild.append(",_lineNumber=").append(_lineNumber);
        strBuild.append(",_errorCode=").append(_errorCode);
        strBuild.append(",_failCount=").append(_failCount);
        strBuild.append(",_subscriberType=").append(_subscriberType);
        strBuild.append(",_language=").append(_language);
        strBuild.append(",_country=").append(_country);
        strBuild.append(",_blackListedStatus=").append(_blackListedStatus);
        strBuild.append(",_languageCode").append(_languageCode);
        strBuild.append(",_subscriberDomainCode=").append(_subscriberDomainCode);
        strBuild.append(",_rechargeThroughParent=").append(_rechargeThroughParent);
        strBuild.append(",_cp2pPayerStatus=").append(_cp2pPayerStatus);
        strBuild.append(",_cp2pWithInList=").append(_cp2pWithInList);
        strBuild.append(",_cp2pListLevel").append(_cp2pListLevel);
        return strBuild.toString();
    }

    /**
     * @return Returns the blackListStatusDesc.
     */
    public String getBlackListStatusDesc() {
        return _blackListStatusDesc;
    }

    /**
     * @param blackListStatusDesc
     *            The blackListStatusDesc to set.
     */
    public void setBlackListStatusDesc(String blackListStatusDesc) {
        _blackListStatusDesc = blackListStatusDesc;
    }

    /**
     * @return Returns the totalTransferAmountForDisp.
     */
    public String getTotalTransferAmountForDisp() {
        return _totalTransferAmountForDisp;
    }

    /**
     * @param totalTransferAmountForDisp
     *            The totalTransferAmountForDisp to set.
     */
    public void setTotalTransferAmountForDisp(String totalTransferAmountForDisp) {
        _totalTransferAmountForDisp = totalTransferAmountForDisp;
    }

    /**
     * @return Returns the blackListedStatus.
     */
    public String getBlackListedStatus() {
        return _blackListedStatus;
    }

    /**
     * @param blackListedStatus
     *            The blackListedStatus to set.
     */
    public void setBlackListedStatus(String blackListedStatus) {
        _blackListedStatus = blackListedStatus;
    }

    /**
     * @return Returns the subscriberType.
     */
    public String getSubscriberType() {
        return _subscriberType;
    }

    /**
     * @param subscriberType
     *            The subscriberType to set.
     */
    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
    }

    /**
     * @return Returns the failCount.
     */
    public long getFailCount() {
        return _failCount;
    }

    /**
     * @param failCount
     *            The failCount to set.
     */
    public void setFailCount(long failCount) {
        _failCount = failCount;
    }

    /**
     * @return Returns the errorCode.
     */
    public String getErrorCode() {
        return _errorCode;
    }

    /**
     * @param errorCode
     *            The errorCode to set.
     */
    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    /**
     * @return Returns the lineNumber.
     */
    public String getLineNumber() {
        return _lineNumber;
    }

    /**
     * @param lineNumber
     *            The lineNumber to set.
     */
    public void setLineNumber(String lineNumber) {
        _lineNumber = lineNumber;
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
     * @return Returns the statusDes.
     */
    public String getStatusDes() {
        return _statusDes;
    }

    /**
     * @param statusDes
     *            The statusDes to set.
     */
    public void setStatusDes(String statusDes) {
        _statusDes = statusDes;
    }

    /**
     * @return Returns the approvedOnStr.
     */
    public String getApprovedOnStr() {
        return _approvedOnStr;
    }

    /**
     * @param approvedOnStr
     *            The approvedOnStr to set.
     */
    public void setApprovedOnStr(String approvedOnStr) {
        _approvedOnStr = approvedOnStr;
    }

    /**
     * @return Returns the maxTxnAmtForDisp.
     */
    public String getMaxTxnAmtForDisp() {
        return _maxTxnAmtForDisp;
    }

    /**
     * @param maxTxnAmtForDisp
     *            The maxTxnAmtForDisp to set.
     */
    public void setMaxTxnAmtForDisp(String maxTxnAmtForDisp) {
        _maxTxnAmtForDisp = maxTxnAmtForDisp;
    }

    /**
     * @return Returns the minTxnAmtForDisp.
     */
    public String getMinTxnAmtForDisp() {
        return _minTxnAmtForDisp;
    }

    /**
     * @param minTxnAmtForDisp
     *            The minTxnAmtForDisp to set.
     */
    public void setMinTxnAmtForDisp(String minTxnAmtForDisp) {
        _minTxnAmtForDisp = minTxnAmtForDisp;
    }

    /**
     * @return Returns the monthlyLimitForDisp.
     */
    public String getMonthlyLimitForDisp() {
        return _monthlyLimitForDisp;
    }

    /**
     * @param monthlyLimitForDisp
     *            The monthlyLimitForDisp to set.
     */
    public void setMonthlyLimitForDisp(String monthlyLimitForDisp) {
        _monthlyLimitForDisp = monthlyLimitForDisp;
    }

    /**
     * @return Returns the totalTxnCountForDisp.
     */
    public String getTotalTxnCountForDisp() {
        return _totalTxnCountForDisp;
    }

    /**
     * @param totalTxnCountForDisp
     *            The totalTxnCountForDisp to set.
     */
    public void setTotalTxnCountForDisp(String totalTxnCountForDisp) {
        _totalTxnCountForDisp = totalTxnCountForDisp;
    }

    /**
     * @return Returns the totalTxnCount.
     */
    public long getTotalTxnCount() {
        return _totalTxnCount;
    }

    /**
     * @param totalTxnCount
     *            The totalTxnCount to set.
     */
    public void setTotalTxnCount(long totalTxnCount) {
        _totalTxnCount = totalTxnCount;
    }

    /**
     * @return Returns the approvedBy.
     */
    public String getApprovedBy() {
        return _approvedBy;
    }

    /**
     * @param approvedBy
     *            The approvedBy to set.
     */
    public void setApprovedBy(String approvedBy) {
        _approvedBy = approvedBy;
    }

    /**
     * @return Returns the approvedOn.
     */
    public Date getApprovedOn() {
        return _approvedOn;
    }

    /**
     * @param approvedOn
     *            The approvedOn to set.
     */
    public void setApprovedOn(Date approvedOn) {
        _approvedOn = approvedOn;
    }

    /**
     * @return Returns the associatedBy.
     */
    public String getAssociatedBy() {
        return _associatedBy;
    }

    /**
     * @param associatedBy
     *            The associatedBy to set.
     */
    public void setAssociatedBy(String associatedBy) {
        _associatedBy = associatedBy;
    }

    /**
     * @return Returns the associationDate.
     */
    public Date getAssociationDate() {
        return _associationDate;
    }

    /**
     * @param associationDate
     *            The associationDate to set.
     */
    public void setAssociationDate(Date associationDate) {
        _associationDate = associationDate;
    }

    /**
     * @return Returns the blackListStatus.
     */
    public String getBlackListStatus() {
        return _blackListStatus;
    }

    /**
     * @param blackListStatus
     *            The blackListStatus to set.
     */
    public void setBlackListStatus(String blackListStatus) {
        _blackListStatus = blackListStatus;
    }

    /**
     * @return Returns the channelUserCategory.
     */
    public String getChannelUserCategory() {
        return _channelUserCategory;
    }

    /**
     * @param channelUserCategory
     *            The channelUserCategory to set.
     */
    public void setChannelUserCategory(String channelUserCategory) {
        _channelUserCategory = channelUserCategory;
    }

    /**
     * @return Returns the channelUserID.
     */
    public String getChannelUserID() {
        return _channelUserID;
    }

    /**
     * @param channelUserID
     *            The channelUserID to set.
     */
    public void setChannelUserID(String channelUserID) {
        _channelUserID = channelUserID;
    }

    /**
     * @return Returns the employeeCode.
     */
    public String getEmployeeCode() {
        return _employeeCode;
    }

    /**
     * @param employeeCode
     *            The employeeCode to set.
     */
    public void setEmployeeCode(String employeeCode) {
        _employeeCode = employeeCode;
    }

    /**
     * @return Returns the employeeName.
     */
    public String getEmployeeName() {
        return _employeeName;
    }

    /**
     * @param employeeName
     *            The employeeName to set.
     */
    public void setEmployeeName(String employeeName) {
        _employeeName = employeeName;
    }

    /**
     * @return Returns the maxTxnAmount.
     */
    public long getMaxTxnAmount() {
        return _maxTxnAmount;
    }

    /**
     * @param maxTxnAmount
     *            The maxTxnAmount to set.
     */
    public void setMaxTxnAmount(long maxTxnAmount) {
        _maxTxnAmount = maxTxnAmount;
    }

    /**
     * @return Returns the minTxnAmount.
     */
    public long getMinTxnAmount() {
        return _minTxnAmount;
    }

    /**
     * @param minTxnAmount
     *            The minTxnAmount to set.
     */
    public void setMinTxnAmount(long minTxnAmount) {
        _minTxnAmount = minTxnAmount;
    }

    /**
     * @return Returns the ownerID.
     */
    public String getOwnerID() {
        return _ownerID;
    }

    /**
     * @param ownerID
     *            The ownerID to set.
     */
    public void setOwnerID(String ownerID) {
        _ownerID = ownerID;
    }

    /**
     * @return Returns the remarks.
     */
    public String getRemarks() {
        return _remarks;
    }

    /**
     * @param remarks
     *            The remarks to set.
     */
    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    /**
     * @return Returns the subscriberID.
     */
    public String getSubscriberID() {
        return _subscriberID;
    }

    /**
     * @param subscriberID
     *            The subscriberID to set.
     */
    public void setSubscriberID(String subscriberID) {
        _subscriberID = subscriberID;
    }

    /**
     * @return Returns the monthlyLimit.
     */
    public long getMonthlyLimit() {
        return _monthlyLimit;
    }

    /**
     * @param monthlyLimit
     *            The monthlyLimit to set.
     */
    public void setMonthlyLimit(long monthlyLimit) {
        _monthlyLimit = monthlyLimit;
    }

    /**
     * @return Returns the amount.
     */
    public long getAmount() {
        return _amount;
    }

    /**
     * @param amount
     *            The amount to set.
     */
    public void setAmount(long amount) {
        _amount = amount;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return _language;
    }

    /**
     * @param language
     *            The language to set.
     */
    public void setLanguage(String language) {
        _language = language;
    }

    /**
     * @return Returns the amountForDisp.
     */
    public String getAmountForDisp() {
        return _amountForDisp;
    }

    /**
     * @param amountForDisp
     *            The amountForDisp to set.
     */
    public void setAmountForDisp(String amountForDisp) {
        _amountForDisp = amountForDisp;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    /**
     * @return Returns the c2sPayee_Status.
     */
    public String getC2sPayeeStatus() {
        return _c2sPayeeStatus;
    }

    /**
     * @param payeeStatus
     *            The c2sPayee_Status to set.
     */
    public void setC2sPayeeStatus(String payeeStatus) {
        _c2sPayeeStatus = payeeStatus;
    }

    /**
     * @return Returns the cp2pPayee_Status.
     */
    public String getCp2pPayeeStatus() {
        return _cp2pPayeeStatus;
    }

    /**
     * @param cp2pPayeeStatus
     *            The cp2pPayee_Status to set.
     */
    public void setCp2pPayeeStatus(String cp2pPayeeStatus) {
        _cp2pPayeeStatus = cp2pPayeeStatus;
    }

    /**
     * @return Returns the errorCodeArgs.
     */
    public String[] getErrorCodeArgs() {
        return _errorCodeArgs;
    }

    /**
     * @param errorCodeArgs
     *            The errorCodeArgs to set.
     */
    public void setErrorCodeArgs(String[] errorCodeArgs) {
        _errorCodeArgs = errorCodeArgs;
    }

    public String getSubscriberDomainCode() {
        return _subscriberDomainCode;
    }

    /**
     * @param subscriberDomainCode
     *            The subscriberDomainCode to set.
     */
    public void setSubscriberDomainCode(String subscriberDomainCode) {
        _subscriberDomainCode = subscriberDomainCode;
    }

    /**
     * @return Returns the RechargeThroughParent.
     */
    public String getRechargeThroughParent() {
        return _rechargeThroughParent;
    }

    /**
     * @param RechargeThroughParent
     *            The RechargeThroughParent to set.
     */
    public void setRechargeThroughParent(String rechargeThroughParent) {
        _rechargeThroughParent = rechargeThroughParent;
    }

    public String getCp2pPayerStatus() {
        return _cp2pPayerStatus;
    }

    /**
     * @param getCp2pPayerStatus
     *            The getCp2pPayerStatus to set.
     */
    public void setCp2pPayerStatus(String cp2pPayerStatus) {
        _cp2pPayerStatus = cp2pPayerStatus;
    }

    // private String _cp2pWithInList=null;
    // private String _cp2pListLevel=null;
    public String getCp2pWithInList() {
        return _cp2pWithInList;
    }

    /**
     * @param setCp2pWithInList
     *            The setCp2pWithInList to set.
     */
    public void setCp2pWithInList(String cp2pWithInList) {
        _cp2pWithInList = cp2pWithInList;
    }

    public String getCp2pListLevel() {
        return _cp2pListLevel;
    }

    /**
     * @param getCp2pListLevel
     *            The getCp2pListLevel to set.
     */
    public void setCp2pListLevel(String cp2pListLevel) {
        _cp2pListLevel = cp2pListLevel;
    }

	public String getvoucherType() {
		return voucherType;
	}

	public void setvoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getvoucherSegment() {
		return voucherSegment;
	}

	public void setvoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

	public String getvoucherProfile() {
		return voucherProfile;
	}

	public void setvoucherProfile(String voucherProfile) {
		this.voucherProfile = voucherProfile;
	}

	public String getVoucherQuantity() {
		return voucherQuantity;
	}

	public void setVoucherQuantity(String voucherQuantity) {
		this.voucherQuantity = voucherQuantity;
	}
    
}
