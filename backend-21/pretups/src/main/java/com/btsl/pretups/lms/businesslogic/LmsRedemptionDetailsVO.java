/**
 * @(#)LmsRedemptionDetailsVO.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                           sweta.verma March, 2018 Initital Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */

package com.btsl.pretups.lms.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.common.TypesI;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.util.PretupsBL;

/**
 * 
 * @author sweta.verma
 *
 */
 
public class LmsRedemptionDetailsVO implements Serializable{
  
    private String _networkCode;
    private String _networkCodeFor;
    private String _graphicalDomainCode;
    private String _domainCode;
    private String _categoryCode;
   
    private String _referenceNum;
   
   
    private Date _createdOn;
    private String _createdBy;

    private long _payableAmount;
    private long _netPayableAmount;
   private String _receiverCategoryCode;
    private String _requestGatewayCode;
    private String _requestGatewayType;
    private String _paymentInstSource;
    private String _productType;
    private String _transferCategory;
    private long _lastModifiedTime;
    private String _type;
    private String _transferSubType;

    private ArrayList _channelTransferitemsVOList;

    private String _grphDomainCodeDesc;
    private String _domainCodeDesc;
    private String _receiverCategoryDesc;
    private String _receiverGradeCodeDesc;
    private String _toUserName;
    private String _fromUserName;
    private String _toUserCode;
    private String _fromUserCode;
    private String _commProfileName;
    private String _receiverTxnProfileName;
    private String _senderTxnProfileName;
    private String _firstApprovedByName;
    private String _secondApprovedByName;
    private String _thirdApprovedByName;
    private String _canceledByApprovedName;
    private String _transferInitatedByName;
    private String _userMsisdn;
    private String _erpNum;
    private String _transferDateAsString;
    private String _statusDesc;
    private String _entryType;

    private String _finalApprovedBy;
    private String _finalApprovedDateAsString;

    private String _address1;
    private String _address2;
    private String _city;
    private String _state;
    private String _country;

    private String _transferCategoryCode;
    private String _transferCategoryCodeDesc;

    // field for the o2c transfer balance logger
    
    private String _transferSubTypeValue = null;

    private String _controlTransfer = null; // field to store that transfer is
    // controlled or uncontrolled.

    private String _receiverGgraphicalDomainCode; // for c2c enquiry
    private String _receiverDomainCode;// for c2c enquiry
    private String _senderCatName;// for c2c enquiry

    // fields to store foc notification message
    private String _defaultLang = null;
    private String _secondLang = null;
    private Timestamp _dbDateTime = null;
    // For Mali -- +ve commission Apply
    private long _commQty = 0;
    private long _senderDrQty = 0;
    private long _receiverCrQty = 0;
    private String _commisionTxnId = null;

    // for transfer quantity change
    private String _levelOneApprovedQuantity = null;
    private String _levelTwoApprovedQuantity = null;
    private String _levelThreeApprovedQuantity = null;
    private double _pybleAmt;
    private double _ntpybleAmt;
    private double _pyinsAmt;

    // added by vikram for vfe
    private String _activeUserId;
    private String _activeUserName;

    // to implement multiple wallet functionality
    private String _walletType = "SAL";

    // added by nilesh
    private String _email;
    private String _msisdn;
    // added by nilesh
    private String _productCode;
    private String _transferProfileID;
    private String _transactionMode = "N";
    // Added By Babu Kunwar For showing post/pre balance in C2C/O2C Transfers
    private String _senderPostStock = null;
    private String _receiverPostStock = null;
    private long _senderPreviousStock = 0;
    private long _receiverPreviousStock = 0;

    // Added by Amit Raheja for reversal trx
    private String _senderLoginID = null;
    private String _receiverLoginID = null;
    private String _refTransferID = null;
    private Date _closeDate;

    // added by gaurav for cell id and switch id
    private String _cellId = null;
    private String _switchId = null;

    // c2s reversal
    private String _senderCategory = null;
    private String _serviceClass = null;
    private ArrayList _userrevlist = null;
    private String _displayTransferMRP = null;

    // user life cycle
    private String _toChannelUserStatus = null;
    private String _fromChannelUserStatus = null;
    private String _channelUserStatus = null;
    private String userWalletCode = null;
    private String tax1Rate=null;
    private String tax1Type=null;
    private String tax2Rate=null;
    private String tax2Type=null;
    
    private long senderPostbalance;
    private long recieverPostBalance;

	
	private long sosRequestAmount;
	private boolean sosFlag;
	private long lrWithdrawAmt;
	private boolean isWeb;
	private boolean otfCountsUpdated = false;
	private boolean targetAchieved = false;
	  private String fromUserGeo;
	 private String fromOwnerGeo;
	 private String toUserGeo;
	 private String toOwnerGeo;
	 private String toMSISDN;
	 private String mrp;
	 private String modifiedOnAsString;
	 private String approvedAmount;
	 private String tax1Value;
	 private String tax2Value;
	 private String receiverCrQtyAsString;
	 private String payInstrumentDateAsString;
	 
	 
	 private String payableAmountAsString;
	 private String netPayableAmountAsString;
	 private String adjustmentID;
	 private String time;
	 
	 private String parentName;
	 private String parentMsisdn;
	 private String parentGeoName;
	 private String ownerUser;
	 private String ownerMsisdn;
	 private String ownerGeo;
	 private String ownerCat;
	 private String name;
	 private String receiverMsisdn;
	 private String commissionType;
	 private String commissionRate;  
	 private String commissionValue; 
	 private String transferAmt;
	 private String marginAmount;
	 private String marginRate;
	 private String otfType;
	 private String parentCategory;
	 private String paymentInstType; 
	  
	 

	 private String domainName;
	 private String ownerName;
	 private String transInCount;
	 private String transOutCount;
	 private String transInAmount;
	 private String fromEXTCODE;
	 private String toEXTCODE;
	 private String ownerProfile;
	 private String parentProfile;
	 private String externalTranDate;
	 private String commQtyAsString;
	 private String senderDrQtyAsString;
	 private Long payableAmounts;
	 private Long netPayableAmounts;
	 private Long approvedQuantity;
	 private String _referenceID = null;
	 private String userID;
	 private String userName;
	 private String msisdn;
	 private String categoryName;
	 private String gegoraphyDomainName;
	 private Long amountTransferred;
	 private String redemptionType;
	 private String redemptionID;
	 private String redemptionDate;
	 private String redemptionBy;
	 private String pointsRedeemed;
	 private String RedempedAmount;
	 
	 
	 
	 public String getRedemptionID() {
		return redemptionID;
	}

	public void setRedemptionID(String redemptionID) {
		this.redemptionID = redemptionID;
	}

	public String getRedemptionDate() {
		return redemptionDate;
	}

