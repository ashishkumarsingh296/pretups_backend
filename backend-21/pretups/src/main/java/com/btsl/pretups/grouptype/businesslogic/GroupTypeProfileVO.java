package com.btsl.pretups.grouptype.businesslogic;

import java.io.Serializable;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @(#)GroupTypeProfileVO.java
 *                             Copyright(c) 2006, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             Travelling object for channel user
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Ankit Zindal 11/07/2006 Initial Creation
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 */

public class GroupTypeProfileVO implements Serializable {

    private String _networkCode;
    private String _groupType;
    private String _reqGatewayType;
    private String _resGatewayType;
    private String _type;
    private long _thresholdValue;
    private String _gatewayCode;
    private String _altGatewayCode;
    private String _frequency;
    private boolean _isGroupTypeCounterReach = false;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(super.toString());
        sbf.append("_networkCode =" + _networkCode);
        sbf.append(",_groupType =" + _groupType);
        sbf.append(",_reqGatewayType =" + _reqGatewayType);
        sbf.append(",_resGatewayType=" + _resGatewayType);
        sbf.append(",_type =" + _type);
        sbf.append(",_thresholdValue=" + _thresholdValue);
        sbf.append(",_gatewayCode =" + _gatewayCode);
        sbf.append(",_altGatewayCode =" + _altGatewayCode);
        sbf.append(",_frequency=" + _frequency);
        return sbf.toString();
    }

    public String logInfo() {
        StringBuffer sbf = new StringBuffer(300);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");
        sbf.append(startSeperator);
        sbf.append("Network code");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkCode());

        sbf.append(startSeperator);
        sbf.append("Group type");
        sbf.append(middleSeperator);
        sbf.append(this.getGroupType());

        sbf.append(startSeperator);
        sbf.append("Req gateway type");
        sbf.append(middleSeperator);
        sbf.append(this.getReqGatewayType());

        sbf.append(startSeperator);
        sbf.append("Res gateway type");
        sbf.append(middleSeperator);
        sbf.append(this.getResGatewayType());

        sbf.append(startSeperator);
        sbf.append(" Type");
        sbf.append(middleSeperator);
        sbf.append(this.getType());

        sbf.append(startSeperator);
        sbf.append("Threshold value");
        sbf.append(middleSeperator);
        sbf.append(this.getThresholdValue());

        sbf.append(startSeperator);
        sbf.append("Gateway code");
        sbf.append(middleSeperator);
        sbf.append(this.getGatewayCode());

        sbf.append(startSeperator);
        sbf.append("Alt gateway code");
        sbf.append(middleSeperator);
        sbf.append(this.getAltGatewayCode());

        sbf.append(startSeperator);
        sbf.append("Frequency");
        sbf.append(middleSeperator);
        sbf.append(this.getFrequency());
        return sbf.toString();
    }

    public String differences(GroupTypeProfileVO profileVO) {

        StringBuffer sbf = new StringBuffer(300);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.isNullString(this.getNetworkCode()) && !profileVO.getNetworkCode().equals(this.getNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("Network code");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkCode());
        }

        if (!BTSLUtil.isNullString(this.getGroupType()) && !profileVO.getGroupType().equals(this.getGroupType())) {
            sbf.append(startSeperator);
            sbf.append("Group type");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getGroupType());
            sbf.append(middleSeperator);
            sbf.append(this.getGroupType());
        }

        if (!BTSLUtil.isNullString(this.getReqGatewayType()) && !profileVO.getReqGatewayType().equals(this.getReqGatewayType())) {
            sbf.append(startSeperator);
            sbf.append("Req Gateway Type");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getReqGatewayType());
            sbf.append(middleSeperator);
            sbf.append(this.getReqGatewayType());
        }

        if (!BTSLUtil.isNullString(this.getResGatewayType()) && !profileVO.getResGatewayType().equals(this.getResGatewayType())) {
            sbf.append(startSeperator);
            sbf.append("Res gateway type");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getResGatewayType());
            sbf.append(middleSeperator);
            sbf.append(this.getResGatewayType());
        }

        if (!BTSLUtil.isNullString(this.getType()) && !profileVO.getType().equals(this.getType())) {
            sbf.append(startSeperator);
            sbf.append("Type ");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getType());
            sbf.append(middleSeperator);
            sbf.append(this.getType());
        }

        if (!(profileVO.getThresholdValue() == this.getThresholdValue())) {
            sbf.append(startSeperator);
            sbf.append("Threshold value");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getThresholdValue());
            sbf.append(middleSeperator);
            sbf.append(this.getThresholdValue());
        }

        if (!BTSLUtil.isNullString(this.getGatewayCode()) && !profileVO.getGatewayCode().equals(this.getGatewayCode())) {
            sbf.append(startSeperator);
            sbf.append("Gateway code");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getGatewayCode());
            sbf.append(middleSeperator);
            sbf.append(this.getGatewayCode());
        }
        if (!BTSLUtil.isNullString(this.getAltGatewayCode()) && !profileVO.getAltGatewayCode().equals(this.getAltGatewayCode())) {
            sbf.append(startSeperator);
            sbf.append("Alt gateway code");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getAltGatewayCode());
            sbf.append(middleSeperator);
            sbf.append(this.getAltGatewayCode());
        }

        if (!BTSLUtil.isNullString(this.getFrequency()) && !profileVO.getFrequency().equals(this.getFrequency())) {
            sbf.append(startSeperator);
            sbf.append("Frequency ");
            sbf.append(middleSeperator);
            sbf.append(profileVO.getFrequency());
            sbf.append(middleSeperator);
            sbf.append(this.getFrequency());
        }
        return sbf.toString();
    }

    /**
     * @return Returns the altGatewayCode.
     */
    public String getAltGatewayCode() {
        return _altGatewayCode;
    }

    /**
     * @param altGatewayCode
     *            The altGatewayCode to set.
     */
    public void setAltGatewayCode(String altGatewayCode) {
        _altGatewayCode = altGatewayCode;
    }

    /**
     * @return Returns the frequency.
     */
    public String getFrequency() {
        return _frequency;
    }

    /**
     * @param frequency
     *            The frequency to set.
     */
    public void setFrequency(String frequency) {
        _frequency = frequency;
    }

    /**
     * @return Returns the gatewayCode.
     */
    public String getGatewayCode() {
        return _gatewayCode;
    }

    /**
     * @param gatewayCode
     *            The gatewayCode to set.
     */
    public void setGatewayCode(String gatewayCode) {
        _gatewayCode = gatewayCode;
    }

    /**
     * @return Returns the groupType.
     */
    public String getGroupType() {
        return _groupType;
    }

    /**
     * @param groupType
     *            The groupType to set.
     */
    public void setGroupType(String groupType) {
        _groupType = groupType;
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
     * @return Returns the reqGatewayType.
     */
    public String getReqGatewayType() {
        return _reqGatewayType;
    }

    /**
     * @param reqGatewayType
     *            The reqGatewayType to set.
     */
    public void setReqGatewayType(String reqGatewayType) {
        _reqGatewayType = reqGatewayType;
    }

    /**
     * @return Returns the resGatewayType.
     */
    public String getResGatewayType() {
        return _resGatewayType;
    }

    /**
     * @param resGatewayType
     *            The resGatewayType to set.
     */
    public void setResGatewayType(String resGatewayType) {
        _resGatewayType = resGatewayType;
    }

    /**
     * @return Returns the thresholdValue.
     */
    public long getThresholdValue() {
        return _thresholdValue;
    }

    /**
     * @param thresholdValue
     *            The thresholdValue to set.
     */
    public void setThresholdValue(long thresholdValue) {
        _thresholdValue = thresholdValue;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    public boolean isGroupTypeCounterReach() {
        return _isGroupTypeCounterReach;
    }

    public void setGroupTypeCounterReach(boolean isGroupTypeCounterReach) {
        _isGroupTypeCounterReach = isGroupTypeCounterReach;
    }
}
