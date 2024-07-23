package com.btsl.pretups.channel.profile.businesslogic;

/**
 * @(#)ProfileSetDetailsVO.java
 *                              Copyright(c) 2008, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              This class refers to the Profile details in a
 *                              profile set version.
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              ankit.singhal 09/02/2009 Initital Creation
 *                              rahul.dutt 16/02/09 modified
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 */
import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.util.Constants;

public class ProfileSetDetailsVO implements Serializable {
    private String _setId;
    private String _version;
    private String _detailId;
    private String _type;
    private String _userType;
    private String _detailType;
    private String _detailSubType;
    private String _periodId;
    private String _productCode;
    private long _startRange;
    private long _endRange;
    private long _points;
    private long _minLimit;
    private long _maxLimit;

    // added by rahul
    private String _startRangeAsString = null;
    private String _endRangeAsString = null;
    private String _factorAsString = null;
    private String _pointsAsString = null;
    private int rowIndex;
    private String _productCodeDesc;
    private String _serviceCode;
    private String _subscriberType;
    private String _serviceTypeName;
    private String _periodType;
    private String _subscriberTypeName;
    private int _slabNo;
    // for points type
    private ArrayList _pointsTypeList;
    private String _pointsTypeCode;
    private String _profileType = null;

    /**
     * toString() method writes the parameters value to the console or log
     * 
     * @return String
     */
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
        sb.append("_points=" + _points + ",");
        sb.append("_minLimit=" + _minLimit + ",");
        sb.append("_maxLimit=" + _maxLimit + ",");
        sb.append("_profileType=" + _profileType + ",");
        sb.append("_startRangeAsString=" + _startRangeAsString + ",");
        sb.append("_endRangeAsString=" + _endRangeAsString + ",");
        sb.append("_factorAsString=" + _factorAsString + ",");
        sb.append("_pointsAsString=" + _pointsAsString + ",");
        sb.append("rowIndex=" + rowIndex + ",");
        sb.append("_productCodeDesc=" + _productCodeDesc + ",");
        sb.append("_serviceCode=" + _serviceCode + ",");
        sb.append("_subscriberType=" + _subscriberType + ",");
        sb.append("_serviceTypeName=" + _serviceTypeName + ",");
        sb.append("_periodType=" + _periodType + ",");
        sb.append("_subscriberTypeName=" + _subscriberTypeName + ",");
        sb.append("_slabNo=" + _slabNo + ",");
        sb.append("_pointsTypeList=" + _pointsTypeList + ",");
        sb.append("_pointsTypeCode=" + _pointsTypeCode + ",");
        return sb.toString();
    }

    public ProfileSetDetailsVO() {
    }

    public ProfileSetDetailsVO(
                    ProfileSetDetailsVO profileSetDetailsVO) {
        this._startRange = profileSetDetailsVO._startRange;
        this._startRangeAsString = profileSetDetailsVO._startRangeAsString;
        this._endRange = profileSetDetailsVO._endRange;
        this._endRangeAsString = profileSetDetailsVO._endRangeAsString;
        this._factorAsString = profileSetDetailsVO._factorAsString;
        this._points = profileSetDetailsVO._points;
        this._pointsAsString = profileSetDetailsVO._pointsAsString;
        this.rowIndex = profileSetDetailsVO.rowIndex;
    }

    /**
     * This method is used to log information.
     * 
     * @return String
     */
    public String logInfo() {
        final StringBuffer sbf = new StringBuffer(10);

        final String startSeperator = Constants.getProperty("startSeperatpr");
        final String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator + "Set ID" + middleSeperator + this.getSetId());
        sbf.append(startSeperator + "Version" + middleSeperator + this.getVersion());
        sbf.append(startSeperator + "Detail Id" + middleSeperator + this.getDetailId());
        sbf.append(startSeperator + "Type" + middleSeperator + this.getType());
        sbf.append(startSeperator + "User type" + middleSeperator + this.getUserType());
        sbf.append(startSeperator + "Detail type" + middleSeperator + this.getDetailType());
        sbf.append(startSeperator + "Detail subtype" + middleSeperator + this.getDetailSubType());
        sbf.append(startSeperator + "Period id" + middleSeperator + this.getPeriodId());
        sbf.append(startSeperator + "Product code" + middleSeperator + this.getProductCode());
        sbf.append(startSeperator + "Start range" + middleSeperator + this.getStartRange());
        sbf.append(startSeperator + "End range" + middleSeperator + this.getEndRange());
        sbf.append(startSeperator + "Points" + middleSeperator + this.getPoints());
        sbf.append(startSeperator + "Min limit" + middleSeperator + this.getMinLimit());
        sbf.append(startSeperator + "Max limit" + middleSeperator + this.getMaxLimit());

        return sbf.toString();
    }

    public String getDetailId() {
        return _detailId;
    }

    public void setDetailId(String detailId) {
        _detailId = detailId;
    }

    public String getDetailSubType() {
        return _detailSubType;
    }

    public void setDetailSubType(String detailSubType) {
        _detailSubType = detailSubType;
    }

    public String getDetailType() {
        return _detailType;
    }

    public void setDetailType(String detailType) {
        _detailType = detailType;
    }

    public long getEndRange() {
        return _endRange;
    }

    public void setEndRange(long endRange) {
        _endRange = endRange;
    }

    public long getMaxLimit() {
        return _maxLimit;
    }

    public void setMaxLimit(long maxLimit) {
        _maxLimit = maxLimit;
    }

    public long getMinLimit() {
        return _minLimit;
    }

    public void setMinLimit(long minLimit) {
        _minLimit = minLimit;
    }

    public String getPeriodId() {
        return _periodId;
    }

    public void setPeriodId(String periodId) {
        _periodId = periodId;
    }

    public long getPoints() {
        return _points;
    }

    public void setPoints(long points) {
        _points = points;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getSetId() {
        return _setId;
    }

    public void setSetId(String setId) {
        _setId = setId;
    }

    public long getStartRange() {
        return _startRange;
    }

    public void setStartRange(long startRange) {
        _startRange = startRange;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getUserType() {
        return _userType;
    }

    public void setUserType(String userType) {
        _userType = userType;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public String getEndRangeAsString() {
        return _endRangeAsString;
    }

    public void setEndRangeAsString(String endRangeAsString) {
        if (endRangeAsString != null) {
            _endRangeAsString = endRangeAsString.trim();
        }
    }

    public String getStartRangeAsString() {
        return _startRangeAsString;
    }

    public void setStartRangeAsString(String startRangeAsString) {
        if (startRangeAsString != null) {
            _startRangeAsString = startRangeAsString.trim();
        }
    }

    public String getPointsAsString() {
        return _pointsAsString;
    }

    public void setPointsAsString(String asString) {
        _pointsAsString = asString;
    }

    public String getFactorAsString() {
        return _factorAsString;
    }

    public void setFactorAsString(String asString) {
        _factorAsString = asString;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     *            The rowIndex to set.
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getProductCodeDesc() {
        return _productCodeDesc;
    }

    public void setProductCodeDesc(String codeDesc) {
        _productCodeDesc = codeDesc;
    }

    public String getServiceCode() {
        return _serviceCode;
    }

    public void setServiceCode(String code) {
        _serviceCode = code;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String type) {
        _subscriberType = type;
    }

    public String getServiceTypeName() {
        return _serviceTypeName;
    }

    public void setServiceTypeName(String typeName) {
        _serviceTypeName = typeName;
    }

    public String getPeriodType() {
        return _periodType;
    }

    public void setPeriodType(String type) {
        _periodType = type;
    }

    public String getSubscriberTypeName() {
        return _subscriberTypeName;
    }

    public void setSubscriberTypeName(String typeName) {
        _subscriberTypeName = typeName;
    }

    public int getSlabNo() {
        return _slabNo;
    }

    public void setSlabNo(int no) {
        _slabNo = no;
    }

    public String getPointsTypeCode() {
        return _pointsTypeCode;
    }

    public void setPointsTypeCode(String typeCode) {
        _pointsTypeCode = typeCode;
    }

    public ArrayList getPointsTypeList() {
        return _pointsTypeList;
    }

    public void setPointsTypeList(ArrayList typeList) {
        this._pointsTypeList = typeList;
    }

    /**
     * @return Returns the profileType.
     */
    public String getProfileType() {
        return _profileType;
    }

    /**
     * @param profileType
     *            The profileType to set.
     */
    public void setProfileType(String profileType) {
        _profileType = profileType;
    }
}
