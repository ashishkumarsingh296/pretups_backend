/**
 * @(#)ChannelTransferVO.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            avinash.kamthan Aug 3, 2005 Initital Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.apache.struts.upload.FormFile;

import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.user.businesslogic.UserLoanVO;
/**
 * @author avinash.kamthan
 * 
 */
public class ChannelTransferVO implements Serializable{
	public static final Log _log = LogFactory.getLog(ChannelTransferVO.class.getName());
    private int _index;
    private String toGrphDomainCodeDesc;
    private String _transferID;
    private String reqQuantity;
    private String _networkCode;
    private String _networkCodeFor;
    private String _graphicalDomainCode;
    private String _domainCode;
    private String _categoryCode;
    private String _senderGradeCode;
    private String _receiverGradeCode;
    private String _fromUserID;
    private String _toUserID;
    private Date _transferDate;
    private String _referenceNum;
    private String _externalTxnNum;
    private Date _externalTxnDate;
    private String _commProfileSetId;
    private String _commProfileVersion;
    private long _requestedQuantity;
    private String _channelRemarks;
    private String _firstApprovalRemark;
    private String _secondApprovalRemark;
    private String _thirdApprovalRemark;
    private String _firstApprovedBy;
    private Date _firstApprovedOn;
    private String _secondApprovedBy;
    private Date _secondApprovedOn;
    private String _thirdApprovedBy;
    private Date _thirdApprovedOn;
    private String _canceledBy;
    private Date _canceledOn;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String status;
    private String _transferType;
    private String _transferInitatedBy;
    private long _transferMRP;
    private long _transferMRPReplica;
    private long _firstApproverLimit;
    private long _secondApprovalLimit;
    private long _payableAmount;
    private long _netPayableAmount;
    private String _batchNum;
    private Date _batchDate;
    private String _payInstrumentType;
    private String _payInstrumentName;
    private String _payInstrumentStatus;
    private String _payInstrumentNum;
    private Date _payInstrumentDate;
    private long _payInstrumentAmt;
    private String _senderTxnProfile;
    private String _receiverTxnProfile;
    private long _totalTax1;
    private long _totalTax2;
    private long _totalTax3;
    private String _source;
    private String _receiverCategoryCode;
    private String _requestGatewayCode;
    private String _requestGatewayType;
    private String _paymentInstSource;
    private String _productType;
    private String _transferCategory;
    private long _lastModifiedTime;
    private String _type;
    private String _transferSubType;
    private String activeUsersUserType;
    private String transferInitatorLoginID;
    private ArrayList _voucherDetails;
    private String _optBatchNum;
    public ArrayList getVoucherDetails() {
		return _voucherDetails;
	}

	public void setVoucherDetails(ArrayList _voucherDetails) {
		this._voucherDetails = _voucherDetails;
	}




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
    private String _referenceID = null;
    private String _transferSubTypeValue = null;

    private String _controlTransfer = null; // field to store that transfer is
    private String controlTransferDesc = null;
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
	private boolean fileUploaded;
	private long lrWithdrawAmt;
	private boolean isWeb;
	private boolean otfCountsUpdated = false;
	private boolean targetAchieved = false;
	 private ArrayList channelTransferitemsVOListforOTF;
	 
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
	 private String userName;
	 private String msisdn;
	 private String categoryName;
	 private String gegoraphyDomainName;
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
	 private String userID;
	 private String loginID;
	 private String serviceTypeName;
	 private String selectorName;
	 private String differentialAmount;
	 private String transactionCount;
	 private String grandCategory;
	 private String o2cTransferInCount;
	 private String o2cTransferInAmount;
	 private String c2cTransferInCount;
	 private String c2cTransferInAmount;
	 private String c2cReturnPlusWithCount;
	 private String c2cReturnPlusWithINAmount;
	 private String o2cReturnPlusWithoutCount;
	 private String o2cReturnPlusWithoutAmount;
	 private String c2cTransferOutCount;
	 private String c2cTransferOutAmount;
	 private String c2cReturnWithOutCount;
	 private String c2cReturnWithOutAmount;
	 private String c2sTransferOutCount;
	 private String c2sTransferAmount;
	 private long unitValue;
	 private long requiredQuantity;
	 private String dualCommissionType;
	 private String profileNames;
	 private String curStatus;
	 private String apprRejStatus;
	 // added For Voucher Details
	 private String batch_no;
	 private String product_name;
	 private String batch_type;
	 private String from_serial_no;
	 private String to_serial_no;
	 private long total_no_of_vouchers;
	 private UserOTFCountsVO userOTFCountsVO=null;
	 private String transferSubTypeAsString;
	 
	 private String fromMsisdn = null;
	 private String toMsisdn = null;
	 private String toPrimaryMSISDN = null;
	 private String fromCategoryDesc = null;
	 private String toCategoryDesc = null;
	 private String fromGradeCodeDesc = null;
	 private String toGradeCodeDesc = null;
	 private String fromCommissionProfileIDDesc = null;
	 private String toCommissionProfileIDDesc = null;
	 private String toTxnProfileDesc = null;
	 private String fromTxnProfileDesc = null;
	 private String  voucherType = null;
	 private String segment =  null;
	 private String firstApprovedOnAsString =  null;
	 private String secondApprovedOnAsString =  null;
	 private String isFileC2C =  "N";
	 private String sosSettlementDateAsString=null;
	 