	public void setRedemptionDate(String redemptionDate) {
		this.redemptionDate = redemptionDate;
	}

	public String getRedemptionBy() {
		return redemptionBy;
	}

	public void setRedemptionBy(String redemptionBy) {
		this.redemptionBy = redemptionBy;
	}

	public String getPointsRedeemed() {
		return pointsRedeemed;
	}

	public void setPointsRedeemed(String pointsRedeemed) {
		this.pointsRedeemed = pointsRedeemed;
	}

	public String getRedempedAmount() {
		return RedempedAmount;
	}

	public void setRedempedAmount(String redempedAmount) {
		RedempedAmount = redempedAmount;
	}



	
	 public LmsRedemptionDetailsVO() {}

     public LmsRedemptionDetailsVO(LmsRedemptionDetailsVO c) {
		
		
		_networkCode =  c._networkCode;
		_networkCodeFor =  c._networkCodeFor;
		_graphicalDomainCode =  c._graphicalDomainCode;
		_domainCode =  c._domainCode;
		_categoryCode =  c._categoryCode;
		_referenceNum =  c._referenceNum;
		_createdOn =  c._createdOn;
		_createdBy =  c._createdBy;
		_payableAmount =  c._payableAmount;
		_netPayableAmount =  c._netPayableAmount;
		_receiverCategoryCode =  c._receiverCategoryCode;
		_requestGatewayCode =  c._requestGatewayCode;
		_requestGatewayType =  c._requestGatewayType;
		_paymentInstSource =  c._paymentInstSource;
		_productType =  c._productType;
		_transferCategory =  c._transferCategory;
		_lastModifiedTime =  c._lastModifiedTime;
		_type =  c._type;
		_transferSubType =  c._transferSubType;
		_channelTransferitemsVOList =  c._channelTransferitemsVOList;
		_grphDomainCodeDesc =  c._grphDomainCodeDesc;
		_domainCodeDesc =  c._domainCodeDesc;
		_receiverCategoryDesc =  c._receiverCategoryDesc;
		_receiverGradeCodeDesc =  c._receiverGradeCodeDesc;
		_toUserName =  c._toUserName;
		_fromUserName =  c._fromUserName;
		_toUserCode =  c._toUserCode;
		_fromUserCode =  c._fromUserCode;
		_commProfileName =  c._commProfileName;
		_receiverTxnProfileName =  c._receiverTxnProfileName;
		_senderTxnProfileName =  c._senderTxnProfileName;
		_firstApprovedByName =  c._firstApprovedByName;
		_secondApprovedByName =  c._secondApprovedByName;
		_thirdApprovedByName =  c._thirdApprovedByName;
		_canceledByApprovedName =  c._canceledByApprovedName;
		_transferInitatedByName =  c._transferInitatedByName;
		_userMsisdn =  c._userMsisdn;
		_erpNum =  c._erpNum;
		_transferDateAsString =  c._transferDateAsString;
		_statusDesc =  c._statusDesc;
		_entryType =  c._entryType;
		_finalApprovedBy =  c._finalApprovedBy;
		_finalApprovedDateAsString =  c._finalApprovedDateAsString;
		_address1 =  c._address1;
		_address2 =  c._address2;
		_city =  c._city;
		_state =  c._state;
		_country =  c._country;
		_transferCategoryCode =  c._transferCategoryCode;
		_transferCategoryCodeDesc =  c._transferCategoryCodeDesc;
		_referenceID =  c._referenceID;
		_transferSubTypeValue =  c._transferSubTypeValue;
		_controlTransfer =  c._controlTransfer;
		_receiverGgraphicalDomainCode =  c._receiverGgraphicalDomainCode;
		_receiverDomainCode =  c._receiverDomainCode;
		_senderCatName =  c._senderCatName;
		_defaultLang =  c._defaultLang;
		_secondLang =  c._secondLang;
		_dbDateTime =  c._dbDateTime;
		_commQty =  c._commQty;
		_senderDrQty =  c._senderDrQty;
		_receiverCrQty =  c._receiverCrQty;
		_commisionTxnId =  c._commisionTxnId;
		_levelOneApprovedQuantity =  c._levelOneApprovedQuantity;
		_levelTwoApprovedQuantity =  c._levelTwoApprovedQuantity;
		_levelThreeApprovedQuantity =  c._levelThreeApprovedQuantity;
		_pybleAmt =  c._pybleAmt;
		_ntpybleAmt =  c._ntpybleAmt;
		_pyinsAmt =  c._pyinsAmt;
		_activeUserId =  c._activeUserId;
		_activeUserName =  c._activeUserName;
		_walletType =  c._walletType;
		_email =  c._email;
		_msisdn =  c._msisdn;
		_productCode =  c._productCode;
		_transferProfileID =  c._transferProfileID;
		_transactionMode =  c._transactionMode;
		_senderPostStock =  c._senderPostStock;
		_receiverPostStock =  c._receiverPostStock;
		_senderPreviousStock =  c._senderPreviousStock;
		_receiverPreviousStock =  c._receiverPreviousStock;
		_senderLoginID =  c._senderLoginID;
		_receiverLoginID =  c._receiverLoginID;
		_refTransferID =  c._refTransferID;
		_closeDate =  c._closeDate;
		_cellId =  c._cellId;
		_switchId =  c._switchId;
		_senderCategory =  c._senderCategory;
		_serviceClass =  c._serviceClass;
		_userrevlist =  c._userrevlist;
		_displayTransferMRP =  c._displayTransferMRP;
		_toChannelUserStatus =  c._toChannelUserStatus;
		_fromChannelUserStatus =  c._fromChannelUserStatus;
		_channelUserStatus =  c._channelUserStatus;
		userWalletCode =  c.userWalletCode;
		senderPostbalance =  c.senderPostbalance;
		recieverPostBalance =  c.recieverPostBalance;
		sosRequestAmount =  c.sosRequestAmount;
		sosFlag =  c.sosFlag;
		lrWithdrawAmt =  c.lrWithdrawAmt;
		isWeb =  c.isWeb;
		otfCountsUpdated =  c.otfCountsUpdated;
		targetAchieved =  c.targetAchieved;
		fromUserGeo =  c.fromUserGeo;
		fromOwnerGeo =  c.fromOwnerGeo;
		toUserGeo =  c.toUserGeo;
		toOwnerGeo =  c.toOwnerGeo;
		toMSISDN =  c.toMSISDN;
		mrp =  c.mrp;
		modifiedOnAsString =  c.modifiedOnAsString;
		approvedAmount =  c.approvedAmount;
		tax1Value =  c.tax1Value;
		tax2Value =  c.tax2Value;
		receiverCrQtyAsString =  c.receiverCrQtyAsString;
		payInstrumentDateAsString =  c.payInstrumentDateAsString;
		payableAmountAsString =  c.payableAmountAsString;
		netPayableAmountAsString =  c.netPayableAmountAsString;
		adjustmentID =  c.adjustmentID;
		time =  c.time;
		userName =  c.userName;
		msisdn =  c.msisdn;
		categoryName =  c.categoryName;
		gegoraphyDomainName =  c.gegoraphyDomainName;
		parentName =  c.parentName;
		parentMsisdn =  c.parentMsisdn;
		parentGeoName =  c.parentGeoName;
		ownerUser =  c.ownerUser;
		ownerMsisdn =  c.ownerMsisdn;
		ownerGeo =  c.ownerGeo;
		ownerCat =  c.ownerCat;
		name =  c.name;
		receiverMsisdn =  c.receiverMsisdn;
		commissionType =  c.commissionType;
		transferAmt =  c.transferAmt;
		marginAmount =  c.marginAmount;
		marginRate =  c.marginRate;
		otfType =  c.otfType;
		parentCategory =  c.parentCategory;
		domainName =  c.domainName;
		ownerName =  c.ownerName;
		transInCount =  c.transInCount;
		transOutCount =  c.transOutCount;
		transInAmount =  c.transInAmount;
		fromEXTCODE =  c.fromEXTCODE;
		toEXTCODE =  c.toEXTCODE;
		ownerProfile =  c.ownerProfile;
		parentProfile =  c.parentProfile;
		externalTranDate =  c.externalTranDate;
		commQtyAsString =  c.commQtyAsString;
		senderDrQtyAsString =  c.senderDrQtyAsString;
		tax3ValueAsString =  c.tax3ValueAsString;
		transOutAmount =  c.transOutAmount;
		commProfileDetailID =  c.commProfileDetailID;
		lrFlag =  c.lrFlag;
		sosStatus =  c.sosStatus;
		sosTxnId =  c.sosTxnId;
		sosProductCode =  c.sosProductCode;
		transactionCode =  c.transactionCode;
		lrStatus =  c.lrStatus;
		info1 =  c.info1;
		info2 =  c.info2;
		otfFlag =  c.otfFlag;
		otfTypePctOrAMt =  c.otfTypePctOrAMt;
		otfRate =  c.otfRate;
		otfAmount =  c.otfAmount;
		channelSoSVOList =  c.channelSoSVOList;
		sosSettlementDate =  c.sosSettlementDate;
		_subSid =  c._subSid;
		_stockUpdated =  c._stockUpdated;
		external_code =  c.external_code;
		grand_msisdn =  c.grand_msisdn;
		productName =  c.productName;
		grandName =  c.grandName;
		grandGeo =  c.grandGeo;
		openingBalance =  c.openingBalance;
		stockBought =  c.stockBought;
		stockReturn =  c.stockReturn;
		channelTransfer =  c.channelTransfer;
		channelReturn =  c.channelReturn;
		c2sTransfers =  c.c2sTransfers;
		closingBalance =  c.closingBalance;
		reconStatus =  c.reconStatus;
		netBalance =  c.netBalance;
		netLifting =  c.netLifting;
	}
	 
     
	
     
     
	
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Long getApprovedQuantity() {
		return approvedQuantity;
	}

