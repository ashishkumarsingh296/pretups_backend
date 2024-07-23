/**
 * @(#)PromotionDetailVO.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            rakesh.sinha Dec,2013 Initital Creation
 *                            Vibhu Trehan Jan,2014 Modification
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */

package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.util.Date;

import com.btsl.pretups.processes.businesslogic.ProgressiveMessageVO;

public class PromotionDetailsVO {

    private static final long serialVersionUID = 1L;

    private String _userid;
    private String _networkCode;
    private String _reciverid;
    private String _serviceType;
    private long _rewardRangeId;
    private long _rewardId;
    private String _setId;
    private String _toSetId;
    private String _promotionType;
    private String _setName;
    private long _startRange;
    private long _endRange;
    private String _startRangeAsString = null;
    private String _endRangeAsString = null;
    private String _pointsTypeCode;
    private String _pointsAsString = null;
    private String _detailType;
    private String _detailSubType;
    private String _subscriberType;
    private String _periodId;
    private String _type;
    private String _userType;
    private String _serviceCode;
    private long _minLimit;
    private long _maxLimit;
    private String _version;
    private long _points;;
    private String _referenceBasedAllowed;
    private String _pointsType;
    private String _productCode;
    //old

    private long _promoid;
    private long _promoAssoid;
    private long _rewardid;
    private long _fromamt;
    private long _toamt;
    private String _payeeallowed;
    private String _payeeownerallowed;
    private String _payeeherirchyallowed;

    private String _payeerallowed;
    private String _payeerownerallowed;
    private String _payeerherirchyallowed;

    private String _associationFor;
    private String _associatedDomain;
    private String _associationCategory;
    private Date _fromDate;
    private Date _toDate;

    private String _detailid;
    private ProgressiveMessageVO ProgressiveMessageVO = new ProgressiveMessageVO();
    private String _optInOutEnabled;
    private String _optInOutStatus;

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

    public String get_setId() {
        return _setId;
    }

    public void set_setId(String id) {
        _setId = id;
    }

    public String getPromotionType() {
        return _promotionType;
    }

    public void setPromotionType(String promotionType) {
        _promotionType = promotionType;
    }

    public String getSetName() {
        return _setName;
    }

    public void setSetName(String name) {
        _setName = name;
    }

    public long getStartRange() {
        return _startRange;
    }

    public void setStartRange(long startRange) {
        _startRange = startRange;
    }

    public long getEndRange() {
        return _endRange;
    }

    public void setEndRange(long endRange) {
        _endRange = endRange;
    }

    public String getStartRangeAsString() {
        return _startRangeAsString;
    }

    public void setStartRangeAsString(String startRangeAsString) {
        _startRangeAsString = startRangeAsString;
    }

    public String getEndRangeAsString() {
        return _endRangeAsString;
    }

    public void setEndRangeAsString(String endRangeAsString) {
        _endRangeAsString = endRangeAsString;
    }

    public String getPointsTypeCode() {
        return _pointsTypeCode;
    }

    public void setPointsTypeCode(String pointsTypeCode) {
        _pointsTypeCode = pointsTypeCode;
    }

    public String getPointsAsString() {
        return _pointsAsString;
    }

    public void setPointsAsString(String asString) {
        _pointsAsString = asString;
    }

    public String getDetailType() {
        return _detailType;
    }

    public void setDetailType(String type) {
        _detailType = type;
    }

    public String getDetailSubType() {
        return _detailSubType;
    }

    public void setDetailSubType(String subType) {
        _detailSubType = subType;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String type) {
        _subscriberType = type;
    }

    public String getPeriodId() {
        return _periodId;
    }

    public void setPeriodId(String id) {
        _periodId = id;
    }

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public String getUserType() {
        return _userType;
    }

    public void setUserType(String type) {
        _userType = type;
    }

    public String getServiceCode() {
        return _serviceCode;
    }

    public void setServiceCode(String code) {
        _serviceCode = code;
    }

    public long getMinLimit() {
        return _minLimit;
    }

    public void setMinLimit(long limit) {
        _minLimit = limit;
    }

    public long getMaxLimit() {
        return _maxLimit;
    }

    public void setMaxLimit(long limit) {
        _maxLimit = limit;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String _version) {
        this._version = _version;
    }

    public long getPoints() {
        return _points;
    }

    public void setPoints(long _points) {
        this._points = _points;
    }

    public long getRewardrangeid() {
        return _rewardRangeId;
    }

    public void setRewardrangeid(long rangeId) {
        _rewardRangeId = rangeId;
    }

    public long get_rewardId() {
        return _rewardId;
    }

