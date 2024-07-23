package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ActivationProfileCombinedLMSVO implements Serializable {

    private ProfileSetDetailsLMSVO _profileSetDetailsVO;
    private ArrayList _slabsList;
    private ArrayList _countSlabsList;
    private ArrayList _amountSlabsList;
    private String _setId;
    private String _version;
    private String _detailId;
    private String _type;
    private String _userType;
    private String _detailType;
    private String _detailSubType;
    private String _periodId;
    private String _serviceCode;
    private String _subscriberType;
    private long _startRange;
    private long _endRange;
    private String _factor;
    private int _points;
    private int _minLimit;
    private int _maxLimit;
    private Date _applicableFrom;
    private long _oneTimeBonus;
    private long _bonusDuration;
    private String _productCode;
    private String _shortCode;
    private String _setName;
    // added by rahul for points type
    private String _pointsType;
    // added for modify profile
    private int _lastVersion;
    private Date _applicableTo;
    private Date _refApplicableFrom;
    private Date _refApplicableTo;
    private String _promotionType;
    private String _promotionTypeName;
    private String _referenceBasedFlag;
    private String _moduleType;
    private String _optContribution;
    private String _prtContribution;

    private String _referenceType;
    private String _referenceDate;

    private String _optInOut;

    public String getOptInOut() {
        return _optInOut;
    }

    public void setOptInOut(String optInOut) {
        _optInOut = optInOut;
    }

    public String getReferenceType() {
        return _referenceType;
    }

    public void setReferenceType(String type) {
        _referenceType = type;
    }

    public String getReferenceDate() {
        return _referenceDate;
    }

    public void setReferenceDate(String date) {
        _referenceDate = date;
    }

    // added for logger entries
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("_setId=" + _setId + ",");
        sb.append("_version=" + _version + ",");
        sb.append("_detailId=" + _detailId + ",");
        sb.append("_type=" + _type + ",");
        sb.append("_userType=" + _userType + ",");
        sb.append("_detailType=" + _detailType + ",");
        sb.append("_detailSubType=" + _detailSubType + ",");
        sb.append("_periodId=" + _periodId + ",");
        sb.append("_productCode=" + _productCode + ",");
        sb.append("_startRange=" + _startRange + ",");
        sb.append("_endRange=" + _endRange + ",");
        sb.append("_factor=" + _factor + ",");
        sb.append("_points=" + _points + ",");
        sb.append("_minLimit=" + _minLimit + ",");
        sb.append("_maxLimit=" + _maxLimit + ",");
        sb.append("_serviceCode=" + _serviceCode + ",");
        sb.append("_subscriberType=" + _subscriberType + ",");
        sb.append("_applicableFrom=" + _applicableFrom + ",");
        sb.append("_applicableTo=" + _applicableTo + ",");
        sb.append("_refApplicableFrom=" + _refApplicableFrom + ",");
        sb.append("_refApplicableTo=" + _refApplicableTo + ",");
        sb.append("_oneTimeBonus=" + _oneTimeBonus + ",");
        sb.append("_bonusDuration=" + _bonusDuration + ",");
        sb.append("_shortCode=" + _shortCode + ",");
        sb.append("_setName=" + _setName + ",");
        sb.append("_pointsType=" + _pointsType + ",");
        sb.append("_lastVersion=" + _lastVersion + ",");
        sb.append("_profileSetDetailsVO=" + _profileSetDetailsVO + ",");
        return sb.toString();
    }

    public ArrayList getSlabsList() {
        return _slabsList;
    }

    public void setSlabsList(ArrayList list) {
        _slabsList = list;
    }

    public ProfileSetDetailsLMSVO getProfileSetDetailsVO() {
        return _profileSetDetailsVO;
    }

    public void setProfileSetDetailsVO(ProfileSetDetailsLMSVO setDetailsVO) {
        _profileSetDetailsVO = setDetailsVO;
    }

    public ArrayList getAmountSlabsList() {
        return _amountSlabsList;
    }

    public void setAmountSlabsList(ArrayList slabsList) {
        _amountSlabsList = slabsList;
    }

    public ArrayList getCountSlabsList() {
        return _countSlabsList;
    }

    public void setCountSlabsList(ArrayList slabsList) {
        _countSlabsList = slabsList;
    }

    /**
     * @return Returns the _detailId.
     */
    public String getDetailId() {
        return _detailId;
    }

    /**
     * @param id
     *            The _detailId to set.
     */
    public void setDetailId(String id) {
        _detailId = id;
    }

    /**
     * @return Returns the _detailSubType.
     */
    public String getDetailSubType() {
        return _detailSubType;
    }

    /**
     * @param subType
     *            The _detailSubType to set.
     */
    public void setDetailSubType(String subType) {
        _detailSubType = subType;
    }

    /**
     * @return Returns the _detailType.
     */
    public String getDetailType() {
        return _detailType;
    }

    /**
     * @param type
     *            The _detailType to set.
     */
    public void setDetailType(String type) {
        _detailType = type;
    }

    /**
     * @return Returns the _endRange.
     */
    public long getEndRange() {
        return _endRange;
    }

    /**
     * @param range
     *            The _endRange to set.
     */
    public void setEndRange(long range) {
        _endRange = range;
    }

    /**
     * @return Returns the _maxLimit.
     */
    public int getMaxLimit() {
        return _maxLimit;
    }

    /**
     * @param limit
     *            The _maxLimit to set.
     */
    public void setMaxLimit(int limit) {
        _maxLimit = limit;
    }

    /**
     * @return Returns the _minLimit.
     */
    public int getMinLimit() {
        return _minLimit;
    }

    /**
     * @param limit
     *            The _minLimit to set.
     */
    public void setMinLimit(int limit) {
        _minLimit = limit;
    }

    /**
     * @return Returns the _points.
     */
    public int getPoints() {
        return _points;
    }

    /**
     * @param _points
     *            The _points to set.
     */
    public void setPoints(int _points) {
        this._points = _points;
    }

    /**
     * @return Returns the _periodId.
     */
    public String getPeriodId() {
        return _periodId;
    }

    /**
     * @param id
     *            The _periodId to set.
     */
    public void setPeriodId(String id) {
        _periodId = id;
    }

    /**
     * @return Returns the _serviceCode.
     */
    public String getServiceCode() {
        return _serviceCode;
    }

    /**
     * @param code
     *            The _serviceCode to set.
     */
    public void setServiceCode(String code) {
        _serviceCode = code;
    }

    /**
     * @return Returns the _setId.
     */
    public String getSetId() {
        return _setId;
    }

    /**
     * @param id
     *            The _setId to set.
     */
    public void setSetId(String id) {
        _setId = id;
    }

    /**
     * @return Returns the _startRange.
     */
    public long getStartRange() {
        return _startRange;
    }

    /**
     * @param range
     *            The _startRange to set.
     */
    public void setStartRange(long range) {
        _startRange = range;
    }

    /**
     * @return Returns the _subscriberType.
     */
    public String getSubscriberType() {
        return _subscriberType;
    }

    /**
     * @param type
     *            The _subscriberType to set.
     */
    public void setSubscriberType(String type) {
        _subscriberType = type;
    }

    /**
     * @return Returns the _type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param _type
     *            The _type to set.
     */
    public void setType(String _type) {
        this._type = _type;
    }

    /**
     * @return Returns the _userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param type
     *            The _userType to set.
     */
    public void setUserType(String type) {
        _userType = type;
    }

    /**
     * @return Returns the _version.
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @param _version
     *            The _version to set.
     */
    public void setVersion(String _version) {
        this._version = _version;
    }

    /**
     * @return Returns the _applicableFrom.
     */
    public Date getApplicableFrom() {
        return _applicableFrom;
    }

    /**
     * @param from
     *            The _applicableFrom to set.
     */
    public void setApplicableFrom(Date from) {
        _applicableFrom = from;
    }

    /**
     * @return Returns the _setName.
     */
    public String getSetName() {
        return _setName;
    }

    /**
     * @param name
     *            The _setName to set.
     */
    public void setSetName(String name) {
        _setName = name;
    }

    /**
     * @return Returns the _oneTimeBonus.
     */
    public long getOneTimeBonus() {
        return _oneTimeBonus;
    }

    /**
     * @param timeBonus
     *            The _oneTimeBonus to set.
     */
    public void setOneTimeBonus(long timeBonus) {
        _oneTimeBonus = timeBonus;
    }

    /**
     * @return Returns the _bonusDuration.
     */
    public long getBonusDuration() {
        return _bonusDuration;
    }

    /**
     * @param duration
     *            The _bonusDuration to set.
     */
    public void setBonusDuration(long duration) {
        _bonusDuration = duration;
    }

    /**
     * @return Returns the _productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param code
     *            The _productCode to set.
     */
    public void setProductCode(String code) {
        _productCode = code;
    }

    /**
     * @return Returns the _shortCode.
     */
    public String getShortCode() {
        return _shortCode;
    }

    /**
     * @param code
     *            The _shortCode to set.
     */
    public void setShortCode(String code) {
        _shortCode = code;
    }

    public String getPointsType() {
        return _pointsType;
    }

    public void setPointsType(String type) {
        _pointsType = type;
    }

    /**
     * @return Returns the _factor.
     */
    public String getFactor() {
        return _factor;
    }

    /**
     * @param _factor
     *            The _factor to set.
     */
    public void setFactor(String _factor) {
        this._factor = _factor;
    }

    public int getLastVersion() {
        return _lastVersion;
    }

    public void setLastVersion(int version) {
        _lastVersion = version;
    }

    public Date getApplicableTo() {
        return _applicableTo;
    }

    public void setApplicableTo(Date to) {
        _applicableTo = to;
    }

    public Date getRefApplicableFrom() {
        return _refApplicableFrom;
    }

    public void setRefApplicableFrom(Date applicableFrom) {
        _refApplicableFrom = applicableFrom;
    }

    public Date getRefApplicableTo() {
        return _refApplicableTo;
    }

    public void setRefApplicableTo(Date applicableTo) {
        _refApplicableTo = applicableTo;
    }

    public String getModuleType() {
        return _moduleType;
    }

    public void setModuleType(String type) {
        _moduleType = type;
    }

    public String getPromotionType() {
        return _promotionType;
    }

    public void setPromotionType(String type) {
        _promotionType = type;
    }

    public String getPromotionTypeName() {
        return _promotionTypeName;
    }

    public void setPromotionTypeName(String typeName) {
        _promotionTypeName = typeName;
    }

    public String getReferenceBasedFlag() {
        return _referenceBasedFlag;
    }

    public void setReferenceBasedFlag(String basedFlag) {
        _referenceBasedFlag = basedFlag;
    }

    public String getOptContribution() {
        return _optContribution;
    }

    public void setOptContribution(String contribution) {
        _optContribution = contribution;
    }

    public String getPrtContribution() {
        return _prtContribution;
    }

    public void setPrtContribution(String contribution) {
        _prtContribution = contribution;
    }

    // brajesh
    private String _msgConfEnableFlag;

    public String getMsgConfEnableFlag() {
        return _msgConfEnableFlag;
    }

    public void setMsgConfEnableFlag(String str) {
        _msgConfEnableFlag = str;
    }

    // Handling of Expired LMS profile
    private String _lmsProfileExpiredFlag;

    public String getLmsProfileExpiredFlag() {
        return _lmsProfileExpiredFlag;
    }

    public void setLmsProfileExpiredFlag(String lmsProfileExpiredFlag) {
        _lmsProfileExpiredFlag = lmsProfileExpiredFlag;
    }
}