	public void setApprovedQuantity(Long approvedQuantity) {
		this.approvedQuantity = approvedQuantity;
	}

	/**
	 * @return the payableAmounts
	 */
	public Long getPayableAmounts() {
		return payableAmounts;
	}

	/**
	 * @param payableAmounts the payableAmounts to set
	 */
	public void setPayableAmounts(Long payableAmounts) {
		this.payableAmounts = payableAmounts;
	}

	/**
	 * @return the netPayableAmounts
	 */
	public Long getNetPayableAmounts() {
		return netPayableAmounts;
	}

	/**
	 * @param netPayableAmounts the netPayableAmounts to set
	 */
	public void setNetPayableAmounts(Long netPayableAmounts) {
		this.netPayableAmounts = netPayableAmounts;
	}

	public String getPaymentInstType() {
		return paymentInstType;
	}

	public void setPaymentInstType(String paymentInstType) {
		this.paymentInstType = paymentInstType;
	}

	public String getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(String commissionRate) {
		this.commissionRate = commissionRate;
	}

	public String getCommissionValue() {
		return commissionValue;
	}

	public void setCommissionValue(String commissionValue) {
		this.commissionValue = commissionValue;
	}

	public String getTax1Rate() {
		return tax1Rate;
	}

	public void setTax1Rate(String tax1Rate) {
		this.tax1Rate = tax1Rate;
	}

	public String getTax1Type() {
		return tax1Type;
	}

	public void setTax1Type(String tax1Type) {
		this.tax1Type = tax1Type;
	}

	public String getTax2Rate() {
		return tax2Rate;
	}

	public void setTax2Rate(String tax2Rate) {
		this.tax2Rate = tax2Rate;
	}

	public String getTax2Type() {
		return tax2Type;
	}

	public void setTax2Type(String tax2Type) {
		this.tax2Type = tax2Type;
	}

	public String getSenderDrQtyAsString() {
		return senderDrQtyAsString;
	}

	public void setSenderDrQtyAsString(String senderDrQtyAsString) {
		this.senderDrQtyAsString = senderDrQtyAsString;
	}

	public String getTax3ValueAsString() {
		return tax3ValueAsString;
	}

	public void setTax3ValueAsString(String tax3ValueAsString) {
		this.tax3ValueAsString = tax3ValueAsString;
	}



	private String tax3ValueAsString;
	 
	 public String getExternalTranDate() {
		 return externalTranDate;
	 }

	 public String getCommQtyAsString() {
		return commQtyAsString;
	}

	public void setCommQtyAsString(String commQtyAsString) {
		this.commQtyAsString = commQtyAsString;
	}

	public void setExternalTranDate(String externalTranDate) {
		 this.externalTranDate = externalTranDate;
	 }
	 
	public String getOwnerProfile() {
		return ownerProfile;
	}

	public void setOwnerProfile(String ownerProfile) {
		this.ownerProfile = ownerProfile;
	}

	public String getParentProfile() {
		return parentProfile;
	}

	public void setParentProfile(String parentProfile) {
		this.parentProfile = parentProfile;
	}

	public String getFromEXTCODE() {
		return fromEXTCODE;
	}

	public void setFromEXTCODE(String fromEXTCODE) {
		this.fromEXTCODE = fromEXTCODE;
	}

	public String getToEXTCODE() {
		return toEXTCODE;
	}