    public void set_rewardId(long id) {
        _rewardId = id;
    }

    // old

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

    public String getAssociationFor() {
        return _associationFor;
    }

    public void setAssociationFor(String for1) {
        _associationFor = for1;
    }

    public String getAssociatedDomain() {
        return _associatedDomain;
    }

    public void setAssociatedDomain(String domain) {
        _associatedDomain = domain;
    }

    public String getAssociationCategory() {
        return _associationCategory;
    }

    public void setAssociationCategory(String category) {
        _associationCategory = category;
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

    public long getRewardid() {
        return _rewardid;
    }

    public void setRewardid(long _rewardid) {
        this._rewardid = _rewardid;
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

    public String get_toSetId() {
        return _toSetId;
    }

    public void set_toSetId(String setId) {
        _toSetId = setId;
    }

    public String getReferenceBasedAllowed() {
        return _referenceBasedAllowed;
    }

    public void setReferenceBasedAllowed(String referenceBasedAllowed) {
        _referenceBasedAllowed = referenceBasedAllowed;
    }

    public String getPointsType() {
        return _pointsType;
    }

    public void setPointsType(String type) {
        _pointsType = type;
    }

    public String getDetailid() {
        return _detailid;
    }

    public void setDetailid(String detailid) {
        _detailid = detailid;
    }

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append(",User Name =" + _userid);
        sbf.append(",Category Code =" + _networkCode);
        sbf.append(",Category Code =" + _networkCode);
        sbf.append(",_serviceType =" + _serviceType);
        sbf.append(",_rewardRangeId =" + _rewardRangeId);
        sbf.append(",_rewardId =" + _rewardId);
        sbf.append(",_setId =" + _setId);
        sbf.append(",_toSetId =" + _toSetId);
        sbf.append(",_promotionType =" + _promotionType);
        sbf.append(",_setName =" + _setName);
        sbf.append(",Category Code =" + _networkCode);
        sbf.append(",_endRange =" + _endRange);
        sbf.append(",_startRangeAsString =" + _startRangeAsString);
        sbf.append(",_endRangeAsString =" + _endRangeAsString);
        sbf.append(",_pointsTypeCode =" + _pointsTypeCode);
        sbf.append(",_pointsAsString=" + _pointsAsString);
        sbf.append(",_detailType =" + _detailType);
        sbf.append(",_detailSubType =" + _detailSubType);
        sbf.append(",_subscriberType =" + _subscriberType);
        sbf.append(",_periodId=" + _periodId);
        sbf.append(",_type =" + _type);
        sbf.append(",_userType =" + _userType);
        sbf.append(",_serviceCode =" + _serviceCode);
        sbf.append(",_minLimit=" + _minLimit);
        sbf.append(",_maxLimit =" + _maxLimit);
        sbf.append(",_version =" + _version);
        sbf.append(",_points=" + _points);
        sbf.append(",_referenceBasedAllowed=" + _referenceBasedAllowed);
        sbf.append(",_pointsType=" + _pointsType);
        return sbf.toString();

    }

    // brajesh
    private String _messageConfEnabled;

    public String getMessageConfEnabled() {
        return _messageConfEnabled;
    }

    public void setMessageConfEnabled(String code) {
        _messageConfEnabled = code;
    }

    // brajesh
    private String _messageSuccess;

    public String getMessageSuccess() {
        return _messageSuccess;
    }

    public void setMessageSuccess(String code) {
        _messageSuccess = code;
    }

    // brajesh
    private String _messageFailure;

    public String getMessageFailure() {
        return _messageFailure;
    }

    public void setMessageFailure(String code) {
        _messageFailure = code;
    }

    // brajesh
    public ProgressiveMessageVO getProgressiveMessageVO() {
        return ProgressiveMessageVO;
    }

    public void setProgressiveMessageVO(ProgressiveMessageVO progressiveMessageVO) {
        ProgressiveMessageVO = progressiveMessageVO;
    }

    /**
     * @return the optInOutEnabled
     */
    public String getOptInOutEnabled() {
        return _optInOutEnabled;
    }

    /**
     * @param optInOutEnabled
     *            the optInOutEnabled to set
     */
    public void setOptInOutEnabled(String optInOutEnabled) {
        _optInOutEnabled = optInOutEnabled;
    }

    public void setOptInOutStatus(String optInOutStatus) {
        _optInOutStatus = optInOutStatus;
    }

    public String getOptInOutStatus() {
        return _optInOutStatus;
    }
	public void setProductCode(String productCode) {
		_productCode =productCode;
	}
	public String getProductCode() {
		
		return _productCode;
	}
}
