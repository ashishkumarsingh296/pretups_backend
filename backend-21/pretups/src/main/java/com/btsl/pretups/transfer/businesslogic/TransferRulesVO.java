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

package com.btsl.pretups.transfer.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author avinash.kamthan
 */
public class TransferRulesVO implements Serializable {
  
	private static final long serialVersionUID = 1L;

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

	 private String _dateRange;
    private String _gatewayCode;
    private String cellGroupId;
    private String cellGroupIdDesc;
    private String applicableFrom;
    private String applicableTO;

    public String getCellGroupIdDesc() {
		return cellGroupIdDesc;
	}

	public void setCellGroupIdDesc(String cellGroupIdDesc) {
		this.cellGroupIdDesc = cellGroupIdDesc;
	}

	private String _gatewayCodeDes;
    public String getCellGroupId() {
		return cellGroupId;
	}

	public void setCellGroupId(String cellGroupId) {
		this.cellGroupId = cellGroupId;
	}

	private String _gradeCode;
    private String _categoryCode;
    private String _gradeCodeDes;
    private String _categoryCodeDes;
    private String _domainCode;
    private String _domainCode1;
    private String _gradeCode1;
    private String _categoryCode1;
	
	private String _modify;
    private String _error;

    public String getGatewayCode() {
        return _gatewayCode;
    }

    public void setGatewayCode(String code) {
        _gatewayCode = code;
    }

    public String getGatewayCodeDes() {
        return _gatewayCodeDes;
    }

    public void setGatewayCodeDes(String code) {
        _gatewayCodeDes = code;
    }

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

    public boolean equalsTransferRulesVO(TransferRulesVO rulesVO) {
        boolean flag = false;

        if (rulesVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }
@Override
    public native int hashCode();

    public String differences(TransferRulesVO rulesVO) {

        final StringBuilder sbf = new StringBuilder();

        final String startSeperator = Constants.getProperty("startSeperatpr");
        final String middleSeperator = Constants.getProperty("middleSeperator");

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

@Override
public native boolean equals(Object obj);

	public String logInfo() {

        final StringBuilder sbf = new StringBuilder();

        final String startSeperator = Constants.getProperty("startSeperatpr");
        final String middleSeperator = Constants.getProperty("middleSeperator");

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
        final StringBuilder sbf = new StringBuilder();
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
        if(PretupsI.C2S_MODULE.equals(this.getModule()))
        		{
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
            sbf.append("_");
            sbf.append(this.getServiceGroupCode());
            sbf.append("_");
            sbf.append(this.getSubscriberStatus());
        }
        		}
		if(!PretupsI.TRANSFER_RULE_PROMOTIONAL.equals(this.getRuleType())){
        sbf.append("_");
        sbf.append(this.getGatewayCode());
        }
		 if(PretupsI.P2P_MODULE.equals(this.getModule()) && PretupsI.TRANSFER_RULE_PROMOTIONAL.equals(this.getRuleType())) {
			 sbf.append("_");
		        sbf.append(this.getGatewayCode());
		 }
		
        if(PretupsI.C2S_MODULE.equals(this.getModule()) && !PretupsI.TRANSFER_RULE_PROMOTIONAL.equals(this.getRuleType())) {
        	sbf.append("_");
            sbf.append(this.getCategoryCode());
            sbf.append("_");
            sbf.append(this.getGradeCode());
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
@Override
    public String toString() {
        final StringBuilder sbl = new StringBuilder();
        sbl.append("_cardGroupSetID=").append(_cardGroupSetID);
        sbl.append("_cardGroupSetIDStatus=").append(_cardGroupSetIDStatus);
        sbl.append(",_createdBy=").append(_createdBy);
        sbl.append(",_createdOn=").append(_createdOn);
        sbl.append(",_modifiedBy=").append(_modifiedBy);
        sbl.append(",_modifiedOn=").append(_modifiedOn);
        sbl.append(",_module=").append(_module);
        sbl.append(",_networkCode=").append(_networkCode);
        sbl.append(",_receiverServiceClassID=").append(_receiverServiceClassID);
        sbl.append(",_receiverSubscriberType=").append(_receiverSubscriberType);
        sbl.append(",_senderServiceClassID=").append(_senderServiceClassID);
        sbl.append(",_senderSubscriberType=").append(_senderSubscriberType);
        sbl.append(",_senderSubscriberType=").append(_subServiceTypeId);
        sbl.append(",_rowID=").append(_rowID);
        sbl.append(",_status=").append(_status);
        sbl.append(",_serviceType=").append(_serviceType);
        sbl.append(",_startTime=").append(_startTime);
        sbl.append(",_endTime=").append(_endTime);
        sbl.append(",_ruleType=").append(_ruleType);
        sbl.append(",_ruleLevel=").append(_ruleLevel);
        sbl.append(",_multipleSlab=").append(_multipleSlab);
        sbl.append(",_gatewayCode=").append(_gatewayCode);
        sbl.append(",_cellGroupId=").append(cellGroupId);
        sbl.append(",_gradeCode1=").append(_gradeCode1);
        sbl.append(",_gradeCode=").append(_gradeCode);
        sbl.append(",_categoryCode=").append(_categoryCode);
        sbl.append(",_categoryCode1=").append(_categoryCode1);
        sbl.append(",_categoryCodeDes=").append(_categoryCodeDes);
        sbl.append(",_gradeCodeDes=").append(_gradeCodeDes);
        sbl.append(",_subServiceTypeIdDes=").append(_subServiceTypeIdDes);
        sbl.append(",_subServiceTypeId=").append(_subServiceTypeId);
        
        return sbl.toString();
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
    public String getGradeCode() {
        return _gradeCode;
    }
    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }
    public String getCategoryCode() {
        return _categoryCode;
    }
    public void setCategoryCode(String code) {
        _categoryCode = code;
    }
    public String getGradeCodeDes() {
        return _gradeCodeDes;
    }
    public void setGradeCodeDes(String gradeCode) {
    	_gradeCodeDes = gradeCode;
    }
    public String getCategoryCodeDes() {
        return _categoryCodeDes;
    }
    public void setCategoryCodeDes(String code) {
    	_categoryCodeDes = code;
    }
    public String getDomainCode() {
        return _domainCode;
    }
    public void setDomainCode(String code) {
        _domainCode = code;
    }
    
    public String getDomainCodeDes() {
        return _domainCode1;
    }
    public void setDomainCodeDes(String code1) {
        _domainCode1 = code1;
    }
    public String getGradeCode1() {
        return _gradeCode1;
    }
    public void setGradeCode1(String gradeCode) {
        _gradeCode1 = gradeCode;
    }
    public String getCategoryCode1() {
        return _categoryCode1;
    }
    public void setCategoryCode1(String code) {
        _categoryCode1 = code;
    }
    public String getModify() {
    		return _modify;
    }

    public void setModify(String modify) {
    		_modify = modify;
    }
    public String getError() {
    		return _error;
    }

    public void setError(String error) {
    		_error = error;
    }
    public String getDateRange() {
		return _dateRange;
	}
	
	public void setDateRange(String dateRange) {
		_dateRange = dateRange;
	}
	
	public static TransferRulesVO getInstance(){
		return new TransferRulesVO();
	}

    	public String getApplicableFrom() {
		return applicableFrom;
	}

	public String getApplicableTO() {
		return applicableTO;
	}

	public void setApplicableFrom(String applicableFrom) {
		this.applicableFrom = applicableFrom;
	}

	public void setApplicableTO(String applicableTO) {
		this.applicableTO = applicableTO;
	}

}
