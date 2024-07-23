package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.loyalitystock.businesslogic.LoyalityStockVO;

public class LoyaltyPointsRedemptionVO implements Serializable {

    private String _userID;
    private String _userName;
    private String _networkID;
    private String _loginID;
    private String _categoryCode;
    private String _categoryName;
    private String _parentID;
    private String _ownerID;
    private String _empCode;
    private String _status;
    private String _msisdn;
    private String _userType;
    private String _externalCode;
    private String _userCode;
    private String _currentLoyaltyPoints;
    private String _previousLoyaltyPoints;
    private String _productCode;
    private String _productShortCode;
    private LoyalityStockVO _loyalityStockVO;;
    private ArrayList _giftItemList;

    private int _redempItemQuantity;
    private int _redempLoyaltyPoint;
    private String _redempLoyaltyAmount;
    private String _multFactor;
    private String _redempType;
    private Date _redemptionDate;
    private String _createdBy;
    private Date _createdOn;
    private String _redempStatus;
    private String _errorCode;

    private String _itemCode;
    private String _itemName;
    private int _itemStockAvailable;
    private int _perItemPoints;
    // private String _stockBuffer;
    private String _redemptionID;
    private Date _modifiedOn;
    private int _stockItemBuffer;
    private String _referenceNo;
    private String _previousLoyaltyPointsBuffer;
    // Added For Target Process- Vibhu
    private String _setId;
    private String _periodId;
    private String _refBaseAllowed;
    private String _endRange;
    private String _lmsTarget;
    private String _dailyTxn;
    private String _weeklyTxn;
    private String _monthlyTxn;
    private String _moduleType;
    private String _promoStartDate;
    private int _parentContribution;
    private int _operatorContribution;
    private long _c2cContribution;
    private long _o2cContribution;
    private String _parentEncryptedPin;
    private String _parentMsisdn;
    private Date _fromDate;
    private long _sumAmount;
    private long _target;
    private long _toRange;
    private Date _sumTxnsDate;
    private Date _currentProcessDate; // (executed date + 1) <
    // (_currentProcessDate) <= (current date)
    private String _txnStatus;
    private String _lmsTxnId;
    private long _totalCrLoyaltyPoint;
    private String _serviceCode;
    private String _pointsType;
    private Date _referenceTargetDate;
    private long _referenceTarget;
    private String _detailId = null;

    private long _parentLoyaltyPoints;
    private String _version;
    private Date _referenceFromDate;
    private Date _referenceToDate;
    private String _targetType;

    private Date _applicableToDate;
    private String _redempLoyaltyPointString;
    private String _transactionId;
    private String _optInOutEnabled;
    private String _optInOutStatus;
	private boolean _bonusCreditDateReached;
	private String _userLanguage;
	private String _userCountry;

    public Date getReferenceFromDate() {
        return _referenceFromDate;
    }

    public void setReferenceFromDate(Date fromDate) {
        _referenceFromDate = fromDate;
    }

    public Date getReferenceToDate() {
        return _referenceToDate;
    }

    public void setReferenceToDate(Date toDate) {
        _referenceToDate = toDate;
    }

    public long getParentLoyaltyPoints() {
        return _parentLoyaltyPoints;
    }

    public void setParentLoyaltyPoints(long loyaltyPoints) {
        _parentLoyaltyPoints = loyaltyPoints;

    }

    public Date getFromDate() {
        return _fromDate;
    }

    public void setFromDate(Date date) {
        _fromDate = date;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String code) {
        _categoryCode = code;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String name) {
        _categoryName = name;
    }

    public String getCurrentLoyaltyPoints() {
        return _currentLoyaltyPoints;
    }

    public void setCurrentLoyaltyPoints(String loyaltyPoints) {
        _currentLoyaltyPoints = loyaltyPoints;
    }

    public String getEmpCode() {
        return _empCode;
    }

    public void setEmpCode(String code) {
        _empCode = code;
    }

    public String getExternalCode() {
        return _externalCode;
    }

