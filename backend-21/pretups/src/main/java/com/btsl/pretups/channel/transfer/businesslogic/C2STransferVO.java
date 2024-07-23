package com.btsl.pretups.channel.transfer.businesslogic;

/*
 * @(#)C2STransferVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.io.Serializable;
import java.util.List;

import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.btsl.user.businesslogic.UserLoanVO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class C2STransferVO extends TransferVO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "C2STransferVO [_senderNetworkCode="
				+ _senderNetworkCode + ", _domainCode=" + _domainCode
				+ ", _ownerUserID=" + _ownerUserID
				+ ", _transferProfileCtInitializeReqd="
				+ _transferProfileCtInitializeReqd + ", _differentialGiven="
				+ _differentialGiven + ", _grphDomainCode=" + _grphDomainCode
				+ ",  _senderCategoryCode="
				+ _senderCategoryCode + ", _processed=" + _processed
				+ ", _txnCalculationDone=" + _txnCalculationDone + ", _counts="
				+ _counts + ", _userId=" + _userId + ", _iatTransferItemVO=" + _iatTransferItemVO
				+ ", _activeUserName="
				+ _activeUserName + ", _activeBonusProvided="
				+ _activeBonusProvided + ", _totalCommission="
				+ _totalCommission + ", _reverseTransferID="
				+ _reverseTransferID + ", _subscriberSID=" + _subscriberSID
				+ ", _oldTxnId=" + _oldTxnId + ", _txnType=" + _txnType
				+ ", totalBalanceAcrossPDAWallets="
				+ totalBalanceAcrossPDAWallets
				+ ", totalPreviousBalanceAcrossPDAWallets="
				+ totalPreviousBalanceAcrossPDAWallets + ", pdaWalletList="
				+ pdaWalletList + ", _voucherCode=" + _voucherCode
				+ ", _graceDaysStr=" + _graceDaysStr + ", _validity="
				+ _validity + ", _serialnumber=" + _serialnumber
				+ ", _lmsVersion=" + _lmsVersion + ", _sid=" + _sid
				+ ", _commission=" + _commission + ", _commissionType="
				+ _commissionType + ", _commissionApplicable="
				+ _commissionApplicable + ", _commissionGiven="
				+ _commissionGiven + ", _isRoam=" + _isRoam
				+ ", _roamPenalty=" + _roamPenalty
				+ ", _roamPenaltyPercentage=" + _roamPenaltyPercentage
				+ ", _roamPenaltyPercentageOwner="
				+ _roamPenaltyPercentageOwner + ", _tax1onRoamPenalty="
				+ _tax1onRoamPenalty + ", _tax2onRoamPenalty="
				+ _tax2onRoamPenalty + ", _ownerUserVO=" + _ownerUserVO
				+ ", _roamPenaltyOwner=" + _roamPenaltyOwner + ", _penalty="
				+ _penalty + ", _isDebitPenalty=" + _isDebitPenalty
				+ ", _penaltyInsufficientBalance="
				+ _penaltyInsufficientBalance
				+ ", _penaltyInsufficientBalanceOwner="
				+ _penaltyInsufficientBalanceOwner + ", _roamDiffAmount="
				+ _roamDiffAmount + ", _ownerCommProfile=" + _ownerCommProfile
				+ ", _penaltyDetails=" + _penaltyDetails
				+ ", _senderRoamReconDebitMessage="
				+ _senderRoamReconDebitMessage
				+ ", _senderOwnerRoamReconDebitMessage="
				+ _senderOwnerRoamReconDebitMessage
				+ ", _senderRoamReconCreditMessage="
				+ _senderRoamReconCreditMessage
				+ ", _senderOwnerRoamReconCreditMessage="
				+ _senderOwnerRoamReconCreditMessage + ", _stopAddnCommission="
				+ _stopAddnCommission + ", _ownerPostBalance="
				+ _ownerPostBalance + ", _adjustmentID=" + _adjustmentID
				+ ", _retAdjAmt=" + _retAdjAmt + ", _isMNP=" + _isMNP
				+ ", _gifterMSISDN=" + _gifterMSISDN
				+ ", _senderReturnPromoMessage=" + _senderReturnPromoMessage
				+ ", _multiCurrencyDetailVO=" + _multiCurrencyDetailVO
				+ ", _ownerPenalty=" + _ownerPenalty 
				+ ", gatewayCodeForReversal" + gatewayCodeForReversal + "]";
	}
    
	private String _senderNetworkCode;
    private String _domainCode;
    private String _ownerUserID;
    private boolean _transferProfileCtInitializeReqd = false;
    private String _differentialGiven;
    private String _grphDomainCode;
    private String _senderCategoryCode;
    private String _processed;
    private String _txnCalculationDone;
    private long _counts;
    private String _userId;
    private IATTransferItemVO _iatTransferItemVO;

    private String _activeUserName;
    private String _activeBonusProvided;
    private long _totalCommission =0;
    private String _reverseTransferID;
    private String _subscriberSID;

    private String _oldTxnId;

    private String _txnType = null;
    /** @author birendra.mishra */

    private long totalBalanceAcrossPDAWallets;
    private long totalPreviousBalanceAcrossPDAWallets;
    private List<UserProductWalletMappingVO> pdaWalletList;
    // Zeeshan Aleem
    private String _voucherCode;
    private String _graceDaysStr = null;
    private int _validity;
    private String _serialnumber;
    // Brajesh
    private String _lmsVersion;
	private String _sid;
	private String _commission="0";
	private String _commissionType;
	private String _commissionApplicable=null;
	private String _commissionGiven=null;

    // roam penalty
    private boolean _isRoam = false;
    private long _roamPenalty;
    private int _roamPenaltyPercentage;
    private int _roamPenaltyPercentageOwner;
    private long _tax1onRoamPenalty;
    private long _tax2onRoamPenalty;
    private ChannelUserVO _ownerUserVO = null;
    private long _roamPenaltyOwner;
    private long _penalty;
    private boolean _isDebitPenalty=false;
	private boolean _penaltyInsufficientBalance=false;
    private boolean _penaltyInsufficientBalanceOwner=false;
    private String _roamDiffAmount=null;
    private String _ownerCommProfile=null;
    private String _penaltyDetails=null;
    private String _senderRoamReconDebitMessage=null;
    private String _senderOwnerRoamReconDebitMessage=null;
    private String _senderRoamReconCreditMessage=null;
    private String _senderOwnerRoamReconCreditMessage=null;
	private boolean _stopAddnCommission=false;
	private long _ownerPostBalance=0;
	private String _adjustmentID;
	private long _retAdjAmt;
	private String _isMNP="N";
	private String _gifterMSISDN="";
	private String _senderReturnPromoMessage;
	private String _multiCurrencyDetailVO;
	 private List<ChannelSoSVO> channelSoSVOList;
	 private String lrAllowed = null;
	 private long lrMaxAmount=0;
	 private long lrAmount=0;
	 private boolean lrFlag;
	 private boolean otfCountsDecreased;
	 private boolean otfCountsIncreased;
	 private boolean targetAchieved;
	 private String otfApplicable = "N";
	 private long promoBonus = 0;
	 private String transferDateTimeAsString;
	 private String vomsSerialPinAsString;
	 private List<UserLoanVO> userLoanVOList;

	public String getTransferDateTimeAsString() {
		return transferDateTimeAsString;
	}
	public void setTransferDateTimeAsString(String transferDateTimeAsString) {
		this.transferDateTimeAsString = transferDateTimeAsString;
	}
	 
	 

	public String isOtfApplicable() {
		return otfApplicable;
	}
	public void setOtfApplicable(String otfApplicable) {
		this.otfApplicable = otfApplicable;
	}
	public boolean isTargetAchieved() {
		return targetAchieved;
	}
	public void setTargetAchieved(boolean targetAchieved) {
		this.targetAchieved = targetAchieved;
	}
	public boolean isOtfCountsIncreased() {
		return otfCountsIncreased;
	}
	public void setOtfCountsIncreased(boolean otfCountsIncreased) {
		this.otfCountsIncreased = otfCountsIncreased;
	}
	public boolean isOtfCountsDecreased() {
		return otfCountsDecreased;
	}
	public void setOtfCountsDecreased(boolean otfCountsDecreased) {
		this.otfCountsDecreased = otfCountsDecreased;
	}
	public List<ChannelSoSVO> getChannelSoSVOList() {
		return channelSoSVOList;
	}
	public void setChannelSoSVOList(List<ChannelSoSVO> channelSoSVOList) {
		this.channelSoSVOList = channelSoSVOList;
	}
	public String getSenderReturnPromoMessage() {
	return _senderReturnPromoMessage;
	}
	public void setSenderReturnPromoMessage(String senderReturnPromoMessage) {
	_senderReturnPromoMessage = senderReturnPromoMessage;
	}
	public String getIsMNP() {
		return _isMNP;
	}

	public void setIsMNP(String _ismnp) {
		_isMNP = _ismnp;
	}

	public String getGifterMSISDN() {
		return _gifterMSISDN;
	}

	public void setGifterMSISDN(String _giftermsisdn) {
		_gifterMSISDN = _giftermsisdn;
	}
	public long getRetAdjAmt() {
		return _retAdjAmt;
	}
	public void setRetAdjAmt(Long adjAmt) {
		_retAdjAmt = adjAmt;
	}
	public String getAdjustmentID() {
		return _adjustmentID;
	}
	public void setAdjustmentID(String adjNo) {
		_adjustmentID = adjNo;
	}
    public long getOwnerPostBalance() {
		return _ownerPostBalance;
	}

	public void setOwnerPostBalance(long _ownerPostBalance) {
		this._ownerPostBalance = _ownerPostBalance;
	}

	public boolean isStopAddnCommission() {
		return _stopAddnCommission;
	}

	public void setStopAddnCommission(boolean _stopAddnCommission) {
		this._stopAddnCommission = _stopAddnCommission;
	}
    
    public String getSenderOwnerRoamReconCreditMessage() {
		return _senderOwnerRoamReconCreditMessage;
	}

	public void setSenderOwnerRoamReconCreditMessage(
			String _senderOwnerRoamReconCreditMessage) {
		this._senderOwnerRoamReconCreditMessage = _senderOwnerRoamReconCreditMessage;
	}

	public String getSenderOwnerRoamReconDebitMessage() {
		return _senderOwnerRoamReconDebitMessage;
	}

	public void setSenderOwnerRoamReconDebitMessage(
			String _senderOwnerRoamReconDebitMessage) {
		this._senderOwnerRoamReconDebitMessage = _senderOwnerRoamReconDebitMessage;
	}

	
    public String getSenderRoamReconCreditMessage() {
		return _senderRoamReconCreditMessage;
	}

	public void setSenderRoamReconCreditMessage(
			String _senderRoamReconCreditMessage) {
		this._senderRoamReconCreditMessage = _senderRoamReconCreditMessage;
	}

	
    public String getSenderRoamReconDebitMessage() {
		return _senderRoamReconDebitMessage;
	}

	public void setSenderRoamReconDebitMessage(String _senderRoamReconDebitMessage) {
		this._senderRoamReconDebitMessage = _senderRoamReconDebitMessage;
	}

	public String getPenaltyDetails() {
		return _penaltyDetails;
	}

	public void setPenaltyDetails(String _penaltyDetails) {
		this._penaltyDetails = _penaltyDetails;
	}

	public String getOwnerCommProfile() {
		return _ownerCommProfile;
	}

	public void setOwnerCommProfile(String _ownerCommProfile) {
		this._ownerCommProfile = _ownerCommProfile;
	}

	public int getRoamPenaltyPercentageOwner() {
		return _roamPenaltyPercentageOwner;
	}

	public void setRoamPenaltyPercentageOwner(int _roamPenaltyPercentageOwner) {
		this._roamPenaltyPercentageOwner = _roamPenaltyPercentageOwner;
	}

   public String getRoamDiffAmount() {
		return _roamDiffAmount;
		}

	public void setRoamDiffAmount(String _roamDiffAmount) {
		this._roamDiffAmount = _roamDiffAmount;
	}
	
    public boolean isPenaltyInsufficientBalanceOwner() {
		return _penaltyInsufficientBalanceOwner;
	}

	public void setPenaltyInsufficientBalanceOwner(
			boolean _penaltyInsufficientBalanceOwner) {
		this._penaltyInsufficientBalanceOwner = _penaltyInsufficientBalanceOwner;
	}

	public boolean isPenaltyInsufficientBalance() {
		return _penaltyInsufficientBalance;
	}

	public void setPenaltyInsufficientBalance(boolean _penaltyInsufficientBalance) {
		this._penaltyInsufficientBalance = _penaltyInsufficientBalance;
	}

   public boolean isDebitPenalty() {
		return _isDebitPenalty;
	}

	public void setIsDebitPenalty(boolean _isDebitPenalty) {
		this._isDebitPenalty = _isDebitPenalty;
	}

    public long getPenalty() {
		return _penalty;
	}

	public void setPenalty(long _penalty) {
		this._penalty = _penalty;
	}

	public long getOwnerPenalty() {
		return _ownerPenalty;
	}

	public void setOwnerPenalty(long _ownerPenalty) {
		this._ownerPenalty = _ownerPenalty;
	}

	private long _ownerPenalty;

    public long getRoamPenaltyOwner() {
        return _roamPenaltyOwner;
    }

    public void setRoamPenaltyOwner(long _roamPenaltyOwner) {
        this._roamPenaltyOwner = _roamPenaltyOwner;
    }

    public ChannelUserVO getOwnerUserVO() {
        return _ownerUserVO;
    }

    public void setOwnerUserVO(ChannelUserVO _OwnerUserVO) {
        this._ownerUserVO = _OwnerUserVO;
    }

    public int getRoamPenaltyPercentage() {
        return _roamPenaltyPercentage;
    }

    public void setRoamPenaltyPercentage(int _roamPenaltyPercentage) {
        this._roamPenaltyPercentage = _roamPenaltyPercentage;
    }

    public long getTax1onRoamPenalty() {
        return _tax1onRoamPenalty;
    }

    public void setTax1onRoamPenalty(long _tax1onRoamPenalty) {
        this._tax1onRoamPenalty = _tax1onRoamPenalty;
    }

    public long getTax2onRoamPenalty() {
        return _tax2onRoamPenalty;
    }

    public void setTax2onRoamPenalty(long _tax2onRoamPenalty) {
        this._tax2onRoamPenalty = _tax2onRoamPenalty;
    }

    public long getRoamPenalty() {
        return _roamPenalty;
    }

    public void setRoamPenalty(long _roamPenalty) {
        this._roamPenalty = _roamPenalty;
    }

    public boolean isRoam() {
        return _isRoam;
    }

    public void setIsRoam(boolean _isRoam) {
        this._isRoam = _isRoam;
    }

    public String getSenderNetworkCode() {
        return _senderNetworkCode;
    }

    public void setSenderNetworkCode(String senderNetworkCode) {
        _senderNetworkCode = senderNetworkCode;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public String getOwnerUserID() {
        return _ownerUserID;
    }

    public void setOwnerUserID(String ownerUserID) {
        _ownerUserID = ownerUserID;
    }

    public boolean isTransferProfileCtInitializeReqd() {
        return _transferProfileCtInitializeReqd;
    }

    public void setTransferProfileCtInitializeReqd(boolean transferProfileCtInitializeReqd) {
        _transferProfileCtInitializeReqd = transferProfileCtInitializeReqd;
    }

    public String getDifferentialGiven() {
        return _differentialGiven;
    }

    public void setDifferentialGiven(String differentialGiven) {
        _differentialGiven = differentialGiven;
    }

    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    public void setGrphDomainCode(String grphDomainCode) {
        _grphDomainCode = grphDomainCode;
    }


    /**
     * @return Returns the processed.
     */
    public String getProcessed() {
        return _processed;
    }

    /**
     * @param processed
     *            The processed to set.
     */
    public void setProcessed(String processed) {
        _processed = processed;
    }

    /**
     * @return Returns the senderCategoryCode.
     */
    public String getSenderCategoryCode() {
        return _senderCategoryCode;
    }

    /**
     * @param senderCategoryCode
     *            The senderCategoryCode to set.
     */
    public void setSenderCategoryCode(String senderCategoryCode) {
        _senderCategoryCode = senderCategoryCode;
    }

    /**
     * @return Returns the txnCalculationDone.
     */
    public String getTxnCalculationDone() {
        return _txnCalculationDone;
    }

    /**
     * @param txnCalculationDone
     *            The txnCalculationDone to set.
     */
    public void setTxnCalculationDone(String txnCalculationDone) {
        _txnCalculationDone = txnCalculationDone;
    }

    /**
     * @return Returns the counts.
     */
    public long getCounts() {
        return _counts;
    }

    /**
     * @param counts
     *            The counts to set.
     */
    public void setCounts(long counts) {
        _counts = counts;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        _userId = userId;
    }


    /**
     * @return Returns the iatTransferItemVO.
     */
    public IATTransferItemVO getIatTransferItemVO() {
        return _iatTransferItemVO;
    }

    /**
     * @param iatTransferItemVO
     *            The iatTransferItemVO to set.
     */
    public void setIatTransferItemVO(IATTransferItemVO iatTransferItemVO) {
        _iatTransferItemVO = iatTransferItemVO;
    }


    /**
     * @return the activeUserName
     */
    public String getActiveUserName() {
        return _activeUserName;
    }

    /**
     * @param activeUserName
     *            the activeUserName to set
     */
    public void setActiveUserName(String activeUserName) {
        this._activeUserName = activeUserName;
    }

    public String getActiveBonusProvided() {
        return _activeBonusProvided;
    }

    public void setActiveBonusProvided(String bonusAccID) {
        _activeBonusProvided = bonusAccID;
    }

    /**
     * @return Returns the totalCommission.
     */
    public long getTotalCommission() {
        return _totalCommission;
    }

    /**
     * @param totalCommission
     *            The totalCommission to set.
     */
    public void setTotalCommission(long totalCommission) {
        _totalCommission = totalCommission;
    }

    /**
     * @return Returns the reverseTransferID.
     */
    public String getReverseTransferID() {
        return _reverseTransferID;
    }

    /**
     * @param reverseTransferID
     *            The reverseTransferID to set.
     */
    public void setReverseTransferID(String reverseTransferID) {
        _reverseTransferID = reverseTransferID;
    }

    /**
     * @return the subscriberSID
     */
    public String getSubscriberSID() {
        return _subscriberSID;
    }

    /**
     * @param subscriberSID
     *            the subscriberSID to set
     */
    public void setSubscriberSID(String subscriberSID) {
        _subscriberSID = subscriberSID;
    }


    public String getOldTxnId() {
        return _oldTxnId;
    }

    public void setOldTxnId(String txnId) {
        _oldTxnId = txnId;
    }

    public String getTxnType() {
        return _txnType;
    }

    public void setTxnType(String type) {
        _txnType = type;
    }

    public long getTotalBalanceAcrossPDAWallets() {
        return totalBalanceAcrossPDAWallets;
    }

    public void setTotalBalanceAcrossPDAWallets(long totalBalanceAcrossPDAWallets) {
        this.totalBalanceAcrossPDAWallets = totalBalanceAcrossPDAWallets;
    }

    public long getTotalPreviousBalanceAcrossPDAWallets() {
        return totalPreviousBalanceAcrossPDAWallets;
    }

    public void setTotalPreviousBalanceAcrossPDAWallets(long totalPreviousBalanceAcrossPDAWallets) {
        this.totalPreviousBalanceAcrossPDAWallets = totalPreviousBalanceAcrossPDAWallets;
    }

    public List<UserProductWalletMappingVO> getPdaWalletList() {
        return pdaWalletList;
    }

    public void setPdaWalletList(List<UserProductWalletMappingVO> pdaWalletList) {
        this.pdaWalletList = pdaWalletList;
    }

    public String getVoucherCode() {
        return _voucherCode;
    }

    public void setVoucherCode(String code) {
        _voucherCode = code;
    }

    public String getGraceDaysStr() {
        return _graceDaysStr;
    }

    public void setGraceDaysStr(String daysStr) {
        _graceDaysStr = daysStr;
    }

    public int getValidity() {
        return _validity;
    }

    public void setValidity(int _validity) {
        this._validity = _validity;
    }

    public String getSerialNumber() {
        return _serialnumber;
    }

    public void setSerialNumber(String serialnumber) {
        _serialnumber = serialnumber;
    }

    // Brajesh
    public String getLmsVersion() {
        return _lmsVersion;
    }

    public void setLmsVersion(String LmsVersion) {
        _lmsVersion = LmsVersion;
    }

	public String getSID() {
		return _sid;
	}
	public void setSID(String sid) {
		_sid = sid;
	}
	public String getCommission() {
		return _commission;
	}
	public void setCommission(String _commission) {
		this._commission = _commission;
	}
	public String getCommissionType() {
		return _commissionType;
	}
	public void setCommissionType(String type) {
		_commissionType = type;
	}
	public String getCommissionApplicable() {
		return _commissionApplicable;
	}
	public void setCommissionApplicable(String applicable) {
		_commissionApplicable = applicable;
	}
	public String getCommissionGiven() {
		return _commissionGiven;
	}
	public void setCommissionGiven(String given) {
		_commissionGiven = given;
	}
	  /**
	 * @return the _multiCurrencyDetailVO
	 */
	public String getMultiCurrencyDetailVO() {
		return _multiCurrencyDetailVO;
	}
	/**
	 * @param _multiCurrencyDetailVO the _multiCurrencyDetailVO to set
	 */
	public void setMultiCurrencyDetailVO(String _multiCurrencyDetailVO) {
		this._multiCurrencyDetailVO = _multiCurrencyDetailVO;
	}
	
    public String getLRallowed() {
		return lrAllowed;
	}

	public void setLRallowed(String lastRecharge) {
		 lrAllowed=lastRecharge;
	}
	
    public long getLRMaxAmount() {
		return lrMaxAmount;
	}

	public void setLRMaxAmount(long maxAmount) {
		lrMaxAmount=maxAmount;
	}

	public long getLRAmount() {
		return lrAmount;
	}

	public void setLRAmount(long amount) {
		lrAmount=amount;
	}

	public boolean getLRFlag() {
		return lrFlag;
	}

	public void setLRFlag(boolean flag) {
		lrFlag=flag;
	}
	
	public long getPromoBonus() {
		return promoBonus;
	}
	public void setPromoBonus(long promoBonus) {
		this.promoBonus = promoBonus;
	}
	
	private UserOTFCountsVO userOTFCountsVO=null;
	 
	 public UserOTFCountsVO getUserOTFCountsVO() {
		 return userOTFCountsVO;
	 }

	 public void setUserOTFCountsVO(UserOTFCountsVO _userOTFCountsVO) {
		 this.userOTFCountsVO = _userOTFCountsVO;
	 }
	 String gatewayCodeForReversal = null;
	 public String getGatewayCodeForReversal() {
		 return gatewayCodeForReversal;
	 }
	 public void setGatewayCodeForReversal(String given) {
		 gatewayCodeForReversal = given;
	 }
	 
	 private String _transferProfileID;
	 private String _commissionProfileSetID;
	 public String getCommissionProfileSetID() {
		 return _commissionProfileSetID;
	 }
	 public void setCommissionProfileSetID(String commissionProfileSetID) {
		 _commissionProfileSetID = commissionProfileSetID;
	 }
	 public String getTransferProfileID() {
		 return _transferProfileID;
	 }
	 public void setTransferProfileID(String transferProfileID) {
		 _transferProfileID = transferProfileID;
	 }
	 private String _lmsProfile;
	 public String getLmsProfile() {
		 return _lmsProfile;
	 }
	 public void setLmsProfile(String lmsProfile) {
		 _lmsProfile = lmsProfile;
	 }
	 private String _validationStatus;
	 public String getValidationStatus() {
		 return _validationStatus;
	 }
	 public void setValidationStatus(String validationStatus) {
		 _validationStatus = validationStatus;
	 }
	 private String _updateStatus;
	 public String getUpdateStatus() {
		 return _updateStatus;
	 }
	 public void setUpdateStatus(String updateStatus) {
		 _updateStatus = updateStatus;
	 }
	 private String _accountStatus;
	 public String getAccountStatus() {
		 return _accountStatus;
	 }
	 public void setAccountStatus(String accountStatus) {
		 _accountStatus = accountStatus;
	 }
	 private String _interfaceResponseCode;
	 public String getInterfaceResponseCode() {
		 return _interfaceResponseCode;
	 }
	 public void setInterfaceResponseCode(String interfaceResponseCode) {
		 _interfaceResponseCode = interfaceResponseCode;
	 }

	 private String _firstCall;
	 public String getFirstCall() {
		 return _firstCall;
	 }
	 public void setFirstCall(String firstCall) {
		 _firstCall = firstCall;
	 }
	
	 private String _transferType2;
	 public String getTransferType2() {
		 return _transferType2;
	 }
	 public void setTransferType2(String transferType2) {
		 _transferType2 = transferType2;
	 }
	 private String _protocolStatus;
	 public String getProtocolStatus() {
		 return _protocolStatus;
	 }
	 public void setProtocolStatus(String protocolStatus) {
		 _protocolStatus = protocolStatus;
	 }
	 private String _interfaceReferenceID2;
	 public String getInterfaceReferenceID2() {
		 return _interfaceReferenceID2;
	 }
	 public void setInterfaceReferenceID2(String interfaceReferenceID2) {
		 _interfaceReferenceID2 = interfaceReferenceID2;
	 }
	 private String _updateStatus2;
	 public String getUpdateStatus2() {
		 return _updateStatus2;
	 }
	 public void setUpdateStatus2(String updateStatus2) {
		 _updateStatus2 = updateStatus2;
	 }
	 private String _transferType1;
	 public String getTransferType1() {
		 return _transferType1;
	 }
	 public void setTransferType1(String transferType1) {
		 _transferType1 = transferType1;
	 }
	 private String _interfaceReferenceID1;
	 public String getInterfaceReferenceID1() {
		 return _interfaceReferenceID1;
	 }
	 public void setInterfaceReferenceID1(String interfaceReferenceID1) {
		 _interfaceReferenceID1 = interfaceReferenceID1;
	 }
	 private String _updateStatus1;
	 public String getUpdateStatus1() {
		 return _updateStatus1;
	 }
	 public void setUpdateStatus1(String updateStatus1) {
		 _updateStatus1 = updateStatus1;
	 }
	 private long _adjustValue;
	 public long getAdjustValue() {
		 return _adjustValue;
	 }
	 public void setAdjustValue(long adjustValue) {
		 _adjustValue = adjustValue;
	 }
	public String getVomsSerialPinAsString() {
		return vomsSerialPinAsString;
	}
	public void setVomsSerialPinAsString(String vomsSerialPinAsString) {
		this.vomsSerialPinAsString = vomsSerialPinAsString;
	}
	
	//isAddTransfer indicates whether a transfer is add(0) or update(1)
	public String isAddTransfer;

	public String getIsAddTransfer() {
		return isAddTransfer;
	}
	public void setIsAddTransfer(String isAddTransfer) {
		this.isAddTransfer = isAddTransfer;
	}
	
	 public List<UserLoanVO> getUserLoanVOList() {
			return userLoanVOList;
		}

	public void setUserLoanVOList(List<UserLoanVO> userLoanVOList) {
		this.userLoanVOList = userLoanVOList;
	}

	
	
}