	public void setToEXTCODE(String toEXTCODE) {
		this.toEXTCODE = toEXTCODE;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getTransInCount() {
		return transInCount;
	}

	public void setTransInCount(String transInCount) {
		this.transInCount = transInCount;
	}

	public String getTransOutCount() {
		return transOutCount;
	}

	public void setTransOutCount(String transOutCount) {
		this.transOutCount = transOutCount;
	}

	public String getTransInAmount() {
		return transInAmount;
	}

	public void setTransInAmount(String transInAmount) {
		this.transInAmount = transInAmount;
	}

	public String getTransOutAmount() {
		return transOutAmount;
	}

	public void setTransOutAmount(String transOutAmount) {
		this.transOutAmount = transOutAmount;
	}



	private String transOutAmount;

	 

	 public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}

	public String getMarginAmount() {
		return marginAmount;
	}

	public void setMarginAmount(String marginAmount) {
		this.marginAmount = marginAmount;
	}

	public String getMarginRate() {
		return marginRate;
	}

	public void setMarginRate(String marginRate) {
		this.marginRate = marginRate;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentMsisdn() {
		return parentMsisdn;
	}

	public void setParentMsisdn(String parentMsisdn) {
		this.parentMsisdn = parentMsisdn;
	}

	public String getParentGeoName() {
		return parentGeoName;
	}

	public void setParentGeoName(String parentGeoName) {
		this.parentGeoName = parentGeoName;
	}

	public String getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(String ownerUser) {
		this.ownerUser = ownerUser;
	}

	public String getOwnerMsisdn() {
		return ownerMsisdn;
	}

	public void setOwnerMsisdn(String ownerMsisdn) {
		this.ownerMsisdn = ownerMsisdn;
	}

	public String getOwnerGeo() {
		return ownerGeo;
	}

	public void setOwnerGeo(String ownerGeo) {
		this.ownerGeo = ownerGeo;
	}

	public String getOwnerCat() {
		return ownerCat;
	}

	public void setOwnerCat(String ownerCat) {
		this.ownerCat = ownerCat;
	}

	public String getReceiverMsisdn() {
		return receiverMsisdn;
	}

	public void setReceiverMsisdn(String receiverMsisdn) {
		this.receiverMsisdn = receiverMsisdn;
	}

	public String getTransferAmt() {
		return transferAmt;
	}

	public void setTransferAmt(String transferAmt) {
		this.transferAmt = transferAmt;
	}

	public String getOtfType() {
		return otfType;
	}

	public void setOtfType(String otfType) {
		this.otfType = otfType;
	}

	public String getGegoraphyDomainName() {
		return gegoraphyDomainName;
	}

	public void setGegoraphyDomainName(String gegoraphyDomainName) {
		this.gegoraphyDomainName = gegoraphyDomainName;
	}

	public String getCategoryName() {
		 return categoryName;
	 }
	 
	 public void setCategoryName(String categoryName) {
		 this.categoryName = categoryName;
	 }
	 
	 public String getMsisdn() {
		 return msisdn;
	 }
	 
	 public void setMsisdn(String msisdn) {
		 this.msisdn = msisdn;
	 }
	 
	 public String getUserName() {
		 return userName;
	 }
	 
	 public void setUserName(String userName) {
		 this.userName = userName;
	 }
	  
	 public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAdjustmentID() {
		return adjustmentID;
	}

	public void setAdjustmentID(String adjustmentID) {
		this.adjustmentID = adjustmentID;
	}

	public void setPayInstrumentDateAsString(String payInstrumentDateAsString)
	 {
		 this.payInstrumentDateAsString = payInstrumentDateAsString;
	 }
	 
	 public String getPayInstrumentDateAsString()
	 {
		 return payInstrumentDateAsString;
	 }
		public void setNetPayableAmountAsStr(String netPayableAmountAsString) {
			this.netPayableAmountAsString = netPayableAmountAsString;
		}
		public String getNetPayableAmountAsStr() {
			return netPayableAmountAsString;
		}
		public void setPayableAmountAsStr(String payableAmountAsString) {
			this.payableAmountAsString = payableAmountAsString;
		}
		public String getPayableAmountAsStr()
		{
			return payableAmountAsString;
		}
	 public String getReceiverCrQtyAsString() {
			return receiverCrQtyAsString;
		}
		public void setReceiverCrQtyAsString(String receiverCrQtyAsString) {
			this.receiverCrQtyAsString = receiverCrQtyAsString;
		}
	 public String getTax1Value() {
			return tax1Value;
		}
		public void setTax1ValueAsString(String tax1Value) {
			this.tax1Value = tax1Value;
		}
		public String getTax2Value() {
			return tax2Value;
		}
		public void setTax2ValueAsString(String tax2Value) {
			this.tax2Value = tax2Value;
		}
	 
	 public String getApprovedAmount() {
			return approvedAmount;
		}
		public void setApprovedAmountAsString(String approvedAmount) {
			this.approvedAmount = approvedAmount;
		}

	 public String getModifiedOnAsString() {
		return modifiedOnAsString;
	}
	public void setModifiedOnAsString(String modifiedOnAsString) {
		this.modifiedOnAsString = modifiedOnAsString;
	}
	public String getFromUserGeo() {
		return fromUserGeo;
	}
	public void setFromUserGeo(String fromUserGeo) {
		this.fromUserGeo = fromUserGeo;
	}
	public String getFromOwnerGeo() {
		return fromOwnerGeo;
	}
	public void setFromOwnerGeo(String fromOwnerGeo) {
		this.fromOwnerGeo = fromOwnerGeo;
	}
	public String getToUserGeo() {
		return toUserGeo;
	}
	public void setToUserGeo(String toUserGeo) {
		this.toUserGeo = toUserGeo;
	}
	public String getToOwnerGeo() {
		return toOwnerGeo;
	}
	public void setToOwnerGeo(String toOwnerGeo) {
		this.toOwnerGeo = toOwnerGeo;
	}
	public String getToMSISDN() {
		return toMSISDN;
	}
	public void setToMSISDN(String toMSISDN) {
		this.toMSISDN = toMSISDN;
	}
	public String getMrp() {
		return mrp;
	}
	public void setMrp(String mrp) {
		this.mrp = mrp;
	}



	private String commProfileDetailID;
	
	 
	public String getCommProfileDetailID() {
		return commProfileDetailID;
	}
	public void setCommProfileDetailID(String commProfileDetailID) {
		this.commProfileDetailID = commProfileDetailID;
	}
	
	public boolean isTargetAchieved() {
		return targetAchieved;
	}
	public void setTargetAcheived(boolean targetAchieved) {
		this.targetAchieved = targetAchieved;
	}
	public boolean isOtfCountsUpdated() {
		return otfCountsUpdated;
	}
	public void setOtfCountsUpdated(boolean otfCountsUpdated) {
		this.otfCountsUpdated = otfCountsUpdated;
	}



	private boolean lrFlag;

	public boolean isWeb() {
		return isWeb;
	}
	public void setWeb(boolean isWeb) {
		this.isWeb = isWeb;
	}



	
	public long getLrWithdrawAmt() {
		return lrWithdrawAmt;
	}
	public void setLrWithdrawAmt(long lrWithdrawAmt) {
		this.lrWithdrawAmt = lrWithdrawAmt;
	}
	public long getSosRequestAmount() {
		return sosRequestAmount;
	}
	public void setSosRequestAmount(long sosRequestAmount) {
		this.sosRequestAmount = sosRequestAmount;
	}
	

    
    private String sosStatus = null;
    private String sosTxnId = null;
    private String sosProductCode = null;
    private String transactionCode;
    private String lrStatus = null;


	//O2C and C2C info tag by Anjali
    private String info1=null;
    private String info2=null;
    
    private boolean otfFlag;
    private String otfTypePctOrAMt;
	private double otfRate;
    private long otfAmount;
    
    public String getOtfTypePctOrAMt() {
		return otfTypePctOrAMt;
	}
	public void setOtfTypePctOrAMt(String otfType) {
		this.otfTypePctOrAMt = otfType;
	}
	public double getOtfRate() {
		return otfRate;
	}
	public void setOtfRate(double otfRate) {
		this.otfRate = otfRate;
	}
	public long getOtfAmount() {
		return otfAmount;
	}
	public void setOtfAmount(long otfAmount) {
		this.otfAmount = otfAmount;
	}





	public boolean isOtfFlag() {
		return otfFlag;
	}
	public void setOtfFlag(boolean otfFlag) {
		this.otfFlag = otfFlag;
	}
	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}
    
