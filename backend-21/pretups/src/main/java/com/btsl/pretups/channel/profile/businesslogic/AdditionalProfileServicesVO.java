package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.util.PretupsBL;

/**
 * @(#)AdditionalProfileServicesVO.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Mohit Goel 24/08/2005 Initial Creation
 *                                      Samna Soin 19/10/2011 Modified
 * 
 */
public class AdditionalProfileServicesVO implements Serializable {
    private Log _log = LogFactory.getLog(AdditionalProfileServicesVO.class.getName());
    private String _commProfileServiceTypeID;
    private String _commProfileSetID;
    private String _commProfileSetVersion;
    private long _minTransferValue;
    private long _maxTransferValue;
    private String _serviceType;
    private String _serviceTypeDesc;
    private String _addtnlComStatus;
    private String _addtnlComStatusName;
    private String _subServiceDesc;
    private String _subServiceCode;
    private String __additionalCommissionTimeSlab;
    private String _gatewayCode;
    private String _applicableFromAdditional;
    private String _applicableToAdditional;

    public AdditionalProfileServicesVO() {
    };

    public AdditionalProfileServicesVO(
                    AdditionalProfileServicesVO additionalProfileServicesVO) {
        this._commProfileServiceTypeID = additionalProfileServicesVO._commProfileServiceTypeID;
        this._commProfileSetID = additionalProfileServicesVO._commProfileSetID;
        this._commProfileSetVersion = additionalProfileServicesVO._commProfileSetVersion;
        this._minTransferValue = additionalProfileServicesVO._minTransferValue;
        this._maxTransferValue = additionalProfileServicesVO._maxTransferValue;
        this._serviceType = additionalProfileServicesVO._serviceType;
        this._serviceTypeDesc = additionalProfileServicesVO._serviceTypeDesc;
        this._addtnlComStatus = additionalProfileServicesVO._addtnlComStatus;
        this._addtnlComStatusName = additionalProfileServicesVO._addtnlComStatusName;
        this._subServiceDesc = additionalProfileServicesVO._subServiceDesc;
        this._subServiceCode = additionalProfileServicesVO._subServiceCode;
        this._gatewayCode = additionalProfileServicesVO._gatewayCode;
        this.__additionalCommissionTimeSlab = additionalProfileServicesVO.__additionalCommissionTimeSlab;
        this._applicableFromAdditional = additionalProfileServicesVO._applicableFromAdditional;
        this._applicableToAdditional = additionalProfileServicesVO._applicableToAdditional;
    }
@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommissionProfileProductsVO Data ");

        sb.append("_commProfileServiceTypeID=" + _commProfileServiceTypeID + ",");
        sb.append("_commProfileSetID=" + _commProfileSetID + ",");
        sb.append("_commProfileSetVersion=" + _commProfileSetVersion + ",");
        sb.append("_minTransferValue=" + _minTransferValue + ",");
        sb.append("_maxTransferValue=" + _maxTransferValue + ",");
        sb.append("_serviceType=" + _serviceType + ",");
        sb.append("_serviceTypeDesc=" + _serviceTypeDesc + ",");
        sb.append("_addtnlComStatus=" + _addtnlComStatus + ",");
        sb.append("_addtnlComStatusName=" + _addtnlComStatusName + ",");
        sb.append("_selectorDesc=" + _subServiceDesc + ",");
        sb.append("_selectorCode=" + _subServiceCode + ",");
        sb.append("_gatewayCode=" + _gatewayCode + ",");
        sb.append("__additionalCommissionTimeSlab=" + __additionalCommissionTimeSlab + ",");
        return sb.toString();
    }

    public long getMaxTransferValue() {
        return _maxTransferValue;
    }

    public void setMaxTransferValue(long maxTransferValue) {
        _maxTransferValue = maxTransferValue;
    }

    public String getMaxTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_maxTransferValue);
    }

    public void setMaxTransferValueAsString(String maxTransferValue) {
        final String METHOD_NAME = "setMaxTransferValueAsString";
        try {
            _maxTransferValue = PretupsBL.getSystemAmount(maxTransferValue);
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public long getMinTransferValue() {
        return _minTransferValue;
    }

    public void setMinTransferValue(long minTransferValue) {
        _minTransferValue = minTransferValue;
    }

    public String getMinTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_minTransferValue);
    }

    public void setMinTransferValueAsString(String minTransferValue) {
        final String METHOD_NAME = "setMinTransferValueAsString";
        try {
            _minTransferValue = PretupsBL.getSystemAmount(minTransferValue);
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * @return Returns the commProfileSetID.
     */
    public String getCommProfileSetID() {
        return _commProfileSetID;
    }

    /**
     * @param commProfileSetID
     *            The commProfileSetID to set.
     */
    public void setCommProfileSetID(String commProfileSetID) {
        _commProfileSetID = commProfileSetID;
    }

    /**
     * @return Returns the commProfileServiceTypeID.
     */
    public String getCommProfileServiceTypeID() {
        return _commProfileServiceTypeID;
    }

    /**
     * @param commProfileServiceTypeID
     *            The commProfileServiceTypeID to set.
     */
    public void setCommProfileServiceTypeID(String commProfileServiceTypeID) {
        _commProfileServiceTypeID = commProfileServiceTypeID;
    }

    /**
     * @return Returns the commProfileSetVersion.
     */
    public String getCommProfileSetVersion() {
        return _commProfileSetVersion;
    }

    /**
     * @param commProfileSetVersion
     *            The commProfileSetVersion to set.
     */
    public void setCommProfileSetVersion(String commProfileSetVersion) {
        _commProfileSetVersion = commProfileSetVersion;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the serviceTypeDesc.
     */
    public String getServiceTypeDesc() {
        return _serviceTypeDesc;
    }

    /**
     * @param serviceTypeDesc
     *            The serviceTypeDesc to set.
     */
    public void setServiceTypeDesc(String serviceTypeDesc) {
        _serviceTypeDesc = serviceTypeDesc;
    }

    public String getAddtnlComStatus() {
        return _addtnlComStatus;
    }

    public void setAddtnlComStatus(String addtnlComStatus) {
        _addtnlComStatus = addtnlComStatus;
    }

    public String getAddtnlComStatusName() {
        return _addtnlComStatusName;
    }

    public void setAddtnlComStatusName(String addtnlComStatusName) {
        _addtnlComStatusName = addtnlComStatusName;
    }

    public String getSubServiceDesc() {
        return _subServiceDesc;
    }

    public void setSubServiceDesc(String subServiceDesc) {
        _subServiceDesc = subServiceDesc;
    }

    public String getSubServiceCode() {
        return _subServiceCode;
    }

    public void setSubServiceCode(String subServiceCode) {
        _subServiceCode = subServiceCode;
    }

    public String getAdditionalCommissionTimeSlab() {
        return __additionalCommissionTimeSlab;
    }

    public void setAdditionalCommissionTimeSlab(String time) {
        __additionalCommissionTimeSlab = time;
    }

    public String getGatewayCode() {
        return _gatewayCode;
    }

    public void setGatewayCode(String gwcode) {
        _gatewayCode = gwcode;
    }

    public String getApplicableFromAdditional() {
        return _applicableFromAdditional;
    }

    public void setApplicableFromAdditional(String todate) {
        _applicableFromAdditional = todate;
    }

    public String getApplicableToAdditional() {
        return _applicableToAdditional;
    }

    public void setApplicableToAdditional(String todate) {
        _applicableToAdditional = todate;
    }
    
    public static AdditionalProfileServicesVO getInstance(){
    	return new AdditionalProfileServicesVO();
    }
}
