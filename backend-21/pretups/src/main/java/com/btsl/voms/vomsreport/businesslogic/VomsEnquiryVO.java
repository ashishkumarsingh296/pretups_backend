package com.btsl.voms.vomsreport.businesslogic;

import java.util.ArrayList;
import java.util.Date;

public class VomsEnquiryVO {
    // Instance variables
    private String _serialNo;
    private String _generationBatchNo;
    private String _enableBatchNo;
    private String _saleBatchNo;
    private String _mrpStr;
    private int _attemptUsed;
    private String _voucherStatus;
    private String _expiryDateStr;
    private long _totalValueUsed;
    private String _consumeBeforeStr;
    private String _lastConsumedBy;
    private String _lastConsumedOnStr;
    private String _createdOnStr;
    private String _modifiedBy;
    private String _modifiedOnStr;
    private String _productionNetworkName;
    private String _lastUserNetworkName;
    private String _categoryName;
    private double _talkTime;
    private String _talkTimeStr;
    private long _validity;
    private String _validityStr;
    private String _categoryType;
    private String _domainName;
    private String _enabledOn;
    private String _previousStatus;
    private String _lastConsumedOption;
    private int _lastAttemptNo;
    private String _attemptType;
    private String _firstConsumedBy;
    private String _firstConsumedOnStr;
    private String _oneTimeUsage;
    private int _noOfRequests = 0;
    private String _status;
    private int _lastRequestAttemptNo;
    private int _attemptAllowed = 0;
    private ArrayList _enquiryVOList;
    private double _mrp = 0.0;
    private String _productName;
    private String _userNetworkName;
    private String _purposeID;
    private long _totalValueAllowed = 0;
    private long _previousBalance = 0;
    private int _attemptNo = 0;
    private String _option;
    private String _consumedBy;
    private String _consumedOnStr;
    private String _requestedBy;
    private long _gracePeriod;
    private long _newBalance;
    private String _requestPartnerID;
    private String _requestSource;
    private long _valueUsed;
    private String _receiverMsisdn;
    private String _senderMsisdn;// To add Sender MSISDN in Voucher
                                 // enquiry[added by Vipul on 06/12/07]
    private String _soldStatus;
    private String _statusCode;
    private Date _soldOn;
    private String _soldOnStr;
    // added by vikram
    private String _selectorName; // for the sub service name

    private ArrayList _voucherUsage;
    //added by vishwajeet
    private String _voucherSegment;

    // voms
    private String _voucherType = null;
    private String _name = null;
    private String _serviceTypeMapping = null;
    private ArrayList _voucherTypeList = null;

	private int totalDistributed;
    private int totalConsumed;
    
    public int getTotalDistributed() {
		return totalDistributed;
	}

	public void setTotalDistributed(int totalDistributed) {
		this.totalDistributed = totalDistributed;
	}

	public int getTotalConsumed() {
		return totalConsumed;
	}

	public void setTotalConsumed(int totalConsumed) {
		this.totalConsumed = totalConsumed;
	}