    public void setExternalCode(String code) {
        _externalCode = code;
    }

    public ArrayList getGiftItemList() {
        return _giftItemList;
    }

    public void setGiftItemList(ArrayList itemList) {
        _giftItemList = itemList;
    }

    public String getLoginID() {
        return _loginID;
    }

    public void setLoginID(String _loginid) {
        _loginID = _loginid;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String _msisdn) {
        this._msisdn = _msisdn;
    }

    public String getNetworkID() {
        return _networkID;
    }

    public void setNetworkID(String _networkid) {
        _networkID = _networkid;
    }

    public String getOwnerID() {
        return _ownerID;
    }

    public void setOwnerID(String _ownerid) {
        _ownerID = _ownerid;
    }

    public String getParentID() {
        return _parentID;
    }

    public void setParentID(String _parentid) {
        _parentID = _parentid;
    }

    public String getPreviousLoyaltyPoints() {
        return _previousLoyaltyPoints;
    }

    public void setPreviousLoyaltyPoints(String loyaltyPoints) {
        _previousLoyaltyPoints = loyaltyPoints;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String _status) {
        this._status = _status;
    }

    public String getUserCode() {
        return _userCode;
    }

    public void setUserCode(String code) {
        _userCode = code;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String _userid) {
        _userID = _userid;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String name) {
        _userName = name;
    }

    public String getUserType() {
        return _userType;
    }

    public void setUserType(String type) {
        _userType = type;
    }

    public LoyalityStockVO getLoyalityStockVO() {
        return _loyalityStockVO;
    }

    public void setLoyalityStockVO(LoyalityStockVO stockVO) {
        _loyalityStockVO = stockVO;
    }

    public String getMultFactor() {
        return _multFactor;
    }

    public void setMultFactor(String factor) {
        _multFactor = factor;
    }

    public int getRedempItemQuantity() {
        return _redempItemQuantity;
    }

    public void setRedempItemQuantity(int itemQuantity) {
        _redempItemQuantity = itemQuantity;
    }

    public String getRedempLoyaltyAmount() {
        return _redempLoyaltyAmount;
    }

    public void setRedempLoyaltyAmount(String loyaltyAmount) {
        _redempLoyaltyAmount = loyaltyAmount;
    }

    public int getRedempLoyaltyPoint() {
        return _redempLoyaltyPoint;
    }

    public void setRedempLoyaltyPoint(int loyaltyPoint) {
        _redempLoyaltyPoint = loyaltyPoint;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date on) {
        _createdOn = on;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public void setErrorCode(String code) {
        _errorCode = code;
    }

    public String getRedempStatus() {
        return _redempStatus;
    }

    public void setRedempStatus(String status) {
        _redempStatus = status;
    }

    public Date getRedemptionDate() {
        return _redemptionDate;
    }

    public void setRedemptionDate(Date date) {
        _redemptionDate = date;
    }

    public String getRedempType() {
        return _redempType;
    }

    public void setRedempType(String type) {
        _redempType = type;
    }

    public String getItemCode() {
        return _itemCode;
    }

    public void setItemCode(String code) {
        _itemCode = code;
    }

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String name) {
        _itemName = name;
    }

    public int getItemStockAvailable() {
        return _itemStockAvailable;
    }

    public void setItemStockAvailable(int stockAvailable) {
        _itemStockAvailable = stockAvailable;
    }

    public int getPerItemPoints() {
        return _perItemPoints;
    }

    public void setPerItemPoints(int itemPoints) {
        _perItemPoints = itemPoints;
    }

    /*
     * public String getStockBuffer() {
     * return _stockBuffer;
     * }
     * public void setStockBuffer(String buffer) {
     * _stockBuffer = buffer;
     * }
     */
    public String getRedemptionID() {
        return _redemptionID;
    }

