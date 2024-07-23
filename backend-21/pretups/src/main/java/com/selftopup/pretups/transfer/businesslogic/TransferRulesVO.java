/**
 * @(#)TransferRulesVO.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          <description>
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          avinash.kamthan June 30, 2005 Initital Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 * 
 */

package com.selftopup.pretups.transfer.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * @author avinash.kamthan
 */
public class TransferRulesVO implements Serializable {
    private String _module;

    private String _networkCode;

    private String _senderSubscriberType;

    private String _receiverSubscriberType;

    private String _senderServiceClassID;

    private String _receiverServiceClassID;

    private String _cardGroupSetID;
    private String _cardGroupSetName;
    private String _cardGroupSetIDStatus;

    private String _status;
    private String _statusDesc;
    private Date _modifiedOn;
    private Timestamp _modifiedOnTimestamp;

    private String _modifiedBy;

    private Date _createdOn;

    private String _createdBy;

    // this variable is used in the jsp to select a record of the
    // TransferRulesVO which is drawn form the ArrayList of VOs.
    private String _multiBox;
    private String _rowID;// to identfy the selected record.
    // these field is to store the description of the selected item on the view
    // page
    private String _moduleDes;
    private String _senderSubscriberTypeDes;
    private String _receiverSubscriberTypeDes;
    private String _senderServiceClassIDDes;
    private String _receiverServiceClassIDDes;
    private String _cardGroupSetIDDes;
    private long _lastModifiedTime = 0;
    private String _cardGroupMessage1;
    private String _cardGroupMessage2;
    private String _subServiceTypeId;
    private String _subServiceTypeIdDes;
    private String _serviceType;
    private String _serviceTypeDes;
    private Date _startTime = null;
    private Date _endTime = null;
    private String _ruleType = null;
    private String _ruleLevel = null;

    // Added by varun

    private String _fromDate;
    private String _tillDate;
    private String _fromTime;
    private String _tillTime;

    // added by ranjana
    private String _selectRangeType;

    private String _multipleSlab;
    private ArrayList _timeSlabList = null;
    // added by vikas kumar
    private String _isModify;
    // added by rahul Prefix based promotion trf rule change
    private String _allowedDays = null;
    private String _allowedSeries = null;
    private String _deniedSeries = null;

    // added by arvinder for allowed days,allowed and disallowed prefixes
    private ArrayList _allowdDaysList;
    // private String _allowedDays;
    private String[] _mdaysSelected;
    private String[] _daysSelected;
    private String _mallowedDays;
    private String _prefixSeries;
    private String _prefixType;

    // added for promotional transfer rul efor cell group
    private String _subscriberStatus;
    private String _combinedKey;
    private String _serviceGroupCode;
    private String _serviceProviderGpDes;
    private String _subscriberStatusDesc;
    private String _spGroupId;
    private String _promotionCode;

    public String getPrefixType() {
        return _prefixType;
    }

    public void setPrefixType(String type) {
        _prefixType = type;
    }

    public String getPrefixSeries() {
        return _prefixSeries;
    }

    public void setPrefixSeries(String series) {
        _prefixSeries = series;
    }

    public String getMallowedDays() {
        return _mallowedDays;
    }