    @Override
	public String toString() {
		return "VomsEnquiryVO [_serialNo=" + _serialNo + ", _generationBatchNo=" + _generationBatchNo
				+ ", _enableBatchNo=" + _enableBatchNo + ", _saleBatchNo=" + _saleBatchNo + ", _mrpStr=" + _mrpStr
				+ ", _attemptUsed=" + _attemptUsed + ", _voucherStatus=" + _voucherStatus + ", _expiryDateStr="
				+ _expiryDateStr + ", _totalValueUsed=" + _totalValueUsed + ", _consumeBeforeStr=" + _consumeBeforeStr
				+ ", _lastConsumedBy=" + _lastConsumedBy + ", _lastConsumedOnStr=" + _lastConsumedOnStr
				+ ", _createdOnStr=" + _createdOnStr + ", _modifiedBy=" + _modifiedBy + ", _modifiedOnStr="
				+ _modifiedOnStr + ", _productionNetworkName=" + _productionNetworkName + ", _lastUserNetworkName="
				+ _lastUserNetworkName + ", _categoryName=" + _categoryName + ", _talkTime=" + _talkTime
				+ ", _talkTimeStr=" + _talkTimeStr + ", _validity=" + _validity + ", _validityStr=" + _validityStr
				+ ", _categoryType=" + _categoryType + ", _domainName=" + _domainName + ", _enabledOn=" + _enabledOn
				+ ", _previousStatus=" + _previousStatus + ", _lastConsumedOption=" + _lastConsumedOption
				+ ", _lastAttemptNo=" + _lastAttemptNo + ", _attemptType=" + _attemptType + ", _firstConsumedBy="
				+ _firstConsumedBy + ", _firstConsumedOnStr=" + _firstConsumedOnStr + ", _oneTimeUsage=" + _oneTimeUsage
				+ ", _noOfRequests=" + _noOfRequests + ", _status=" + _status + ", _lastRequestAttemptNo="
				+ _lastRequestAttemptNo + ", _attemptAllowed=" + _attemptAllowed + ", _enquiryVOList=" + _enquiryVOList
				+ ", _mrp=" + _mrp + ", _productName=" + _productName + ", _userNetworkName=" + _userNetworkName
				+ ", _purposeID=" + _purposeID + ", _totalValueAllowed=" + _totalValueAllowed + ", _previousBalance="
				+ _previousBalance + ", _attemptNo=" + _attemptNo + ", _option=" + _option + ", _consumedBy="
				+ _consumedBy + ", _consumedOnStr=" + _consumedOnStr + ", _requestedBy=" + _requestedBy
				+ ", _gracePeriod=" + _gracePeriod + ", _newBalance=" + _newBalance + ", _requestPartnerID="
				+ _requestPartnerID + ", _requestSource=" + _requestSource + ", _valueUsed=" + _valueUsed
				+ ", _receiverMsisdn=" + _receiverMsisdn + ", _senderMsisdn=" + _senderMsisdn + ", _soldStatus="
				+ _soldStatus + ", _statusCode=" + _statusCode + ", _soldOn=" + _soldOn + ", _soldOnStr=" + _soldOnStr
				+ ", _selectorName=" + _selectorName + ", _voucherUsage=" + _voucherUsage + ", _voucherSegment="
				+ _voucherSegment + ", _voucherType=" + _voucherType + ", _name=" + _name + ", _serviceTypeMapping="
				+ _serviceTypeMapping + ", _voucherTypeList=" + _voucherTypeList + ", totalDistributed="
				+ totalDistributed + ", totalConsumed=" + totalConsumed + "]";
	}

    public ArrayList getVoucherTypeList() {
        return _voucherTypeList;
    }

    public void setVoucherTypeList(ArrayList type) {
        _voucherTypeList = type;
    }

    public String getVoucherType() {
        return _voucherType;
    }