    public void setRedemptionID(String _redemptionid) {
        _redemptionID = _redemptionid;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    public int getStockItemBuffer() {
        return _stockItemBuffer;
    }

    public void setStockItemBuffer(int itemBuffer) {
        _stockItemBuffer = itemBuffer;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String code) {
        _productCode = code;
    }

    public String getProductShortCode() {
        return _productShortCode;
    }

    public void setProductShortCode(String shortCode) {
        _productShortCode = shortCode;
    }

    public String getReferenceNo() {
        return _referenceNo;
    }

    public void setReferenceNo(String no) {
        _referenceNo = no;
    }

    public String getPreviousLoyaltyPointsBuffer() {
        return _previousLoyaltyPointsBuffer;
    }

    public void setPreviousLoyaltyPointsBuffer(String loyaltyPointsBuffer) {
        _previousLoyaltyPointsBuffer = loyaltyPointsBuffer;
    }

    public String getSetId() {
        return _setId;
    }

    public void setSetId(String id) {
        _setId = id;
    }

    public String getPeriodId() {
        return _periodId;
    }

    public void setPeriodId(String periodId) {
        _periodId = periodId;
    }

    public String getRefBaseAllowed() {
        return _refBaseAllowed;
    }

    public void setRefBaseAllowed(String refBaseAllowed) {
        _refBaseAllowed = refBaseAllowed;
    }

    public String getEndRange() {
        return _endRange;
    }

    public void setEndRange(String endRange) {
        _endRange = endRange;
    }

    public String getLmsTarget() {
        return _lmsTarget;
    }

    public void setLmsTarget(String lmsTarget) {
        _lmsTarget = lmsTarget;
    }

    public String getDailyTxn() {
        return _dailyTxn;
    }

    public void setDailyTxn(String dailyTxn) {
        _dailyTxn = dailyTxn;
    }

    public String get_weeklyTxn() {
        return _weeklyTxn;
    }

    public void setWeeklyTxn(String weeklyTxn) {
        _weeklyTxn = weeklyTxn;
    }

    public String getMonthlyTxn() {
        return _monthlyTxn;
    }

    public void set_monthlyTxn(String monthlyTxn) {
        _monthlyTxn = monthlyTxn;
    }

    public String getModuleType() {
        return _moduleType;
    }

    public void setModuleType(String moduleType) {
        _moduleType = moduleType;
    }

    public int getParentContribution() {
        return _parentContribution;
    }

    public void setParentContribution(int contribution) {
        _parentContribution = contribution;
    }

    public int getOperatorContribution() {
        return _operatorContribution;
    }

    public void setOperatorContribution(int contribution) {
        _operatorContribution = contribution;
    }

    public String getPromoStartDate() {
        return _promoStartDate;
    }

    public void setPromoStartDate(String promoStartDate) {
        _promoStartDate = promoStartDate;
    }

    public long getC2cContribution() {
        return _c2cContribution;
    }

    public void setC2cContribution(long contribution) {
        _c2cContribution = contribution;
    }

    public long getO2cContribution() {
        return _o2cContribution;
    }

    public void setO2cContribution(long contribution) {
        _o2cContribution = contribution;
    }

    public String getParentEncryptedPin() {
        return _parentEncryptedPin;
    }

    public void setParentEncryptedPin(String encryptedPin) {
        _parentEncryptedPin = encryptedPin;
    }

    public String getParentMsisdn() {
        return _parentMsisdn;
    }

    public void setParentMsisdn(String msisdn) {
        _parentMsisdn = msisdn;
    }

    public Long getSumAmount() {
        return _sumAmount;
    }

    public void setSumAmount(Long amount) {
        _sumAmount = amount;
    }

    public Long getTarget() {
        return _target;
    }

    public void setTarget(Long _target) {
        this._target = _target;
    }

    public Long getToRange() {
        return _toRange;
    }

    public void setToRange(Long range) {
        _toRange = range;
    }

    public Date getSumTxnsDate() {
        return _sumTxnsDate;
    }

    public void setSumTxnsDate(Date txnsDate) {
        _sumTxnsDate = txnsDate;
    }

    public Date getCurrentProcessDate() {
        return _currentProcessDate;
    }

    public void setCurrentProcessDate(Date date) {
        _currentProcessDate = date;
    }

    public String getTxnStatus() {
        return _txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        _txnStatus = txnStatus;
    }

    public String getLmsTxnId() {
        return _lmsTxnId;
    }

    public void setLmsTxnId(String txnId) {
        _lmsTxnId = txnId;
    }

    public Long getTotalCrLoyaltyPoint() {
        return _totalCrLoyaltyPoint;
    }

    public void setTotalCrLoyaltyPoint(Long crLoyaltyPoint) {
        _totalCrLoyaltyPoint = crLoyaltyPoint;
    }

    public String getServiceCode() {
        return _serviceCode;
    }

    public void setServiceCode(String code) {
        _serviceCode = code;
    }

    public String getPointsType() {
        return _pointsType;
    }

    public void setPointsType(String type) {
        _pointsType = type;
    }

    public Date getReferenceTargetDate() {
        return _referenceTargetDate;
    }

    public void setReferenceTargetDate(Date referenceTargetDate) {
        _referenceTargetDate = referenceTargetDate;
    }

    public long getReferenceTarget() {
        return _referenceTarget;
    }

    public void setReferenceTarget(long referenceTarget) {
        _referenceTarget = referenceTarget;
    }

    public String getDetailId() {
        return _detailId;
    }

    public void setDetailId(String detailId) {
        _detailId = detailId;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String _version) {
        this._version = _version;
    }

    public String getTargetType() {
        return _targetType;
    }

    public void setTargetType(String type) {
        _targetType = type;
    }

    public Date getApplicableToDate() {
        return _applicableToDate;
    }

    public void setApplicableToDate(Date toDate) {
        _applicableToDate = toDate;
    }

    // brajesh
    public String getRedempLoyaltyPointString() {
        return _redempLoyaltyPointString;
    }

    public void setRedempLoyaltyPointString(String loyaltyPoint) {
        _redempLoyaltyPointString = loyaltyPoint;
    }

    public String getTransactionId() {
        return _transactionId;
    }

    public void setTransactionId(String transactionId) {
        _transactionId = transactionId;
    }

    // Handling of OPT IN/OPT OUT as design changed
    public void setOptInOutEnabled(String optInOutEnabled) {
        _optInOutEnabled = optInOutEnabled;
    }

    public String getOptInOutEnabled() {
        return _optInOutEnabled;
    }

    public void setOptInOutStatus(String optInOutStatus) {
        _optInOutStatus = optInOutStatus;
    }

    public String getOptInOutStatus() {
        return _optInOutStatus;
    }
    
	/**
	 * @return the bonusCreditDateReached
	 */
	public boolean getBonusCreditDateReached() {
		return _bonusCreditDateReached;
	}
	/**
	 * @param bonusCreditDateReached the bonusCreditDateReached to set
	 */
	public void setBonusCreditDateReached(boolean bonusCreditDateReached) {
		_bonusCreditDateReached = bonusCreditDateReached;
	}
	/**
	 * @return the userLanguage
	 */
	public String getUserLanguage() {
		return _userLanguage;
	}
	/**
	 * @param userLanguage the userLanguage to set
	 */
	public void setUserLanguage(String userLanguage) {
		_userLanguage = userLanguage;
	}
	/**
	 * @return the userCountry
	 */
	public String getUserCountry() {
		return _userCountry;
	}
	/**
	 * @param userCountry the userCountry to set
	 */
	public void setUserCountry(String userCountry) {
		_userCountry = userCountry;
	}
	
	private String _promoEndDate;
	private String _domainName;
	private String _geographyName;

	public String getPromoEndDate() {
		return _promoEndDate;
	}

	public void setPromoEndDate(String promoEndDate) {
		_promoEndDate = promoEndDate;
	}

	public String getDomainName() {
		return _domainName;
	}

	public void setDomainName(String domainName) {
		_domainName = domainName;
	}

	public String getGeographyName() {
		return _geographyName;
	}

	public void setGeographyName(String geographyName) {
		_geographyName = geographyName;
	}
}