    public void setMallowedDays(String days) {
        _mallowedDays = days;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    public String getMultiBox() {
        return _multiBox;
    }

    public void setMultiBox(String multiBox) {
        _multiBox = multiBox;
    }

    public String getCardGroupSetID() {
        return _cardGroupSetID;
    }

    public void setCardGroupSetID(String cardGroupSetID) {
        _cardGroupSetID = cardGroupSetID;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getModule() {
        return _module;
    }

    public void setModule(String module) {
        _module = module;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getReceiverServiceClassID() {
        return _receiverServiceClassID;
    }

    public void setReceiverServiceClassID(String receiverServiceClassID) {
        _receiverServiceClassID = receiverServiceClassID;
    }

    public String getReceiverSubscriberType() {
        return _receiverSubscriberType;
    }

    public void setReceiverSubscriberType(String receiverSubscriberType) {
        _receiverSubscriberType = receiverSubscriberType;
    }

    public String getSenderServiceClassID() {
        return _senderServiceClassID;
    }

    public void setSenderServiceClassID(String senderServiceClassID) {
        _senderServiceClassID = senderServiceClassID;
    }

    public String getSenderSubscriberType() {
        return _senderSubscriberType;
    }

    public void setSenderSubscriberType(String senderSubscriberType) {
        _senderSubscriberType = senderSubscriberType;
    }

    public boolean equals(TransferRulesVO rulesVO) {
        boolean flag = false;

        if (rulesVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }

    public String differences(TransferRulesVO rulesVO) {

        StringBuffer sbf = new StringBuffer(10);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.isNullString(this.getCardGroupSetID()) && !this.getCardGroupSetID().equals(rulesVO.getCardGroupSetID())) {
            sbf.append(startSeperator);
            sbf.append("Card Group Set ID");
            sbf.append(middleSeperator);
            sbf.append(rulesVO.getCardGroupSetID());
            sbf.append(middleSeperator);
            sbf.append(this.getCardGroupSetID());
        }

        if (!BTSLUtil.isNullString(this.getCardGroupSetIDStatus()) && !this.getCardGroupSetIDStatus().equals(rulesVO.getCardGroupSetIDStatus())) {
            sbf.append(startSeperator);
            sbf.append("Card Group Set ID Status");
            sbf.append(middleSeperator);
            sbf.append(rulesVO.getCardGroupSetIDStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getCardGroupSetIDStatus());
        }

        if (!BTSLUtil.isNullString(this.getCardGroupMessage1()) && !this.getCardGroupMessage1().equalsIgnoreCase(rulesVO.getCardGroupMessage1())) {
            sbf.append(startSeperator);
            sbf.append("Card Group Set ID Message 1");
            sbf.append(middleSeperator);
            sbf.append(rulesVO.getCardGroupMessage1());
            sbf.append(middleSeperator);
            sbf.append(this.getCardGroupMessage1());
        }

        if (!BTSLUtil.isNullString(this.getCardGroupMessage2()) && !this.getCardGroupMessage2().equalsIgnoreCase(rulesVO.getCardGroupMessage2())) {
            sbf.append(startSeperator);
            sbf.append("Card Group Set ID Message 2");
            sbf.append(middleSeperator);
            sbf.append(rulesVO.getCardGroupMessage2());
            sbf.append(middleSeperator);
            sbf.append(this.getCardGroupMessage2());
        }

        return sbf.toString();
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(10);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Card Group Set ID");
        sbf.append(middleSeperator);
        sbf.append(this.getCardGroupSetID());

        sbf.append(startSeperator);
        sbf.append("Card Group Set ID Status");
        sbf.append(middleSeperator);
        sbf.append(this.getCardGroupSetIDStatus());

        sbf.append(startSeperator);
        sbf.append("Card Group Set ID Message 1");
        sbf.append(middleSeperator);
        sbf.append(this.getCardGroupMessage1());

        sbf.append(startSeperator);
        sbf.append("Card Group Set ID Message 2");
        sbf.append(middleSeperator);
        sbf.append(this.getCardGroupMessage2());

        return sbf.toString();
    }

    /**
     * @return
     */
    public String getKey() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(this.getServiceType());
        sbf.append("_");
        sbf.append(this.getModule());
        sbf.append("_");
        sbf.append(this.getNetworkCode());
        sbf.append("_");
        sbf.append(this.getSenderSubscriberType());
        sbf.append("_");
        sbf.append(this.getReceiverSubscriberType());
        sbf.append("_");
        sbf.append(this.getSenderServiceClassID());
        sbf.append("_");
        sbf.append(this.getReceiverServiceClassID());
        sbf.append("_");
        sbf.append(this.getSubServiceTypeId());
        sbf.append("_");
        sbf.append(this.getRuleLevel());
        if (SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
            sbf.append("_");
            sbf.append(this.getServiceGroupCode());
            sbf.append("_");
            sbf.append(this.getSubscriberStatus());
        }
        return sbf.toString();
    }

    public String getCardGroupSetIDDes() {
        return _cardGroupSetIDDes;
    }

    public void setCardGroupSetIDDes(String cardGroupSetIDDes) {
        _cardGroupSetIDDes = cardGroupSetIDDes;
    }

    public String getModuleDes() {
        return _moduleDes;
    }

    public void setModuleDes(String moduleDes) {
        _moduleDes = moduleDes;
    }

    public String getReceiverServiceClassIDDes() {
        return _receiverServiceClassIDDes;
    }

    public void setReceiverServiceClassIDDes(String receiverServiceClassIDDes) {
        _receiverServiceClassIDDes = receiverServiceClassIDDes;
    }

    public String getReceiverSubscriberTypeDes() {
        return _receiverSubscriberTypeDes;
    }

    public void setReceiverSubscriberTypeDes(String receiverSubscriberTypeDes) {
        _receiverSubscriberTypeDes = receiverSubscriberTypeDes;
    }

    public String getSenderServiceClassIDDes() {
        return _senderServiceClassIDDes;
    }

    public void setSenderServiceClassIDDes(String senderServiceClassIDDes) {
        _senderServiceClassIDDes = senderServiceClassIDDes;
    }

    public String getSenderSubscriberTypeDes() {
        return _senderSubscriberTypeDes;
    }

    public void setSenderSubscriberTypeDes(String senderSubscriberTypeDes) {
        _senderSubscriberTypeDes = senderSubscriberTypeDes;
    }

    public String getRowID() {
        return _rowID;
    }

    public void setRowID(String rowID) {
        _rowID = rowID;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_cardGroupSetID=" + _cardGroupSetID);
        sbf.append("_cardGroupSetIDStatus=" + _cardGroupSetIDStatus);
        sbf.append(",_createdBy=" + _createdBy);
        sbf.append(",_createdOn=" + _createdOn);
        sbf.append(",_modifiedBy=" + _modifiedBy);
        sbf.append(",_modifiedOn=" + _modifiedOn);
        sbf.append(",_module=" + _module);
        sbf.append(",_networkCode=" + _networkCode);
        sbf.append(",_receiverServiceClassID=" + _receiverServiceClassID);
        sbf.append(",_receiverSubscriberType=" + _receiverSubscriberType);
        sbf.append(",_senderServiceClassID=" + _senderServiceClassID);
        sbf.append(",_senderSubscriberType=" + _senderSubscriberType);
        sbf.append(",_senderSubscriberType=" + _subServiceTypeId);
        sbf.append(",_rowID=" + _rowID);
        sbf.append(",_status=" + _status);
        sbf.append(",_serviceType=" + _serviceType);
        sbf.append(",_startTime=" + _startTime);
        sbf.append(",_endTime=" + _endTime);
        sbf.append(",_ruleType=" + _ruleType);
        sbf.append(",_ruleLevel=" + _ruleLevel);
        sbf.append(",_multipleSlab=" + _multipleSlab);
        return sbf.toString();
    }

    public Timestamp getModifiedOnTimestamp() {
        return _modifiedOnTimestamp;
    }

    public void setModifiedOnTimestamp(Timestamp modifiedOnTimestamp) {
        _modifiedOnTimestamp = modifiedOnTimestamp;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getStatusDesc() {
        return _statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    public String getCardGroupSetIDStatus() {
        return _cardGroupSetIDStatus;
    }

    public void setCardGroupSetIDStatus(String cardGroupSetIDStatus) {
        _cardGroupSetIDStatus = cardGroupSetIDStatus;
    }

    public String getCardGroupMessage1() {
        return _cardGroupMessage1;
    }

    public void setCardGroupMessage1(String cardGroupMessage1) {
        _cardGroupMessage1 = cardGroupMessage1;
    }

    public String getCardGroupMessage2() {
        return _cardGroupMessage2;
    }

    public void setCardGroupMessage2(String cardGroupMessage2) {
        _cardGroupMessage2 = cardGroupMessage2;
    }

    public String getCardGroupSetName() {
        return _cardGroupSetName;
    }

    public void setCardGroupSetName(String cardGroupSetName) {
        _cardGroupSetName = cardGroupSetName;
    }

    public String getSubServiceTypeId() {
        return _subServiceTypeId;
    }

    public void setSubServiceTypeId(String subServiceTypeId) {
        _subServiceTypeId = subServiceTypeId;
    }

    public String getSubServiceTypeIdDes() {
        return _subServiceTypeIdDes;
    }

    public void setSubServiceTypeIdDes(String subServiceTypeIdDes) {
        _subServiceTypeIdDes = subServiceTypeIdDes;
    }

    public String getFromDate() {
        return _fromDate;
    }

    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    public String getFromTime() {
        return _fromTime;
    }

    public void setFromTime(String fromTime) {
        _fromTime = fromTime;
    }

    public String getTillDate() {
        return _tillDate;
    }

    public void setTillDate(String tillDate) {
        _tillDate = tillDate;
    }

    public String getTillTime() {
        return _tillTime;
    }

    public void setTillTime(String tillTime) {
        _tillTime = tillTime;
    }

    public String getServiceTypeDes() {
        return _serviceTypeDes;
    }

    public void setServiceTypeDes(String type) {
        _serviceTypeDes = type;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String type) {
        _serviceType = type;
    }

    /**
     * @return Returns the endTime.
     */
    public Date getEndTime() {
        return _endTime;
    }

    /**
     * @param endTime
     *            The endTime to set.
     */
    public void setEndTime(Date endTime) {
        _endTime = endTime;
    }

    /**
     * @return Returns the ruleLevel.
     */
    public String getRuleLevel() {
        return _ruleLevel;
    }

    /**
     * @param ruleLevel
     *            The ruleLevel to set.
     */
    public void setRuleLevel(String ruleLevel) {
        _ruleLevel = ruleLevel;
    }

    /**
     * @return Returns the ruleType.
     */
    public String getRuleType() {
        return _ruleType;
    }

    /**
     * @param ruleType
     *            The ruleType to set.
     */
    public void setRuleType(String ruleType) {
        _ruleType = ruleType;
    }

    /**
     * @return Returns the startTime.
     */
    public Date getStartTime() {
        return _startTime;
    }

    /**
     * @param startTime
     *            The startTime to set.
     */
    public void setStartTime(Date startTime) {
        _startTime = startTime;
    }

    /**
     * @return Returns the selectRangeType.
     */
    public String getSelectRangeType() {
        return _selectRangeType;
    }

    /**
     * @param selectRangeType
     *            The selectRangeType to set.
     */
    public void setSelectRangeType(String selectRangeType) {
        _selectRangeType = selectRangeType;
    }

    /**
     * @return Returns the multipleSlab.
     */
    public String getMultipleSlab() {
        return _multipleSlab;
    }

    /**
     * @param multipleSlab
     *            The multipleSlab to set.
     */
    public void setMultipleSlab(String multipleSlab) {
        _multipleSlab = multipleSlab;
    }

    /**
     * @return Returns the timeSlabpromotionalRulesList.
     */
    public ArrayList getTimeSlabList() {
        return _timeSlabList;
    }

    /**
     * @param timeSlabpromotionalRulesList
     *            The timeSlabpromotionalRulesList to set.
     */
    public void setTimeSlabList(ArrayList timeSlabList) {
        _timeSlabList = timeSlabList;
    }

    /**
     * @return
     */
    public String getIsModify() {
        return _isModify;
    }

    /**
     * @param modify
     */
    public void setIsModify(String modify) {
        _isModify = modify;
    }

    /**
     * @return the _allowedDays
     */
    public String getAllowedDays() {
        return _allowedDays;
    }

    /**
     * @param days
     *            the _allowedDays to set
     */
    public void setAllowedDays(String days) {
        _allowedDays = days;
    }

    /**
     * @return the _allowedSeries
     */
    public String getAllowedSeries() {
        return _allowedSeries;
    }

    /**
     * @param series
     *            the _allowedSeries to set
     */
    public void setAllowedSeries(String series) {
        _allowedSeries = series;
    }

    /**
     * @return the _deniedSeries
     */

    /**
     * @param series
     *            the _deniedSeries to set
     */
    public void setDeniedSeries(String series) {
        _deniedSeries = series;
    }

    public ArrayList getAllowdDaysList() {
        return _allowdDaysList;
    }

    public void setAllowdDaysList(ArrayList daysList) {
        _allowdDaysList = daysList;
    }

    public String[] getDaysSelected() {
        return _daysSelected;
    }

    public void setDaysSelected(String[] selected) {
        _daysSelected = selected;
    }

    public String getDeniedSeries() {
        return _deniedSeries;
    }

    public String[] getMdaysSelected() {
        return _mdaysSelected;
    }

    public void setMdaysSelected(String[] selected) {
        _mdaysSelected = selected;
    }

    public String getSubscriberStatus() {
        return _subscriberStatus;
    }

    public void setSubscriberStatus(String status) {
        _subscriberStatus = status;
    }

    public String getCombinedKey() {
        return _serviceType + ":" + _subscriberStatus;
    }

    public void setCombinedKey(String key) {
        _combinedKey = key;
    }

    public String getServiceGroupCode() {
        return _serviceGroupCode;
    }

    public void setServiceGroupCode(String groupCode) {
        _serviceGroupCode = groupCode;
    }

    public String getServiceProviderGpDes() {
        return _serviceProviderGpDes;
    }

    public void setServiceProviderGpDes(String providerGpDes) {
        _serviceProviderGpDes = providerGpDes;
    }

    public String getSubscriberStatusDesc() {
        return _subscriberStatusDesc;
    }

    public void setSubscriberStatusDesc(String statusDesc) {
        _subscriberStatusDesc = statusDesc;
    }

    public String getSpGroupId() {
        return _spGroupId;
    }

    public void setSpGroupId(String groupId) {
        _spGroupId = groupId;
    }

    public String getPromotionCode() {
        return _promotionCode;
    }

    public void setPromotionCode(String code) {
        _promotionCode = code;
    }
}