    public void setVoucherType(String type) {
        _voucherType = type;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getServiceTypeMapping() {
        return _serviceTypeMapping;
    }

    public void setServiceTypeMapping(String typeMapping) {
        _serviceTypeMapping = typeMapping;
    }

    //
    /**
     * Method toString.
     * This method is used to display all of the information of
     * the object of the VomsProductVO class.
     * 
     * @return String
     */
    /*
     * public String toString()
     * {
     * StringBuffer sb = new StringBuffer();
     * sb.append(" _categoryID=" + _categoryID);
     * sb.append(" _productName=" + _productName);
     * sb.append(" _description=" + _description);
     * sb.append(" _shortName=" + _shortName);
     * sb.append(" _mrp=" + _mrp);
     * sb.append(" _status=" + _status);
     * sb.append(" _mrpStr=" + _mrpStr);
     * sb.append(" _productCode=" + _productCode);
     * sb.append(" _minReqQuantity=" + _minReqQuantity);
     * sb.append(" _maxReqQuantity=" + _maxReqQuantity);
     * sb.append(" _multipleFactor=" + _multipleFactor);
     * sb.append(" _expiryPeriod=" + _expiryPeriod);
     * sb.append(" _individualEntity=" + _individualEntity);
     * sb.append(" _attribute1=" + _attribute1);
     * sb.append(" _createdBy=" + _createdBy);
     * sb.append(" _createdOn=" + _createdOn);
     * sb.append(" _modifiedBy=" + _modifiedBy);
     * sb.append(" _modifiedOn=" + _modifiedOn);
     * sb.append(" _serviceCode=" + _serviceCode);
     * sb.append(" _noOfArguments=" + _noOfArguments);
     * sb.append(" _talkTime=" + _talkTime);
     * sb.append(" _validity=" + _validity);
     * 
     * return sb.toString();
     * }
     */

    public String getEnableBatchNo() {
        return _enableBatchNo;
    }

    public void setEnableBatchNo(String enableBatchNo) {
        _enableBatchNo = enableBatchNo;
    }

    public String getGenerationBatchNo() {
        return _generationBatchNo;
    }

    public void setGenerationBatchNo(String generationBatchNo) {
        _generationBatchNo = generationBatchNo;
    }

    public String getSaleBatchNo() {
        return _saleBatchNo;
    }

    public void setSaleBatchNo(String saleBatchNo) {
        _saleBatchNo = saleBatchNo;
    }

    public String getSerialNo() {
        return _serialNo;
    }

    public void setSerialNo(String serialNo) {
        _serialNo = serialNo;
    }

    public double getMrp() {
        return _mrp;
    }

    public void setMrp(double mrp) {
        _mrp = mrp;
    }

    public String getMrpStr() {
        return _mrpStr;
    }

    public void setMrpStr(String mrpStr) {
        _mrpStr = mrpStr;
    }

    public int getAttemptUsed() {
        return _attemptUsed;
    }

    public void setAttemptUsed(int attemptUsed) {
        _attemptUsed = attemptUsed;
    }

    public String getConsumeBeforeStr() {
        return _consumeBeforeStr;
    }

    public void setConsumeBeforeStr(String consumeBeforeStr) {
        _consumeBeforeStr = consumeBeforeStr;
    }

    public String getExpiryDateStr() {
        return _expiryDateStr;
    }

    public void setExpiryDateStr(String expiryDateStr) {
        _expiryDateStr = expiryDateStr;
    }

    public String getLastConsumedBy() {
        return _lastConsumedBy;
    }

    public void setLastConsumedBy(String lastConsumedBy) {
        _lastConsumedBy = lastConsumedBy;
    }

    public String getLastConsumedOnStr() {
        return _lastConsumedOnStr;
    }

    public void setLastConsumedOnStr(String lastConsumedOnStr) {
        _lastConsumedOnStr = lastConsumedOnStr;
    }

    public long getTotalValueUsed() {
        return _totalValueUsed;
    }

    public void setTotalValueUsed(long totalValueUsed) {
        _totalValueUsed = totalValueUsed;
    }

    public String getVoucherStatus() {
        return _voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        _voucherStatus = voucherStatus;
    }

    public int getAttemptAllowed() {
        return _attemptAllowed;
    }

    public void setAttemptAllowed(int attemptAllowed) {
        _attemptAllowed = attemptAllowed;
    }

    public String getAttemptType() {
        return _attemptType;
    }

    public void setAttemptType(String attemptType) {
        _attemptType = attemptType;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public String getCategoryType() {
        return _categoryType;
    }

    public void setCategoryType(String categoryType) {
        _categoryType = categoryType;
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    public String getEnabledOn() {
        return _enabledOn;
    }

    public void setEnabledOn(String enabledOn) {
        _enabledOn = enabledOn;
    }

    public ArrayList getEnquiryVOList() {
        return _enquiryVOList;
    }

    public void setEnquiryVOList(ArrayList enquiryVOList) {
        _enquiryVOList = enquiryVOList;
    }

    public String getFirstConsumedBy() {
        return _firstConsumedBy;
    }

    public void setFirstConsumedBy(String firstConsumedBy) {
        _firstConsumedBy = firstConsumedBy;
    }

    public String getFirstConsumedOnStr() {
        return _firstConsumedOnStr;
    }

    public void setFirstConsumedOnStr(String firstConsumedOnStr) {
        _firstConsumedOnStr = firstConsumedOnStr;
    }

    public int getLastAttemptNo() {
        return _lastAttemptNo;
    }

    public void setLastAttemptNo(int lastAttemptNo) {
        _lastAttemptNo = lastAttemptNo;
    }

    public String getLastConsumedOption() {
        return _lastConsumedOption;
    }

    public void setLastConsumedOption(String lastConsumedOption) {
        _lastConsumedOption = lastConsumedOption;
    }

    public String getLastUserNetworkName() {
        return _lastUserNetworkName;
    }

    public void setLastUserNetworkName(String lastUserNetworkName) {
        _lastUserNetworkName = lastUserNetworkName;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public String getCreatedOnStr() {
        return _createdOnStr;
    }

    public void setCreatedOnStr(String createdOnStr) {
        _createdOnStr = createdOnStr;
    }

    public int getLastRequestAttemptNo() {
        return _lastRequestAttemptNo;
    }

    public void setLastRequestAttemptNo(int lastRequestAttemptNo) {
        _lastRequestAttemptNo = lastRequestAttemptNo;
    }

    public String getModifiedOnStr() {
        return _modifiedOnStr;
    }

    public void setModifiedOnStr(String modifiedOnStr) {
        _modifiedOnStr = modifiedOnStr;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String productName) {
        _productName = productName;
    }

    public int getNoOfRequests() {
        return _noOfRequests;
    }

    public void setNoOfRequests(int noOfRequests) {
        _noOfRequests = noOfRequests;
    }

    public String getOneTimeUsage() {
        return _oneTimeUsage;
    }

    public void setOneTimeUsage(String oneTimeUsage) {
        _oneTimeUsage = oneTimeUsage;
    }

    public String getPreviousStatus() {
        return _previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        _previousStatus = previousStatus;
    }

    public String getProductionNetworkName() {
        return _productionNetworkName;
    }

    public void setProductionNetworkName(String productionNetworkName) {
        _productionNetworkName = productionNetworkName;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public double getTalkTime() {
        return _talkTime;
    }

    public void setTalkTime(double talkTime) {
        _talkTime = talkTime;
    }

    public String getTalkTimeStr() {
        return _talkTimeStr;
    }

    public void setTalkTimeStr(String talkTimeStr) {
        _talkTimeStr = talkTimeStr;
    }

    public long getTotalValueAllowed() {
        return _totalValueAllowed;
    }

    public void setTotalValueAllowed(long totalValueAllowed) {
        _totalValueAllowed = totalValueAllowed;
    }

    public long getValidity() {
        return _validity;
    }

    public void setValidity(long validity) {
        _validity = validity;
    }

    public String getValidityStr() {
        return _validityStr;
    }

    public void setValidityStr(String validityStr) {
        _validityStr = validityStr;
    }

    public String getPurposeID() {
        return _purposeID;
    }

    public void setPurposeID(String purposeID) {
        _purposeID = purposeID;
    }

    public String getUserNetworkName() {
        return _userNetworkName;
    }

    public void setUserNetworkName(String userNetworkName) {
        _userNetworkName = userNetworkName;
    }

    public int getAttemptNo() {
        return _attemptNo;
    }

    public void setAttemptNo(int attemptNo) {
        _attemptNo = attemptNo;
    }

    public String getOption() {
        return _option;
    }

    public void setOption(String option) {
        _option = option;
    }

    public long getPreviousBalance() {
        return _previousBalance;
    }

    public void setPreviousBalance(long previousBalance) {
        _previousBalance = previousBalance;
    }

    public String getConsumedBy() {
        return _consumedBy;
    }

    public void setConsumedBy(String consumedBy) {
        _consumedBy = consumedBy;
    }

    public String getConsumedOnStr() {
        return _consumedOnStr;
    }

    public void setConsumedOnStr(String consumedOnStr) {
        _consumedOnStr = consumedOnStr;
    }

    public long getGracePeriod() {
        return _gracePeriod;
    }

    public void setGracePeriod(long gracePeriod) {
        _gracePeriod = gracePeriod;
    }

    public long getNewBalance() {
        return _newBalance;
    }

    public void setNewBalance(long newBalance) {
        _newBalance = newBalance;
    }

    public String getRequestedBy() {
        return _requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        _requestedBy = requestedBy;
    }

    public String getRequestPartnerID() {
        return _requestPartnerID;
    }

    public void setRequestPartnerID(String requestPartnerID) {
        _requestPartnerID = requestPartnerID;
    }

    public String getRequestSource() {
        return _requestSource;
    }

    public void setRequestSource(String requestSource) {
        _requestSource = requestSource;
    }

    public long getValueUsed() {
        return _valueUsed;
    }

    public void setValueUsed(long valueUsed) {
        _valueUsed = valueUsed;
    }

    public String getReceiverMsisdn() {
        return _receiverMsisdn;
    }

    public void setReceiverMsisdn(String receiverMsisdn) {
        _receiverMsisdn = receiverMsisdn;
    }

    public ArrayList getVoucherUsage() {
        return _voucherUsage;
    }

    public void setVoucherUsage(ArrayList voucherUsage) {
        _voucherUsage = voucherUsage;
    }

    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    public void setSenderMsisdn(String senderMsisdn) {
        _senderMsisdn = senderMsisdn;
    }

    /**
     * @return the selectorName
     */
    public String getSelectorName() {
        return _selectorName;
    }

    /**
     * @param selectorName
     *            the selectorName to set
     */
    public void setSelectorName(String selectorName) {
        _selectorName = selectorName;
    }

	public String getSoldOnStr() {
		return _soldOnStr;
	}

	public void setSoldOnStr(String soldOnStr) {
		this._soldOnStr = soldOnStr;
	}

	public Date getSoldOn() {
		return _soldOn;
	}

	public void setSoldOn(Date soldOn) {
		this._soldOn = soldOn;
	}
	
	public String getSoldStatus() {
		return _soldStatus;
	}

	public void setSoldStatus(String soldStatus) {
		this._soldStatus = soldStatus;
	}

	public String getStatusCode() {
		return _statusCode;
	}

	public void setStatusCode(String statusCode) {
		this._statusCode = statusCode;
	}

	public String getVoucherSegment() {
		return _voucherSegment;
	}

	public void setVoucherSegment(String _voucherSegment) {
		this._voucherSegment = _voucherSegment;
	}

}