	 private String networkName = null;
	 private String networkNameFor = null;
	 private String receiverDomainCodeDesc = null;
	 private String senderGradeCodeDesc = null;
	 public String getReceiverGgraphicalDomainCodeDesc() {
		return receiverGgraphicalDomainCodeDesc;
	}

	public void setReceiverGgraphicalDomainCodeDesc(String receiverGgraphicalDomainCodeDesc) {
		this.receiverGgraphicalDomainCodeDesc = receiverGgraphicalDomainCodeDesc;
	}




	private String receiverGgraphicalDomainCodeDesc = null;

	 public String getSenderGradeCodeDesc() {
		return senderGradeCodeDesc;
	}

	public void setSenderGradeCodeDesc(String senderGradeCodeDesc) {
		this.senderGradeCodeDesc = senderGradeCodeDesc;
	}

	public String getReceiverDomainCodeDesc() {
		return receiverDomainCodeDesc;
	}

	public void setReceiverDomainCodeDesc(String receiverDomainCodeDesc) {
		this.receiverDomainCodeDesc = receiverDomainCodeDesc;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getNetworkNameFor() {
		return networkNameFor;
	}

	public void setNetworkNameFor(String networkNameFor) {
		this.networkNameFor = networkNameFor;
	}




	private String createdOnAsString;

	 public String getIsFileC2C() {
		return isFileC2C;
	}

	public void setIsFileC2C(String isFileC2C) {
		this.isFileC2C = isFileC2C;
	}




	public String getSosSettlementDateAsString() {
		return sosSettlementDateAsString;
	}

	public void setSosSettlementDateAsString(String sosSettlementDateAsString) {
		this.sosSettlementDateAsString = sosSettlementDateAsString;
	}




	//private FormFile uploadedFile = null;
	 private String uploadedFilePath;
	 private String uploadedFileName;
	 private ArrayList<ChannelTransferItemsVO> channelTransferList; 
	 private ArrayList voucherTypeList;
	 private ArrayList slabslist;

	
	public ArrayList getVoucherTypeList() {
		return voucherTypeList;
	}

	public void setVoucherTypeList(ArrayList voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	public ArrayList getSlabslist() {
		return slabslist;
	}

	public void setSlabslist(ArrayList slabslist) {
		this.slabslist = slabslist;
	}

	


	private File approvalFile = null;
	 private boolean bundleType = false;
	 
	 
	 
	 public File getApprovalFile() {
		return approvalFile;
	}

	public void setApprovalFile(File approvalFile) {
		this.approvalFile = approvalFile;
	}
	
	public String getUploadedFilePath() {
		return uploadedFilePath;
	}

	public void setUploadedFilePath(String uploadedFilePath) {
		this.uploadedFilePath = uploadedFilePath;
	}

	public String getUploadedFileName() {
		return uploadedFileName;
	}

	public void setUploadedFileName(String uploadedFileName) {
		this.uploadedFileName = uploadedFileName;
	}


	public String getTransferSubTypeAsString() {
		return transferSubTypeAsString;
	}

	public void setTransferSubTypeAsString(String transferSubTypeAsString) {
		this.transferSubTypeAsString = transferSubTypeAsString;
	}

	public boolean isReconciliationFlag() {
		return reconciliationFlag;
	}

	public void setReconciliationFlag(boolean reconciliationFlag) {
		this.reconciliationFlag = reconciliationFlag;
	}


	public boolean isFileUploaded() {
		return fileUploaded;
	}

	public void setIsFileUploaded(boolean fileUploaded) {
		this.fileUploaded = fileUploaded;
	}

	

/*
	public FormFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(FormFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

*/


	private boolean reconciliationFlag;
	 private String previousStatus;
	 
	 public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	public UserOTFCountsVO getUserOTFCountsVO() {
		 return userOTFCountsVO;
	 }

	 public void setUserOTFCountsVO(UserOTFCountsVO _userOTFCountsVO) {
		 this.userOTFCountsVO = _userOTFCountsVO;
	 }
		
	 public String getBatch_no() {
		return batch_no;
	}

	public void setBatch_no(String batch_no) {
		this.batch_no = batch_no;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}	
	 public String getBatch_type() {
		return batch_type;
	}

	public void setBatch_type(String batch_type) {
		this.batch_type = batch_type;
	}

	public String getFrom_serial_no() {
		return from_serial_no;
	}

	public void setFrom_serial_no(String from_serial_no) {
		this.from_serial_no = from_serial_no;
	}

	public String getTo_serial_no() {
		return to_serial_no;
	}

	public void setTo_serial_no(String to_serial_no) {
		this.to_serial_no = to_serial_no;
	}

	public long getTotal_no_of_vouchers() {
		return total_no_of_vouchers;
	}

	public void setTotal_no_of_vouchers(long total_no_of_vouchers) {
		this.total_no_of_vouchers = total_no_of_vouchers;
	}
	 
	 public String getProfileNames() {
		return profileNames;
	}

	public void setProfileNames(String profileNames) {
		this.profileNames = profileNames;
	}

	public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}

	public long getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(long unitValue) {
		this.unitValue = unitValue;
	}

	public long getRequiredQuantity() {
		return requiredQuantity;
	}

	public void setRequiredQuantity(long requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
	}

	public String getC2cTransferInCount() {
		return c2cTransferInCount;
	}

	public void setC2cTransferInCount(String c2cTransferInCount) {
		this.c2cTransferInCount = c2cTransferInCount;
	}

	public String getC2cTransferInAmount() {
		return c2cTransferInAmount;
	}

	public void setC2cTransferInAmount(String c2cTransferInAmount) {
		this.c2cTransferInAmount = c2cTransferInAmount;
	}



	
	 
	 
	 public String getO2cTransferInCount() {
		return o2cTransferInCount;
	}

	public void setO2cTransferInCount(String o2cTransferInCount) {
		this.o2cTransferInCount = o2cTransferInCount;
	}

	public String getO2cTransferInAmount() {
		return o2cTransferInAmount;
	}

	public void setO2cTransferInAmount(String o2cTransferInAmount) {
		this.o2cTransferInAmount = o2cTransferInAmount;
	}



	
	 
   
	 
	 public String getGrandcategory(){
		 return grandCategory;
	 }
	 
	 public void setGrandCategory(String grandCategory){
		 this.grandCategory = grandCategory;
	 }
	 
	 public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

	public String getSelectorName() {
		return selectorName;
	}

	public void setSelectorName(String selectorName) {
		this.selectorName = selectorName;
	}

	public String getDifferentialAmount() {
		return differentialAmount;
	}

	public void setDifferentialAmount(String differentialAmount) {
		this.differentialAmount = differentialAmount;
	}

	public String getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(String transactionCount) {
		this.transactionCount = transactionCount;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public ChannelTransferVO() {}

     public ChannelTransferVO(ChannelTransferVO c) {
		_index =  c._index;
		_transferID =  c._transferID;
		reqQuantity =  c.reqQuantity;
		_networkCode =  c._networkCode;
		_networkCodeFor =  c._networkCodeFor;
		_graphicalDomainCode =  c._graphicalDomainCode;
		_domainCode =  c._domainCode;
		_categoryCode =  c._categoryCode;
		_senderGradeCode =  c._senderGradeCode;
		_receiverGradeCode =  c._receiverGradeCode;
		_fromUserID =  c._fromUserID;
		_toUserID =  c._toUserID;
		_transferDate =  c._transferDate;
		_referenceNum =  c._referenceNum;
		_externalTxnNum =  c._externalTxnNum;
		_externalTxnDate =  c._externalTxnDate;
		_commProfileSetId =  c._commProfileSetId;
		_commProfileVersion =  c._commProfileVersion;
		_requestedQuantity =  c._requestedQuantity;
		_channelRemarks =  c._channelRemarks;
		_firstApprovalRemark =  c._firstApprovalRemark;
		_secondApprovalRemark =  c._secondApprovalRemark;
		_thirdApprovalRemark =  c._thirdApprovalRemark;
		_firstApprovedBy =  c._firstApprovedBy;
		_firstApprovedOn =  c._firstApprovedOn;
		_secondApprovedBy =  c._secondApprovedBy;
		_secondApprovedOn =  c._secondApprovedOn;
		_thirdApprovedBy =  c._thirdApprovedBy;
		_thirdApprovedOn =  c._thirdApprovedOn;
		_canceledBy =  c._canceledBy;
		_canceledOn =  c._canceledOn;
		_createdOn =  c._createdOn;
		_createdBy =  c._createdBy;
		_modifiedOn =  c._modifiedOn;
		_modifiedBy =  c._modifiedBy;
		 status =  c.status;
		_transferType =  c._transferType;
		_transferInitatedBy =  c._transferInitatedBy;
		_transferMRP =  c._transferMRP;
		_firstApproverLimit =  c._firstApproverLimit;
		_secondApprovalLimit =  c._secondApprovalLimit;
		_payableAmount =  c._payableAmount;
		_netPayableAmount =  c._netPayableAmount;
		_batchNum =  c._batchNum;
		_batchDate =  c._batchDate;
		_payInstrumentType =  c._payInstrumentType;
		_payInstrumentStatus =  c._payInstrumentStatus;
		_payInstrumentNum =  c._payInstrumentNum;
		_payInstrumentDate =  c._payInstrumentDate;
		_payInstrumentAmt =  c._payInstrumentAmt;
		_senderTxnProfile =  c._senderTxnProfile;
		_receiverTxnProfile =  c._receiverTxnProfile;
		_totalTax1 =  c._totalTax1;
		_totalTax2 =  c._totalTax2;
		_totalTax3 =  c._totalTax3;
		_source =  c._source;
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
		controlTransferDesc = c.controlTransferDesc;
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
		channelTransferitemsVOListforOTF =  c.channelTransferitemsVOListforOTF;
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
		o2cTransferInCount = c.o2cTransferInCount;
		o2cTransferInAmount = c.o2cTransferInAmount;
		c2cTransferInCount = c.c2cTransferInCount;
		c2cTransferInAmount = c.c2cTransferInAmount;
		c2cReturnPlusWithCount = c.c2cReturnPlusWithCount;
		c2cReturnPlusWithINAmount = c.c2cReturnPlusWithINAmount;
		o2cReturnPlusWithoutCount = c.o2cReturnPlusWithoutCount;
		o2cReturnPlusWithoutAmount = c.o2cReturnPlusWithoutAmount;
		c2cTransferOutCount = c.c2cTransferOutCount;
		c2cTransferOutAmount = c.c2cTransferOutAmount;
		c2cReturnWithOutCount = c.c2cReturnWithOutCount;
		c2cReturnWithOutAmount = c.c2cReturnWithOutAmount;
		c2sTransferOutCount = c.c2sTransferOutCount;
		c2sTransferAmount = c.c2sTransferAmount;
		dualCommissionType = c.dualCommissionType;
		createdOnAsString = c.createdOnAsString;
		activeUsersUserType = c.activeUsersUserType;
		
	}
	 
     
	
     
     
	
	
	public String getC2cReturnPlusWithCount() {
		return c2cReturnPlusWithCount;
	}

	public void setC2cReturnPlusWithCount(String c2cReturnPlusWithCount) {
		this.c2cReturnPlusWithCount = c2cReturnPlusWithCount;
	}

	public String getC2cReturnPlusWithINAmount() {
		return c2cReturnPlusWithINAmount;
	}

	public void setC2cReturnPlusWithINAmount(String c2cReturnPlusWithINAmount) {
		this.c2cReturnPlusWithINAmount = c2cReturnPlusWithINAmount;
	}

	public String getO2cReturnPlusWithoutCount() {
		return o2cReturnPlusWithoutCount;
	}

	public void setO2cReturnPlusWithoutCount(String o2cReturnPlusWithoutCount) {
		this.o2cReturnPlusWithoutCount = o2cReturnPlusWithoutCount;
	}

	public String getO2cReturnPlusWithoutAmount() {
		return o2cReturnPlusWithoutAmount;
	}

	public void setO2cReturnPlusWithoutAmount(String o2cReturnPlusWithoutAmount) {
		this.o2cReturnPlusWithoutAmount = o2cReturnPlusWithoutAmount;
	}

	public String getC2cTransferOutCount() {
		return c2cTransferOutCount;
	}

	public void setC2cTransferOutCount(String c2cTransferOutCount) {
		this.c2cTransferOutCount = c2cTransferOutCount;
	}

	public String getC2cTransferOutAmount() {
		return c2cTransferOutAmount;
	}

	public void setC2cTransferOutAmount(String c2cTransferOutAmount) {
		this.c2cTransferOutAmount = c2cTransferOutAmount;
	}

	public String getC2cReturnWithOutCount() {
		return c2cReturnWithOutCount;
	}

	public void setC2cReturnWithOutCount(String c2cReturnWithOutCount) {
		this.c2cReturnWithOutCount = c2cReturnWithOutCount;
	}

	public String getC2cReturnWithOutAmount() {
		return c2cReturnWithOutAmount;
	}

	public void setC2cReturnWithOutAmount(String c2cReturnWithOutAmount) {
		this.c2cReturnWithOutAmount = c2cReturnWithOutAmount;
	}

	public String getC2sTransferOutCount() {
		return c2sTransferOutCount;
	}

	public void setC2sTransferOutCount(String c2sTransferOutCount) {
		this.c2sTransferOutCount = c2sTransferOutCount;
	}

	public String getC2sTransferAmount() {
		return c2sTransferAmount;
	}

	public void setC2sTransferAmount(String c2sTransferAmount) {
		this.c2sTransferAmount = c2sTransferAmount;
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
	 public String getReqQuantity() {
			return reqQuantity;
		}
		public void setReqQuantity(String reqQuantity) {
			this.reqQuantity = reqQuantity;
		}
	 
	public String getCommProfileDetailID() {
		return commProfileDetailID;
	}
	public void setCommProfileDetailID(String commProfileDetailID) {
		this.commProfileDetailID = commProfileDetailID;
	}
	public ArrayList getChannelTransferitemsVOListforOTF() {
		return channelTransferitemsVOListforOTF;
	}
	public void setChannelTransferitemsVOListforOTF(
			ArrayList channelTransferitemsVOListforOTF) {
		this.channelTransferitemsVOListforOTF = channelTransferitemsVOListforOTF;
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
    private String info3=null;
    private String info4=null;
    private String info5=null;
    
    private String info6=null;
    private String info7=null;
    private String info8=null;
    private String info9=null;
    private String info10=null;
    
    public String getInfo6() {
		return info6;
	}

	public void setInfo6(String info6) {
		this.info6 = info6;
	}

	public String getInfo7() {
		return info7;
	}

	public void setInfo7(String info7) {
		this.info7 = info7;
	}

	public String getInfo8() {
		return info8;
	}

	public void setInfo8(String info8) {
		this.info8 = info8;
	}

	public String getInfo9() {
		return info9;
	}

	public void setInfo9(String info9) {
		this.info9 = info9;
	}

	public String getInfo10() {
		return info10;
	}

	public void setInfo10(String info10) {
		this.info10 = info10;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}



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
    
	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
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
    public boolean isReversalFlag() {
		return _reversalFlag;
	}
	public void setReversalFlag(boolean reversalFlag) {
		this._reversalFlag = reversalFlag;
	}
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

    public String getCanceledBy() {
        return _canceledBy;
    }

    public void setCanceledBy(String cancledBy) {
        _canceledBy = cancledBy;
    }

    public Date getCanceledOn() {
        return _canceledOn;
    }

    public void setCanceledOn(Date cancledOn) {
        _canceledOn = cancledOn;
    }

    public String getChannelRemarks() {
        return _channelRemarks;
    }

    public void setChannelRemarks(String channelRemarks) {
        _channelRemarks = channelRemarks;
    }

    public String getCommProfileSetId() {
        return _commProfileSetId;
    }

    public void setCommProfileSetId(String commProfileSetId) {
        _commProfileSetId = commProfileSetId;
    }

    public String getCommProfileVersion() {
        return _commProfileVersion;
    }

    public void setCommProfileVersion(String commProfileVersion) {
        _commProfileVersion = commProfileVersion;
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

    public Date getExternalTxnDate() {
        return _externalTxnDate;
    }

    public void setExternalTxnDate(Date externalTxnDate) {
        _externalTxnDate = externalTxnDate;
    }

    public String getExternalTxnNum() {
        return _externalTxnNum;
    }

    public void setExternalTxnDateAsString(String externalTxnDate) {
        if (!BTSLUtil.isNullString(externalTxnDate)) {
            try {
                _externalTxnDate = BTSLUtil.getDateFromDateString(externalTxnDate);
            } catch (ParseException e) {
            	 _log.errorTrace("setExternalTxnDateAsString", e);
            }
        }
    }

    public String getExternalTxnDateAsString() {
        if (_externalTxnDate == null) {
            return null;
        }
        try {
            return BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(_externalTxnDate));
        } catch (ParseException e) {
            return null;
        }
    }

    public void setExternalTxnNum(String externalTxnNum) {
        _externalTxnNum = externalTxnNum;
    }

    public String getFirstApprovalRemark() {
        return _firstApprovalRemark;
    }

    public void setFirstApprovalRemark(String firstApprovalRemark) {
        _firstApprovalRemark = firstApprovalRemark;
    }

    public String getFirstApprovedBy() {
        return _firstApprovedBy;
    }

    public void setFirstApprovedBy(String firstApprovedBy) {
        _firstApprovedBy = firstApprovedBy;
    }

    public Date getFirstApprovedOn() {
        return _firstApprovedOn;
    }

    public void setFirstApprovedOn(Date firstApprovedOn) {
        _firstApprovedOn = firstApprovedOn;
    }

    public long getFirstApproverLimit() {
        return _firstApproverLimit;
    }

    public void setFirstApproverLimit(long firstApproverLimit) {
        _firstApproverLimit = firstApproverLimit;
    }

    /**
     * @return Returns the batchDate.
     */
    public Date getBatchDate() {
        return _batchDate;
    }

    /**
     * @param batchDate
     *            The batchDate to set.
     */
    public void setBatchDate(Date batchDate) {
        _batchDate = batchDate;
    }

    /**
     * @return Returns the batchNum.
     */
    public String getBatchNum() {
        return _batchNum;
    }

    /**
     * @param batchNum
     *            The batchNum to set.
     */
    public void setBatchNum(String batchNum) {
        _batchNum = batchNum;
    }

    public String getFromUserID() {
        return _fromUserID;
    }

    public void setFromUserID(String fromUserId) {
        _fromUserID = fromUserId;
    }

    public String getGraphicalDomainCode() {
        return _graphicalDomainCode;
    }

    public void setGraphicalDomainCode(String graphicalDomainCode) {
        _graphicalDomainCode = graphicalDomainCode;
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

    public long getPayInstrumentAmt() {
        return _payInstrumentAmt;
    }

    public void setPayInstrumentAmt(long payInstrumentAmt) {
        _payInstrumentAmt = payInstrumentAmt;
    }

    public Date getPayInstrumentDate() {
        return _payInstrumentDate;
    }

    public void setPayInstrumentDate(Date payInstrumentDate) {
        _payInstrumentDate = payInstrumentDate;
    }

    public String getPayInstrumentNum() {
        return _payInstrumentNum;
    }

    public void setPayInstrumentNum(String payInstrumentNum) {
        _payInstrumentNum = payInstrumentNum;
    }

    public String getPayInstrumentType() {
        return _payInstrumentType;
    }

    public void setPayInstrumentType(String payInstrumentType) {
        _payInstrumentType = payInstrumentType;
    }

    public String getReceiverGradeCode() {
        return _receiverGradeCode;
    }

    public void setReceiverGradeCode(String receiverGradeCode) {
        _receiverGradeCode = receiverGradeCode;
    }

    public String getReceiverTxnProfile() {
        return _receiverTxnProfile;
    }

    public void setReceiverTxnProfile(String receiverTxnProfile) {
        _receiverTxnProfile = receiverTxnProfile;
    }

    public String getReferenceNum() {
        return _referenceNum;
    }

    public void setReferenceNum(String refrenceNum) {
        _referenceNum = refrenceNum;
    }

    public long getRequestedQuantity() {
        return _requestedQuantity;
    }

    public String getRequestedQuantityAsString() {
        return PretupsBL.getDisplayAmount(_requestedQuantity);
    }

    public void setRequestedQuantity(long requstedQuantity) {
        _requestedQuantity = requstedQuantity;
    }

    public String getNetworkCodeFor() {
        return _networkCodeFor;
    }

    public void setNetworkCodeFor(String roamNetworkCode) {
        _networkCodeFor = roamNetworkCode;
    }

    public long getSecondApprovalLimit() {
        return _secondApprovalLimit;
    }

    public void setSecondApprovalLimit(long secondApprovalLimit) {
        _secondApprovalLimit = secondApprovalLimit;
    }

    public String getSecondApprovalRemark() {
        return _secondApprovalRemark;
    }

    public void setSecondApprovalRemark(String secondApprovalRemark) {
        _secondApprovalRemark = secondApprovalRemark;
    }

    public String getSecondApprovedBy() {
        return _secondApprovedBy;
    }

    public void setSecondApprovedBy(String secondApprovedBy) {
        _secondApprovedBy = secondApprovedBy;
    }

    public Date getSecondApprovedOn() {
        return _secondApprovedOn;
    }

    public void setSecondApprovedOn(Date secondApprovedOn) {
        _secondApprovedOn = secondApprovedOn;
    }

    public String getSenderGradeCode() {
        return _senderGradeCode;
    }

    public void setSenderGradeCode(String senderGradeCode) {
        _senderGradeCode = senderGradeCode;
    }

    public String getSenderTxnProfile() {
        return _senderTxnProfile;
    }

    public void setSenderTxnProfile(String senderTxnProfile) {
        _senderTxnProfile = senderTxnProfile;
    }

    public String getSource() {
        return _source;
    }

    public void setSource(String source) {
        _source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThirdApprovalRemark() {
        return _thirdApprovalRemark;
    }

    public void setThirdApprovalRemark(String thirdApprovalRemark) {
        _thirdApprovalRemark = thirdApprovalRemark;
    }

    public String getThirdApprovedBy() {
        return _thirdApprovedBy;
    }

    public void setThirdApprovedBy(String thirdApprovedBy) {
        _thirdApprovedBy = thirdApprovedBy;
    }

    public Date getThirdApprovedOn() {
        return _thirdApprovedOn;
    }

    public void setThirdApprovedOn(Date thirdApprovedOn) {
        _thirdApprovedOn = thirdApprovedOn;
    }

    public long getTotalTax1() {
        return _totalTax1;
    }

    public void setTotalTax1(long totalTax1) {
        _totalTax1 = totalTax1;
    }

    public long getTotalTax2() {
        return _totalTax2;
    }

    public void setTotalTax2(long totalTax2) {
        _totalTax2 = totalTax2;
    }

    public long getTotalTax3() {
        return _totalTax3;
    }

    public void setTotalTax3(long totalTax3) {
        _totalTax3 = totalTax3;
    }

    public String getToUserID() {
        return _toUserID;
    }

    public void setToUserID(String toUserID) {
        _toUserID = toUserID;
    }

    public Date getTransferDate() {
        return _transferDate;
    }

    public void setTransferDate(Date transferDate) {
        _transferDate = transferDate;
    }

    public String getTransferID() {
        return _transferID;
    }

    public void setTransferID(String transferID) {
        _transferID = transferID;
    }

    public String getTransferType() {
        return _transferType;
    }

    public void setTransferType(String transferType) {
        _transferType = transferType;
    }

    public String getTransferInitatedBy() {
        return _transferInitatedBy;
    }

    public void setTransferInitatedBy(String trnsfrInitatedBy) {
        _transferInitatedBy = trnsfrInitatedBy;
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

    public long getTransferMRP() {
        return _transferMRP;
    }

    public void setTransferMRP(long transferMRP) {
        _transferMRP = transferMRP;
    }

    @Override
	public String toString() {
        final StringBuffer sbf = new StringBuffer(" transferID  " + _transferID + ",");
        sbf.append(" networkCode  " + _networkCode + ", ");
        sbf.append(" roamNetworkCode  " + _networkCodeFor + ", ");
        sbf.append(" graphicalDomainCode  " + _graphicalDomainCode + ", ");
        sbf.append(" domainCode  " + _domainCode + ", ");
        sbf.append(" categoryCode  " + _categoryCode + ", ");
        sbf.append(" senderGradeCode  " + _senderGradeCode + ", ");
        sbf.append(" receiverGradeCode  " + _receiverGradeCode + ", ");
        sbf.append(" fromUserId  " + _fromUserID + ", ");
        sbf.append(" toUserID  " + _toUserID + ", ");
        sbf.append(" transferDate  " + _transferDate + ", ");
        sbf.append(" refrenceNum  " + _referenceNum + ", ");
        sbf.append(" externalTxnNum  " + _externalTxnNum + ", ");
        sbf.append(" externalTxnDate  " + _externalTxnDate + ", ");
        sbf.append(" commProfileSetId  " + _commProfileSetId + ", ");
        sbf.append(" commProfileVersion  " + _commProfileVersion + ", ");
        sbf.append(" requstedQuantity  " + _requestedQuantity + ", ");
        sbf.append(" channelRemarks  " + _channelRemarks + ", ");
        sbf.append(" firstApprovalRemark  " + _firstApprovalRemark + ", ");
        sbf.append(" secondApprovalRemark  " + _secondApprovalRemark + ", ");
        sbf.append(" thirdApprovalRemark  " + _thirdApprovalRemark + ", ");
        sbf.append(" firstApprovedBy  " + _firstApprovedBy + ", ");
        sbf.append(" firstApprovedOn  " + _firstApprovedOn + ", ");
        sbf.append(" secondApprovedBy  " + _secondApprovedBy + ", ");
        sbf.append(" secondApprovedOn  " + _secondApprovedOn + ", ");
        sbf.append(" thirdApprovedBy  " + _thirdApprovedBy + ", ");
        sbf.append(" thirdApprovedOn  " + _thirdApprovedOn + ", ");
        sbf.append(" cancledBy  " + _canceledBy + ", ");
        sbf.append(" cancledOn  " + _canceledOn + ", ");
        sbf.append(" createdOn  " + _createdOn + ", ");
        sbf.append(" createdBy  " + _createdBy + ", ");
        sbf.append(" modifiedOn  " + _modifiedOn + ", ");
        sbf.append(" modifiedBy  " + _modifiedBy + ", ");
        sbf.append(" status  " + status + ", ");
        sbf.append(" curStatus" + curStatus + ", ");
        sbf.append(" apprRejStatus" + apprRejStatus + ", ");
        sbf.append(" transferType  " + _transferType + ", ");
        sbf.append(" trnsfrInitatedBy  " + _transferInitatedBy + ", ");
        sbf.append(" firstApproverLimit  " + _firstApproverLimit + ", ");
        sbf.append(" secondApprovalLimit  " + _secondApprovalLimit + ", ");
        sbf.append(" payableAmount  " + _payableAmount + ", ");
        sbf.append(" netPayableAmount  " + _netPayableAmount + ", ");
        sbf.append(" BatchNum  " + _batchNum + ", ");
        sbf.append(" batchDate  " + _batchDate + ", ");
        sbf.append(" payInstrumentType  " + _payInstrumentType + ", ");
        sbf.append(" payInstrumentStatus  " + _payInstrumentStatus + ", ");
        sbf.append(" payInstrumentNum  " + _payInstrumentNum + ", ");
        sbf.append(" payInstrumentDate  " + _payInstrumentDate + ", ");
        sbf.append(" payInstrumentAmt  " + _payInstrumentAmt + ", ");
        sbf.append(" senderTxnProfile  " + _senderTxnProfile + ", ");
        sbf.append(" receiverTxnProfile  " + _receiverTxnProfile + ", ");
        sbf.append(" totalTax1  " + _totalTax1 + ", ");
        sbf.append(" totalTax2  " + _totalTax2 + ", ");
        sbf.append(" totalTax3  " + _totalTax3 + ", ");
        sbf.append(" source  " + _source + ", ");
        sbf.append(" receiverCategoryCode  " + _receiverCategoryCode + ", ");
        sbf.append(" requestGatewayCode  " + _requestGatewayCode + ", ");
        sbf.append(" requestGateWayType  " + _requestGatewayType + ", ");
        sbf.append(" _controlTransfer= " + _controlTransfer);
        sbf.append(" controlTransferDesc= " + controlTransferDesc);
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
        sbf.append(", _transferSubType" +_transferSubType);
        sbf.append(", userOTFCountsVO" + userOTFCountsVO);
        sbf.append(", dualCommissionType" + dualCommissionType);
        sbf.append(", voucherType" + voucherType);
        sbf.append(",segment " + segment);
        sbf.append(",firstApprovedOnAsString " + firstApprovedOnAsString);
        sbf.append(",secondApprovedOnAsString " + firstApprovedOnAsString);
        sbf.append(" channelTransferitemsVOList  " + _channelTransferitemsVOList + ", ");
        sbf.append(" channelTransferList  " + channelTransferList + ", ");
        sbf.append(" voucherTypeList  " + voucherTypeList + ", ");
        sbf.append(" slabslist  " + slabslist + ", ");
        sbf.append(" activeUsersUserType  " + activeUsersUserType );
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

    public int getIndex() {
        return _index;
    }

    public void setIndex(int index) {
        _index = index;
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

    public String getTransferMRPAsString() {
        return PretupsBL.getDisplayAmount(_transferMRP);
    }

    public String getTransferDateAsString() {
        return _transferDateAsString;
    }

    public void setTransferDateAsString(String transferDateAsString) {
        this._transferDateAsString = transferDateAsString;
    }

    public String getCreatedOnAsString() {
		return createdOnAsString;
	}

	public void setCreatedOnAsString(String createdOnAsString) {
		this.createdOnAsString = createdOnAsString;
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
    

    public String getControlTransferDesc() {
		return controlTransferDesc;
	}

	public void setControlTransferDesc(String controlTransferDesc) {
		this.controlTransferDesc = controlTransferDesc;
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


	private String[] messageArgumentList;
	public String[] getMessageArgumentList() {
		return messageArgumentList;
	}
	public void setMessageArgumentList(String[] messageArgumentList) {
		this.messageArgumentList = messageArgumentList;
	}

	private ArrayList<ChannelVoucherItemsVO> _channelVoucherItemsVoList;
	
	public ArrayList<ChannelVoucherItemsVO> getChannelVoucherItemsVoList() {
		return _channelVoucherItemsVoList;
	}

	public void setChannelVoucherItemsVoList(ArrayList<ChannelVoucherItemsVO> _channelVoucherItemsVoList) {
		this._channelVoucherItemsVoList = _channelVoucherItemsVoList;
	}
	
	public String getPayInstrumentStatus() {
        return _payInstrumentStatus;
    }
    public void setPayInstrumentStatus(String payInstrumentStatus) {
        _payInstrumentStatus = payInstrumentStatus;
    }

	public String getPayInstrumentName() {
		return _payInstrumentName;
	}

	public void setPayInstrumentName(String payInstrumentName) {
		this._payInstrumentName = payInstrumentName;
	}

	public long get_transferMRPReplica() {
		return _transferMRPReplica;
	}

	public void set_transferMRPReplica(long _transferMRPReplica) {
		this._transferMRPReplica = _transferMRPReplica;
	}

	public String getCurStatus() {
		return curStatus;
	}

	public void setCurStatus(String curStatus) {
		this.curStatus = curStatus;
	}

	public String getApprRejStatus() {
		return apprRejStatus;
	}

	public void setApprRejStatus(String apprRejStatus) {
		this.apprRejStatus = apprRejStatus;
	}

	public String getFromMsisdn() {
		return fromMsisdn;
	}

	public void setFromMsisdn(String fromMsisdn) {
		this.fromMsisdn = fromMsisdn;
	}

	public String getToMsisdn() {
		return toMsisdn;
	}

	public void setToMsisdn(String toMsisdn) {
		this.toMsisdn = toMsisdn;
	}

	public String getToPrimaryMSISDN() {
		return toPrimaryMSISDN;
	}

	public void setToPrimaryMSISDN(String toPrimaryMSISDN) {
		this.toPrimaryMSISDN = toPrimaryMSISDN;
	}

	public String getFromCategoryDesc() {
		return fromCategoryDesc;
	}

	public void setFromCategoryDesc(String fromCategoryDesc) {
		this.fromCategoryDesc = fromCategoryDesc;
	}

	public String getToCategoryDesc() {
		return toCategoryDesc;
	}

	public void setToCategoryDesc(String toCategoryDesc) {
		this.toCategoryDesc = toCategoryDesc;
	}

	public String getFromGradeCodeDesc() {
		return fromGradeCodeDesc;
	}

	public void setFromGradeCodeDesc(String fromGradeCodeDesc) {
		this.fromGradeCodeDesc = fromGradeCodeDesc;
	}

	public String getToGradeCodeDesc() {
		return toGradeCodeDesc;
	}

	public void setToGradeCodeDesc(String toGradeCodeDesc) {
		this.toGradeCodeDesc = toGradeCodeDesc;
	}

	public String getFromCommissionProfileIDDesc() {
		return fromCommissionProfileIDDesc;
	}

	public void setFromCommissionProfileIDDesc(String fromCommissionProfileIDDesc) {
		this.fromCommissionProfileIDDesc = fromCommissionProfileIDDesc;
	}

	public String getToCommissionProfileIDDesc() {
		return toCommissionProfileIDDesc;
	}

	public void setToCommissionProfileIDDesc(String toCommissionProfileIDDesc) {
		this.toCommissionProfileIDDesc = toCommissionProfileIDDesc;
	}

	public String getToTxnProfileDesc() {
		return toTxnProfileDesc;
	}

	public void setToTxnProfileDesc(String toTxnProfileDesc) {
		this.toTxnProfileDesc = toTxnProfileDesc;
	}

	public String getFromTxnProfileDesc() {
		return fromTxnProfileDesc;
	}

	public void setFromTxnProfileDesc(String fromTxnProfileDesc) {
		this.fromTxnProfileDesc = fromTxnProfileDesc;
	}

	public String getVoucher_type() {
		return voucherType;
	}

	public void setVoucher_type(String voucher_type) {
		this.voucherType = voucher_type;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getFirstApprovedOnAsString() {
		
		if (_firstApprovedOn == null) {
            return null;
        }
        try {
            return BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(_firstApprovedOn));
        } catch (ParseException e) {
            return null;
        }
	}

	public void setFirstApprovedOnAsString(String firstApprovedOnAsString) {
		this.firstApprovedOnAsString = firstApprovedOnAsString;
	}

	public String getSecondApprovedOnAsString() {
		if (_secondApprovedOn == null) {
            return null;
        }
        try {
            return BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(_secondApprovedOn));
        } catch (ParseException e) {
            return null;
        }
	
	}

	public void setSecondApprovedOnAsString(String secondApprovedOnAsString) {
		this.secondApprovedOnAsString = secondApprovedOnAsString;
	}
	
	public void setBundleType(boolean bundleType) {
		this.bundleType = bundleType;
	}
	 public boolean isBundleType() {
		return bundleType;
	}

	public ArrayList<ChannelTransferItemsVO> getChannelTransferList() {
		return channelTransferList;
	}

	public void setChannelTransferList(ArrayList<ChannelTransferItemsVO> channelTransferList) {
		this.channelTransferList = channelTransferList;
	}
	
	public static ChannelTransferVO getInstance(){
		return new ChannelTransferVO();
	}

	public String getToGrphDomainCodeDesc() {
		return toGrphDomainCodeDesc;
	}

	public void setToGrphDomainCodeDesc(String toGrphDomainCodeDesc) {
		this.toGrphDomainCodeDesc = toGrphDomainCodeDesc;
	}
	
	private String multiCurrencyDetail;
	 
	
	public String getMultiCurrencyDetail() {
		return multiCurrencyDetail;
	}

	public void setMultiCurrencyDetail(String multiCurrencyDetail) {
		this.multiCurrencyDetail = multiCurrencyDetail;
	}

	public String getActiveUsersUserType() {
		return activeUsersUserType;
	}

	public void setActiveUsersUserType(String activeUsersUserType) {
		this.activeUsersUserType = activeUsersUserType;
	}
	
	public String getOptBatchNum() {
		return _optBatchNum;
	}

	public void setOptBatchNum(String optBatchNum) {
		_optBatchNum = optBatchNum;
	}
	
	private List<UserLoanVO> userLoanVOList;
	
	public List<UserLoanVO> getUserLoanVOList() {
		return userLoanVOList;
	}

	public void setUserLoanVOList(List<UserLoanVO> userLoanVOList) {
		this.userLoanVOList = userLoanVOList;
	}

	public String getTransferInitatorLoginID() {
		return transferInitatorLoginID;
	}

	public void setTransferInitatorLoginID(String transferInitatorLoginID) {
		this.transferInitatorLoginID = transferInitatorLoginID;
	}
	
}