    public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}



	private List<ChannelSoSVO> channelSoSVOList;

	   private Date sosSettlementDate;
    
    public Date getSosSettlementDate() {
		return sosSettlementDate;
	}

	public void setSosSettlementDate(Date sosSettlementDate) {
		this.sosSettlementDate = sosSettlementDate;
	}

	public List<ChannelSoSVO> getChannelSoSVOList() {
		return channelSoSVOList;
	}

	public void setChannelSoSVOList(List<ChannelSoSVO> channelSoSVOList) {
		this.channelSoSVOList = channelSoSVOList;
	}

	public long getRecieverPostBalance() {
		return recieverPostBalance;
	}

	public void setRecieverPostBalance(long recieverPostBalance) {
		this.recieverPostBalance = recieverPostBalance;
	}

	public long getSenderPostbalance() {
		return senderPostbalance;
	}

	public void setSenderPostbalance(long senderPostbalance) {
		this.senderPostbalance = senderPostbalance;
	}
	//added for multi wallet o2c reverse
	private boolean _reversalFlag= false;
    
    private  String _subSid=null;

    public String getCellId() {
        return _cellId;
    }

    /**
     * @param _cellid
     *            the _cellID to set
     */
    public void setCellId(String _cellid) {
        _cellId = _cellid;
    }

    /**
     * @return the _switchID
     */
    public String getSwitchId() {
        return _switchId;
    }

    /**
     * @param _switchid
     *            the _switchID to set
     */
    public void setSwitchId(String _switchid) {
        _switchId = _switchid;
    }

    /**
     * 
     * @return receiverPostStock
     */
    public String getReceiverPostStock() {
        return _receiverPostStock;
    }

    /**
     * 
     * @param receiverPostStock
     */
    public void setReceiverPostStock(String receiverPostStock) {
        _receiverPostStock = receiverPostStock;
    }

    /**
     * 
     * @return receiverPreviousStock
     */
    public long getReceiverPreviousStock() {
        return _receiverPreviousStock;
    }

    public void setReceiverPreviousStock(long receiverPreviousStock) {
        _receiverPreviousStock = receiverPreviousStock;
    }

    /**
     * 
     * @return senderPostStock
     */
    public String getSenderPostStock() {
        return _senderPostStock;
    }

    public void setSenderPostStock(String senderPostStock) {
        _senderPostStock = senderPostStock;
    }

    /**
     * @return _senderPreviousStock
     */
    public long getSenderPreviousStock() {
        return _senderPreviousStock;
    }

    public void setSenderPreviousStock(long senderPreviousStock) {
        _senderPreviousStock = senderPreviousStock;
    }

    /**
     * @return the walletType
     */
    public String getWalletType() {
        return _walletType;
    }

    /**
     * @param walletType
     *            the walletType to set
     */
    public void setWalletType(String walletType) {
        _walletType = walletType;
    }

    /**
     * @return Returns the defaultLang.
     */
    public String getDefaultLang() {
        return _defaultLang;
    }

    /**
     * @param defaultLang
     *            The defaultLang to set.
     */
    public void setDefaultLang(String defaultLang) {
        _defaultLang = defaultLang;
    }

    /**
     * @return Returns the secondLang.
     */
    public String getSecondLang() {
        return _secondLang;
    }

    /**
     * @param secondLang
     *            The secondLang to set.
     */
    public void setSecondLang(String secondLang) {
        _secondLang = secondLang;
    }

    public String getErpNum() {
        return _erpNum;
    }

    public void setErpNum(String erpNum) {
        _erpNum = erpNum;
    }

    public String getReceiverCategoryCode() {
        return _receiverCategoryCode;
    }

    public void setReceiverCategoryCode(String receiverCategoryCode) {
        _receiverCategoryCode = receiverCategoryCode;
    }

    public String getRequestGatewayCode() {
        return _requestGatewayCode;
    }

    public void setRequestGatewayCode(String requestGatewayCode) {
        _requestGatewayCode = requestGatewayCode;
    }

    public String getRequestGatewayType() {
        return _requestGatewayType;
    }

    public void setRequestGatewayType(String requestGateWayType) {
        _requestGatewayType = requestGateWayType;
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

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

   

    public String getGraphicalDomainCode() {
        return _graphicalDomainCode;
    }

    public void setGraphicalDomainCode(String graphicalDomainCode) {
        _graphicalDomainCode = graphicalDomainCode;
    }
   
    public long getNetPayableAmount() {
        return _netPayableAmount;
    }

    public String getNetPayableAmountAsString() {
        return PretupsBL.getDisplayAmount(_netPayableAmount);
    }

    public void setNetPayableAmount(long netPayableAmount) {
        _netPayableAmount = netPayableAmount;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public long getPayableAmount() {
        return _payableAmount;
    }

    public void setPayableAmount(long payableAmount) {
        _payableAmount = payableAmount;
    }

    public String getReferenceNum() {
        return _referenceNum;
    }

    public void setReferenceNum(String refrenceNum) {
        _referenceNum = refrenceNum;
    }

     public String getNetworkCodeFor() {
        return _networkCodeFor;
    }

    public void setNetworkCodeFor(String roamNetworkCode) {
        _networkCodeFor = roamNetworkCode;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this._categoryCode = categoryCode;
    }

    public ArrayList getChannelTransferitemsVOList() {
        return _channelTransferitemsVOList;
    }

    public void setChannelTransferitemsVOList(ArrayList channelTransferitemsVO) {
        _channelTransferitemsVOList = channelTransferitemsVO;
    }

  
    @Override
	public String toString() {
        final StringBuffer sbf = new StringBuffer(" networkCode  " + _networkCode + ", ");
        sbf.append(" roamNetworkCode  " + _networkCodeFor + ", ");
        sbf.append(" graphicalDomainCode  " + _graphicalDomainCode + ", ");
        sbf.append(" domainCode  " + _domainCode + ", ");
        sbf.append(" categoryCode  " + _categoryCode + ", ");
        sbf.append(" createdOn  " + _createdOn + ", ");
        sbf.append(" createdBy  " + _createdBy + ", ");
        sbf.append(" payableAmount  " + _payableAmount + ", ");
        sbf.append(" netPayableAmount  " + _netPayableAmount + ", ");
        sbf.append(" receiverCategoryCode  " + _receiverCategoryCode + ", ");
        sbf.append(" requestGatewayCode  " + _requestGatewayCode + ", ");
        sbf.append(" requestGateWayType  " + _requestGatewayType + ", ");
        sbf.append(" _controlTransfer= " + _controlTransfer);
        sbf.append(" , _receiverGgraphicalDomainCode = " + _receiverGgraphicalDomainCode);
        sbf.append(" , _receiverDomainCode=" + _receiverDomainCode);
        sbf.append(" ,commQty " + _commQty);
        sbf.append(" ,_senderDrQty" + _senderDrQty);
        sbf.append(" ,_receiverCrQty" + _receiverCrQty);
        sbf.append(" ,_commisionTxnId" + _commisionTxnId);
        sbf.append(", levelOneApprovedQuantity=" + _levelOneApprovedQuantity);
        sbf.append(", levelTwoApprovedQuantity=" + _levelTwoApprovedQuantity);
        sbf.append(", levelThreeApprovedQuantity=" + _levelThreeApprovedQuantity);
        sbf.append(", pybleAmt=" + _pybleAmt);
        sbf.append(", _ntpybleAmt=" + _ntpybleAmt);
        sbf.append(", _pyinsAmt=" + _pyinsAmt);
        sbf.append(", _refTransferID" + _refTransferID);
        sbf.append(", _stockUpdated" +_stockUpdated);
        
        return sbf.toString();
    }

    public String getPaymentInstSource() {
        return _paymentInstSource;
    }

    public void setPaymentInstSource(String paymentInstSource) {
        _paymentInstSource = paymentInstSource;
    }

    public String getCanceledByApprovedName() {
        return _canceledByApprovedName;
    }

    public void setCanceledByApprovedName(String cancledByApprovedName) {
        _canceledByApprovedName = cancledByApprovedName;
    }

    public String getCommProfileName() {
        return _commProfileName;
    }

    public void setCommProfileName(String commProfileName) {
        _commProfileName = commProfileName;
    }

    public String getDomainCodeDesc() {
        return _domainCodeDesc;
    }

    public void setDomainCodeDesc(String domainCodeDesc) {
        _domainCodeDesc = domainCodeDesc;
    }

    public String getFirstApprovedByName() {
        return _firstApprovedByName;
    }

    public void setFirstApprovedByName(String firstApprovedByName) {
        _firstApprovedByName = firstApprovedByName;
    }

    public String getReceiverTxnProfileName() {
        return _receiverTxnProfileName;
    }

    public void setReceiverTxnProfileName(String receiverTxnProfileName) {
        _receiverTxnProfileName = receiverTxnProfileName;
    }

    public String getSecondApprovedByName() {
        return _secondApprovedByName;
    }

    public void setSecondApprovedByName(String secondApprovedByName) {
        _secondApprovedByName = secondApprovedByName;
    }

    public String getReceiverCategoryDesc() {
        return _receiverCategoryDesc;
    }

    public void setReceiverCategoryDesc(String receiverCategoryDesc) {
        _receiverCategoryDesc = receiverCategoryDesc;
    }

    public String getReceiverGradeCodeDesc() {
        return _receiverGradeCodeDesc;
    }

    public void setReceiverGradeCodeDesc(String receiverGradeCodeDesc) {
        _receiverGradeCodeDesc = receiverGradeCodeDesc;
    }

    public String getThirdApprovedByName() {
        return _thirdApprovedByName;
    }

    public void setThirdApprovedByName(String thirdApprovedByName) {
        _thirdApprovedByName = thirdApprovedByName;
    }

    public String getToUserName() {
        return _toUserName;
    }

    public void setToUserName(String toUserName) {
        _toUserName = toUserName;
    }

    public String getTransferInitatedByName() {
        return _transferInitatedByName;
    }

    public void setTransferInitatedByName(String transferInitatedByName) {
        _transferInitatedByName = transferInitatedByName;
    }

    public String getGrphDomainCodeDesc() {
        return _grphDomainCodeDesc;
    }

    public void setGrphDomainCodeDesc(String grphDomainCodeDesc) {
        _grphDomainCodeDesc = grphDomainCodeDesc;
    }

   
    public String getProductType() {
        return _productType;
    }

    public void setProductType(String productType) {
        _productType = productType;
    }

    public String getUserMsisdn() {
        return _userMsisdn;
    }

    public void setUserMsisdn(String userMsisdn) {
        _userMsisdn = userMsisdn;
    }

    public String getTransferCategory() {
        return _transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
    }

    public String getPayableAmountAsString() {
        return PretupsBL.getDisplayAmount(_payableAmount);
    }

    
    public String getTransferDateAsString() {
        return _transferDateAsString;
    }

    public void setTransferDateAsString(String transferDateAsString) {
        this._transferDateAsString = transferDateAsString;
    }

    public String getFromUserName() {
        return _fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        _fromUserName = fromUserName;
    }

    public String getFinalApprovedBy() {
        return _finalApprovedBy;
    }

    public void setFinalApprovedBy(String finalApprovedBy) {
        _finalApprovedBy = finalApprovedBy;
    }

    public String getFinalApprovedDateAsString() {
        return _finalApprovedDateAsString;
    }

    public void setFinalApprovedDateAsString(String finalApprovedDateAsString) {
        _finalApprovedDateAsString = finalApprovedDateAsString;
    }

    public String getStatusDesc() {
        return _statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    public String getTransferSubType() {
        return _transferSubType;
    }

    public void setTransferSubType(String transferSubType) {
        _transferSubType = transferSubType;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getSenderTxnProfileName() {
        return _senderTxnProfileName;
    }

    public void setSenderTxnProfileName(String senderTxnProfileName) {
        _senderTxnProfileName = senderTxnProfileName;
    }

    public String getFromUserCode() {
        return _fromUserCode;
    }

    public void setFromUserCode(String fromUserCode) {
        _fromUserCode = fromUserCode;
    }

    public String getToUserCode() {
        return _toUserCode;
    }

    public void setToUserCode(String toUserCode) {
        _toUserCode = toUserCode;
    }

    public String getEntryType() {
        return _entryType;
    }

    public void setEntryType(String entryType) {
        _entryType = entryType;
    }

    public String getFullAddress() {

        final StringBuffer address = new StringBuffer();

        if (_address1 != null) {
            address.append(_address1);
        }
        if (_address2 != null) {
            address.append(" ");
            address.append(_address2);
        }
        if (_city != null) {
            address.append(" ");
            address.append(_city);
        }
        if (_state != null) {
            address.append(" ");
            address.append(_state);
        }
        if (_country != null) {
            address.append(" ");
            address.append(_country);
        }
        return address.toString();
    }

    public String getAddress1() {
        return _address1;
    }

    public void setAddress1(String address1) {
        _address1 = address1;
    }

    public String getAddress2() {
        return _address2;
    }

    public void setAddress2(String address2) {
        _address2 = address2;
    }

    public String getCity() {
        return _city;
    }

    public void setCity(String city) {
        _city = city;
    }

    public String getState() {
        return _state;
    }

    public void setState(String state) {
        _state = state;
    }

    public String getTransferCategoryCode() {
        return _transferCategoryCode;
    }

    public void setTransferCategoryCode(String transferCategoryCode) {
        _transferCategoryCode = transferCategoryCode;
    }

    public String getTransferCategoryCodeDesc() {
        return _transferCategoryCodeDesc;
    }

    public void setTransferCategoryCodeDesc(String transferCategoryCodeDesc) {
        _transferCategoryCodeDesc = transferCategoryCodeDesc;
    }

    public String getCountry() {
        return _country;
    }

    public void setCountry(String country) {
        _country = country;
    }

    public String getReferenceID() {
        return _referenceID;
    }

    public void setReferenceID(String referenceID) {
        _referenceID = referenceID;
    }

    public String getTransferSubTypeValue() {
        return _transferSubTypeValue;
    }

    public void setTransferSubTypeValue(String transferSubTypeValue) {
        _transferSubTypeValue = transferSubTypeValue;
    }

    public String getControlTransfer() {
        return _controlTransfer;
    }

    public void setControlTransfer(String controlTransfer) {
        _controlTransfer = controlTransfer;
    }

    public String getReceiverDomainCode() {
        return _receiverDomainCode;
    }

    public void setReceiverDomainCode(String receiverDomainCode) {
        _receiverDomainCode = receiverDomainCode;
    }

    public String getReceiverGgraphicalDomainCode() {
        return _receiverGgraphicalDomainCode;
    }

    public void setReceiverGgraphicalDomainCode(String receiverGgraphicalDomainCode) {
        _receiverGgraphicalDomainCode = receiverGgraphicalDomainCode;
    }

    public String getSenderCatName() {
        return _senderCatName;
    }

    public void setSenderCatName(String senderCatName) {
        _senderCatName = senderCatName;
    }

    public Timestamp getDBDateTime() {
        return _dbDateTime;
    }

    public void setDBDateTime(Timestamp dbDateTime) {
        _dbDateTime = dbDateTime;
    }

    /**
     * @return Returns the commissionQuantity.
     */
    public long getCommQty() {
        return _commQty;
    }

    /**
     * @return Returns the receiverCrQty.
     */
    public long getReceiverCrQty() {
        return _receiverCrQty;
    }

    /**
     * @return Returns the senderDrQty.
     */
    public long getSenderDrQty() {
        return _senderDrQty;
    }

    /**
     * @param commissionQuantity
     *            The commissionQuantity to set.
     */
    public void setCommQty(long commQty) {
        _commQty = commQty;
    }

    /**
     * @param receiverCrQty
     *            The receiverCrQty to set.
     */
    public void setReceiverCrQty(long receiverCrQty) {
        _receiverCrQty = receiverCrQty;
    }

    /**
     * @param senderDrQty
     *            The senderDrQty to set.
     */
    public void setSenderDrQty(long senderDrQty) {
        _senderDrQty = senderDrQty;
    }

    /**
     * @return Returns the commisionTxnId.
     */
    public String getCommisionTxnId() {
        return _commisionTxnId;
    }

    /**
     * @param commisionTxnId
     *            The commisionTxnId to set.
     */
    public void setCommisionTxnId(String commisionTxnId) {
        _commisionTxnId = commisionTxnId;
    }

    public String getLevelOneApprovedQuantity() {
        return _levelOneApprovedQuantity;
    }

    public void setLevelOneApprovedQuantity(String levelOneApprovedQuantity) {
        _levelOneApprovedQuantity = levelOneApprovedQuantity;
    }

    public String getLevelThreeApprovedQuantity() {
        return _levelThreeApprovedQuantity;
    }

    public void setLevelThreeApprovedQuantity(String levelThreeApprovedQuantity) {
        _levelThreeApprovedQuantity = levelThreeApprovedQuantity;
    }

    public String getLevelTwoApprovedQuantity() {
        return _levelTwoApprovedQuantity;
    }

    public void setLevelTwoApprovedQuantity(String levelTwoApprovedQuantity) {
        _levelTwoApprovedQuantity = levelTwoApprovedQuantity;
    }

    public double getNtpybleAmt() {
        return _ntpybleAmt;
    }

    public void setNtpybleAmt(double ntpybleAmt) {
        _ntpybleAmt = ntpybleAmt;
    }

    public double getPybleAmt() {
        return _pybleAmt;
    }

    public void setPybleAmt(double pybleAmt) {
        _pybleAmt = pybleAmt;
    }

    public double getPyinsAmt() {
        return _pyinsAmt;
    }

    public void setPyinsAmt(double pyinsAmt) {
        _pyinsAmt = pyinsAmt;
    }

    /**
     * @return the activeUserId
     */
    public String getActiveUserId() {
        return _activeUserId;
    }

    /**
     * @param activeUserId
     *            the activeUserId to set
     */
    public void setActiveUserId(String activeUserId) {
        _activeUserId = activeUserId;
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
        _activeUserName = activeUserName;
    }

    // added by nilesh
    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    public String getToUserMsisdn() {
        return _msisdn;
    }

    public void setToUserMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    // added by nilesh

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setTransferProfileID(String transferProfileID) {
        _transferProfileID = transferProfileID;
    }

    public String getTransferProfileID() {
        return _transferProfileID;
    }

    public String getTransactionMode() {
        return _transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        _transactionMode = transactionMode;
    }

    public String getSenderLoginID() {
        return _senderLoginID;
    }

    public void setSenderLoginID(String loginID) {
        _senderLoginID = loginID;
    }

    public String getReceiverLoginID() {
        return _receiverLoginID;
    }

    public void setReceiverLoginID(String loginID) {
        _receiverLoginID = loginID;
    }

    public String getRefTransferID() {
        return _refTransferID;
    }

    public void setRefTransferID(String transferID) {
        _refTransferID = transferID;
    }

    public Date getCloseDate() {
        return _closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this._closeDate = closeDate;
    }

    // c2sreversal

    public String getSenderCategory() {
        return _senderCategory;
    }

    public void setSenderCategory(String category) {
        _senderCategory = category;
    }

    public String getServiceClass() {
        return _serviceClass;
    }

    public void setServiceClass(String class1) {
        _serviceClass = class1;
    }

    public ArrayList getUserrevlist() {
        return _userrevlist;
    }

    public void setUserrevlist(ArrayList _userrevlist) {
        this._userrevlist = _userrevlist;
    }

    public String getDisplayTransferMRP() {
        return _displayTransferMRP;
    }

    public void setDisplayTransferMRP(String transferMRP) {
        _displayTransferMRP = transferMRP;
    }

    public String getToChannelUserStatus() {
        return _toChannelUserStatus;
    }

    public void setToChannelUserStatus(String channelUserStatus) {
        _toChannelUserStatus = channelUserStatus;
    }

    public String getFromChannelUserStatus() {
        return _fromChannelUserStatus;
    }

    public void setFromChannelUserStatus(String channelUserStatus) {
        _fromChannelUserStatus = channelUserStatus;
    }

    public String getChannelUserStatus() {
        return _channelUserStatus;
    }

    public void setChannelUserStatus(String userStatus) {
        _channelUserStatus = userStatus;
    }

    public String getUserWalletCode() {
        return userWalletCode;
    }

    public void setUserWalletCode(String userWalletCode) {
        this.userWalletCode = userWalletCode;
    }
    
    public String getSubSid(){
    	return _subSid;
    }
    public void setSubSid(String subSid){
    	_subSid= subSid;
    }
    
    //added for o2c direct transfer
    private String _stockUpdated= TypesI.YES;
	private String external_code;
	private String grand_msisdn;
	private String productName;
	private String grandName;
	private String grandGeo;
	private String openingBalance;
	private String stockBought;
	private String stockReturn;
	private String channelTransfer;
	private String channelReturn;
	private String c2sTransfers;
	private String closingBalance;
	private String reconStatus;
	private String netBalance;
	private String netLifting;
    
	public String getStockUpdated(){
    	return _stockUpdated;
    }
    public void setStockUpdated(String stockUpdated){
    	_stockUpdated=stockUpdated;
    }
    
    public String getSosStatus(){
    	return sosStatus;
    }
    public void setSosStatus(String SosStatus){
    	sosStatus = SosStatus;
    }
    
    public String getSosTxnId(){
    	return sosTxnId;
    }
    public void setSosTxnId(String SosTxnId){
    	sosTxnId = SosTxnId;
    }
    
    public String getSosProductCode(){
    	return sosProductCode;
    }
    public void setSosProductCode(String sosProduct){
    	sosProductCode = sosProduct;
    }
    
    public boolean getSosFlag(){
    	return sosFlag;
    }
    public void setSosFlag(boolean SosFlagCode){
    	sosFlag = SosFlagCode;
    }
    
    public String getLRStatus(){
    	return lrStatus;
    }
    public void setLRStatus(String status){
    	lrStatus = status;
    }
    
    public boolean getLRFlag(){
    	return lrFlag;
    }
    public void setLRFlag(boolean lrFlagCode){
    	lrFlag = lrFlagCode;
    }

	public void setExternalCode(String externalCode) {
		this.external_code = externalCode;
		
	}
    
	public String getExternalCode() {
		return external_code;
		
	}
	
	public void setGrandMsisdn(String grandMsisdn) {
		this.grand_msisdn = grandMsisdn;
		
	}

	public String getGrandMsisdn() {
		return grand_msisdn;
		
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
		
	}

	
	public String getProductName() {
		return productName;
	}

	public void setGrandName(String grandName) {
		this.grandName = grandName;
		
	}

	public String getGrandName() {
		return grandName;
		
	}
	
	public void setGrandGeo(String grandGeo) {
		this.grandGeo = grandGeo;
		
	}
	
	public String getGrandGeo() {
		return grandGeo;
		
	}

	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
		
	}
    
	public String getOpeningBalance() {
		return openingBalance;
		
	}
	
	
	
	public void setStockBought(String stockBought) {
		this.stockBought = stockBought;
		
	}

	public String getStockBought() {
		return stockBought;
		
	}

	
	public void setStockReturn(String stockReturn) {
		this.stockReturn = stockReturn;
		
	}

	public String getStockReturn() {
		return stockReturn;
		
	}

	
	public void setChannelTransfers(String channelTransfer) {
		this.channelTransfer = channelTransfer;
		
	}

	public String getChannelTransfers() {
		return channelTransfer;
		
	}
	
	
	public void setChannelReturn(String channelReturn) {
		this.channelReturn = channelReturn;
		
	}
    
	public String getChannelReturn() {
		return channelReturn;
		
	}
	
	
	
	public void setClosingBalance(String closingBalance) {
		this.closingBalance = closingBalance;
		
	}

	public String getClosingBalance() {
		return closingBalance;
		
	}
	
	
	public void setReconStatus(String reconStatus) {
		this.reconStatus = reconStatus;
		
	}

	public String getReconStatus() {
		return reconStatus;
		
	}
	public void setNetBalance(String netBalance) {
		this.netBalance = netBalance;
		
	}

	public String getNetBalance() {
		return netBalance;
		
	}
	
	public void setNetLifting(String netLifting) {
		this.netLifting = netLifting;
		
	}
	
	public String getNetLifting() {
		return netLifting;
		
	}
	public String getC2sTransfers() {
		return c2sTransfers;
	}

	public void setC2sTransfers(String c2sTransfers) {
		this.c2sTransfers = c2sTransfers;
	}

	public Long getAmountTransferred() {
		return amountTransferred;
	}

	public void setAmountTransferred(Long amountTransferred) {
		this.amountTransferred = amountTransferred;
	}

	public String getRedemptionType() {
		return redemptionType;
	}

	public void setRedemptionType(String redemptionType) {
		this.redemptionType = redemptionType;
	}

	



    
}
