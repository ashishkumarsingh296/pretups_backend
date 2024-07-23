/**
 * @(#)LoyaltyVO.java
 *                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 * 
 *                    <description>
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    rakesh.sinha Dec,2013 Initital Creation
 *                    Vibhu Trehan Jan,2014 Modification
 * 
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import com.btsl.util.BTSLUtil;

public class LoyaltyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _category;
    private long _transferamt;
    private long _promoid;
    private long _promoAssoid;

    private String _domainCodePayee;
    private String _domainCodePayeer;
    private String _catCodePayee;
    private String _catCodePayeer;

    private long _rewardid;
    private long _rewardrangeid;
    private long _fromamt;
    private long _toamt;

    private Date _fromDate;
    private Date _toDate;

    private int _factor;

    private String _payeeallowed;
    private String _payeeownerallowed;
    private String _payeeherirchyallowed;

    private String _payeerallowed;
    private String _payeerownerallowed;
    private String _payeerherirchyallowed;

    private String _txnStatus;
    private String _errorode;

    private long _crpointtoPayee;
    private long _crpointtoPayeer;
    private long _crpointtoPayeeOwner;
    private long _crpointtoPayeerOwner;
    private long _crpointtoPayeeHerichy;
    private long _crpointtoPayeerHerichy;

    private String _userid;
    private String _networkCode;

    private String _reciverid;

    private String _serviceType;

    private String _txnId;

    private long _totalCrLoyaltyPoint;

    private String _senderMsisdn;
    private String _reciverMsisdn;

    private String _lmstxnid;

    private Date _createdOn;

    private String fromuserId;
    private String touserId;

    private String _domain;

    private String _associationfor;

    private long _totalLoyalityPointsSum = 0;
    private long _loyalityPointsInitiated = 0;
    private long _loyalityPointsExpired = 0;
    private long _loyalityPointsRedempted = 0;
    private long _loyalityPointsStock = 0;
    private long _loyalityPointsUsers = 0;
    private String _loyaltyPoint;
    private String _comments;

    private String _setId;
    private String _toSetId;
    private boolean _errorFlag = false;

    private String _moduleType;
    // for lms Promotion Process
    private String _setName;
    private String _msisdn;
    private Locale _locale;
    private String _message;

    private String _productCode;
    // added by brajesh for version column entry in bonus and bonus_history
    // table
    private String _version;
    private String _proflieType;
    private String _bucketCode;
    private Date _pointsDate;
    private String _transferId;
    private String _createdBy;

    private String _lmsProfileName;
    private String _lmsSetName;
    private String _lmsSetId;
    private String _optInOutEnabled;
    private String _optInOutStatus;
	private String _productName;
	
    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String code) {
        _productCode = code;
    }

    public long getTransferamt() {
        return _transferamt;
    }

    public void setTransferamt(long _transferamt) {
        this._transferamt = _transferamt;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String _category) {
        this._category = _category;
    }

    public long getPromoid() {
        return _promoid;
    }

    public void setPromoid(long _promoid) {
        this._promoid = _promoid;
    }

    public long getPromoAssoid() {
        return _promoAssoid;
    }

    public void setPromoAssoid(long assoid) {
        _promoAssoid = assoid;
    }

    public String getDomainCodePayee() {
        return _domainCodePayee;
    }

    public void setDomainCodePayee(String codePayee) {
        _domainCodePayee = codePayee;
    }

    public String getDomainCodePayeer() {
        return _domainCodePayeer;
    }

    public void setDomainCodePayeer(String codePayeer) {
        _domainCodePayeer = codePayeer;
    }

    public String getCatCodePayee() {
        return _catCodePayee;
    }

    public void setCatCodePayee(String codePayee) {
        _catCodePayee = codePayee;
    }

    public String getCatCodePayeer() {
        return _catCodePayeer;
    }

    public void setCatCodePayeer(String codePayeer) {
        _catCodePayeer = codePayeer;
    }

    public long getFromamt() {
        return _fromamt;
    }

    public void setFromamt(long _fromamt) {
        this._fromamt = _fromamt;
    }

    public long getToamt() {
        return _toamt;
    }

    public void setToamt(long _toamt) {
        this._toamt = _toamt;
    }

    public long getRewardid() {
        return _rewardid;
    }

    public void setRewardid(long _rewardid) {
        this._rewardid = _rewardid;
    }

    public long getRewardrangeid() {
        return _rewardrangeid;
    }

    public void setRewardrangeid(long _rewardrangeid) {
        this._rewardrangeid = _rewardrangeid;
    }

    public Date getFromDate() {
        return _fromDate;
    }

    public void setFromDate(Date date) {
        _fromDate = date;
    }

    public Date getToDate() {
        return _toDate;
    }

    public void setToDate(Date date) {
        _toDate = date;
    }

    public int getFactor() {
        return _factor;
    }

    public void setFactor(int _factor) {
        this._factor = _factor;
    }

    public String getPayeeallowed() {
        return _payeeallowed;
    }

    public void setPayeeallowed(String _payeeallowed) {
        this._payeeallowed = _payeeallowed;
    }

    public String getPayeeownerallowed() {
        return _payeeownerallowed;
    }

    public void setPayeeownerallowed(String _payeeownerallowed) {
        this._payeeownerallowed = _payeeownerallowed;
    }

    public String getPayeeherirchyallowed() {
        return _payeeherirchyallowed;
    }

    public void setPayeeherirchyallowed(String _payeeherirchyallowed) {
        this._payeeherirchyallowed = _payeeherirchyallowed;
    }

    public String getPayeerallowed() {
        return _payeerallowed;
    }

    public void setPayeerallowed(String _payeerallowed) {
        this._payeerallowed = _payeerallowed;
    }

    public String getPayeerownerallowed() {
        return _payeerownerallowed;
    }

    public void setPayeerownerallowed(String _payeerownerallowed) {
        this._payeerownerallowed = _payeerownerallowed;
    }

    public String getPayeerherirchyallowed() {
        return _payeerherirchyallowed;
    }

    public void setPayeerherirchyallowed(String _payeerherirchyallowed) {
        this._payeerherirchyallowed = _payeerherirchyallowed;
    }

    public String getTxnStatus() {
        return _txnStatus;
    }

    public void setTxnStatus(String status) {
        _txnStatus = status;
    }

    public String getErrorode() {
        return _errorode;
    }

    public void setErrorode(String _errorode) {
        this._errorode = _errorode;
    }

    public long getCrpointtoPayee() {
        return _crpointtoPayee;
    }

    public void setCrpointtoPayee(long payee) {
        _crpointtoPayee = payee;
    }

    public long getCrpointtoPayeer() {
        return _crpointtoPayeer;
    }

    public void setCrpointtoPayeer(long payeer) {
        _crpointtoPayeer = payeer;
    }

    public long getCrpointtoPayeeOwner() {
        return _crpointtoPayeeOwner;
    }

    public void setCrpointtoPayeeOwner(long payeeOwner) {
        _crpointtoPayeeOwner = payeeOwner;
    }

    public long getCrpointtoPayeerOwner() {
        return _crpointtoPayeerOwner;
    }

    public void setCrpointtoPayeerOwner(long payeerOwner) {
        _crpointtoPayeerOwner = payeerOwner;
    }

    public long getCrpointtoPayeeHerichy() {
        return _crpointtoPayeeHerichy;
    }

    public void setCrpointtoPayeeHerichy(long payeeHerichy) {
        _crpointtoPayeeHerichy = payeeHerichy;
    }

    public long getCrpointtoPayeerHerichy() {
        return _crpointtoPayeerHerichy;
    }

    public void setCrpointtoPayeerHerichy(long payeerHerichy) {
        _crpointtoPayeerHerichy = payeerHerichy;
    }

    public String getUserid() {
        return _userid;
    }

    public void setUserid(String _userid) {
        this._userid = _userid;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    public String getReciverid() {
        return _reciverid;
    }

    public void setReciverid(String _reciverid) {
        this._reciverid = _reciverid;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String type) {
        _serviceType = type;
    }

    public String getTxnId() {
        return _txnId;
    }

    public void setTxnId(String id) {
        _txnId = id;
    }

    public long getTotalCrLoyaltyPoint() {
        return _totalCrLoyaltyPoint;
    }

    public void setTotalCrLoyaltyPoint(long crLoyaltyPoint) {
        _totalCrLoyaltyPoint = crLoyaltyPoint;
    }

    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    public void setSenderMsisdn(String senderMsisdn) {
        this._senderMsisdn = senderMsisdn;
    }

    public String getReciverMsisdn() {
        return _reciverMsisdn;
    }

    public void setReciverMsisdn(String reciverMsisdn) {
        this._reciverMsisdn = reciverMsisdn;
    }

    public String getLmstxnid() {
        return _lmstxnid;
    }

    public void setLmstxnid(String lmstxnid) {
        this._lmstxnid = lmstxnid;
    }

    public void setCreatedOn(Date createdOn) {

        this._createdOn = createdOn;

    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public String getFromuserId() {
        return fromuserId;
    }

    public void setFromuserId(String fromuserId) {
        this.fromuserId = fromuserId;
    }

    public String getTouserId() {
        return touserId;
    }

    public void setTouserId(String touserId) {
        this.touserId = touserId;
    }

    public String getAssociationfor() {
        return _associationfor;
    }

    public void setAssociationfor(String associationfor) {
        _associationfor = associationfor;
    }

    public String getDomain() {
        return _domain;
    }

    public void setDomain(String domain) {
        _domain = domain;
    }

    public long getTotalLoyalityPointsSum() {
        return _totalLoyalityPointsSum;
    }

    public void setTotalLoyalityPointsSum(long totalLoyalityPointsSum) {
        _totalLoyalityPointsSum = totalLoyalityPointsSum;
    }

    public long getLoyalityPointsInitiated() {
        return _loyalityPointsInitiated;
    }

    public void setLoyalityPointsInitiated(long loyalityPointsInitiated) {
        _loyalityPointsInitiated = loyalityPointsInitiated;
    }

    public long getLoyalityPointsRedempted() {
        return _loyalityPointsRedempted;
    }

    public void setLoyalityPointsRedempted(long loyalityPointsRedempted) {
        _loyalityPointsRedempted = loyalityPointsRedempted;
    }

    public long getLoyalityPointsStock() {
        return _loyalityPointsStock;
    }

    public void setLoyalityPointsStock(long loyalityPointsStock) {
        _loyalityPointsStock = loyalityPointsStock;
    }

    public long getLoyalityPointsUsers() {
        return _loyalityPointsUsers;
    }

    public void setLoyalityPointsUsers(long loyalityPointsUsers) {
        _loyalityPointsUsers = loyalityPointsUsers;
    }

    public long getLoyalityPointsExpired() {
        return _loyalityPointsExpired;
    }

    public void setLoyalityPointsExpired(long loyalityPointsExpired) {
        _loyalityPointsExpired = loyalityPointsExpired;
    }

    public String getLoyaltyPoint() {
        return _loyaltyPoint;
    }

    public void setLoyaltyPoint(String loyaltyPoint) {
        _loyaltyPoint = loyaltyPoint;
    }

    public String getComments() {
        return _comments;
    }

    public void setComments(String _comments) {
        this._comments = _comments;
    }

    public boolean getErrorFlag() {
        return _errorFlag;
    }

    public void setErrorFlag(boolean flag) {
        _errorFlag = flag;
    }

    public String getSetId() {
        return _setId;
    }

    public void setSetId(String setId) {
        _setId = setId;
    }

    public String getModuleType() {
        return _moduleType;
    }

    public void setModuleType(String moduleType) {
        _moduleType = moduleType;
    }

    public String getToSetId() {
        return _toSetId;
    }

    public void setToSetId(String toSetId) {
        _toSetId = toSetId;
    }

    public String getSetName() {
        return _setName;
    }

    public void setSetName(String setName) {
        _setName = setName;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setLocale(Locale _locale) {
        this._locale = _locale;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String _message) {
        this._message = _message;
    }

    // Added by Brajesh For version column in bonus and bonus_histroy table
    public String getVersion() {
        return _version;
    }

    public void setVersion(String code) {
        _version = code;
    }

    private String _endRange;

    public String getEndRange() {
        return _endRange;
    }

    public void setEndRange(String code) {
        _endRange = code;
    }

    private String _messageConfEnabled;

    public String getMessageConfEnabled() {
        return _messageConfEnabled;
    }

    public void setMessageConfEnabled(String code) {
        _messageConfEnabled = code;
    }

    private String _detailType;

    public String getDetailType() {
        return _detailType;
    }

    public void setDetailType(String code) {
        _detailType = code;
    }

    private String _detailId;

    public String getDetailId() {
        return _detailId;
    }

    public void setDetailId(String code) {
        _detailId = code;
    }

    private String _periodId;

    public String getPeriodId() {
        return _periodId;
    }

    public void setPeriodId(String code) {
        _periodId = code;
    }

    public String getProflieType() {
        return _proflieType;
    }

    public void setProfileType(String type) {
        _proflieType = type;
    }

    public String getBucketCode() {
        return _bucketCode;
    }

    public void setBucketCode(String code) {
        _bucketCode = code;
    }

    public Date getPointsDate() {
        return _pointsDate;
    }

    public void setPointsDate(Date date) {
        _pointsDate = date;
    }

    public String getTransferId() {
        return _transferId;
    }

    public void setTransferId(String id) {
        _transferId = id;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    /*
     * public String getLmsSetName() {
     * return _lmsProfileName;
     * }
     * public void setLmsSetName(String profileName) {
     * _lmsSetName = profileName;
     * }
     * public String getLmsSetId() {
     * return _lmsSetId;
     * }
     * public void setLmsSetId(String profileName) {
     * _lmsSetId = profileName;
     * }
     */

    public String getLmsProfileName() {
        return _lmsProfileName;
    }

    public void setLmsProfileName(String profileName) {
        _lmsProfileName = profileName;
    }

    public String getSetNamewithSetId() {
        if (!BTSLUtil.isNullString(this._setId)) {
            return this._lmsProfileName + "(" + this._setId + ")";
        }
        return this._lmsProfileName;
    }

    // Handling of OPT IN/OPT OUT Message
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
	
	public void setProductName(String productName) {
		_productName =productName;
	}
	public String getProductName() {
		
		return _productName;
	}
}
