package com.restapi.o2c.service;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOTFCountsVO;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;

public class O2CApprovalTxnDetailsResponseVO extends BaseResponse {

	private String networkCode = null;
	private String networkName = null;
	private ArrayList domainList = null;
	private int listSize;
	private String domainCode = null;
	private String transferNum = null;
	private String distributorName = null;
	private String domainName = null;
	private String domainNameForUserCode = null;
	private String geographicDomainName = null;
	private String geographicDomainCode = null;
	private ArrayList geographicDomainList = null;
	private String userName = null;
	private String userNameTmp = null;
	private String primaryTxnNum = null;
	private String categoryName = null;
	private String categoryCode = null;
	private String categoryCodeForUserCode = null;
	private String gardeDesc = null;
	private String erpCode = null;
	private String productType = null;
	private String commissionProfileName = null;
	private String transferDate = null;
	private String refrenceNum = null;
	private String address = null;
	private String remarks = null;
	private String paymentInstrumentName = null;
	private String paymentInstrumentCode = null;
	private String paymentInstNum = null;
	private String paymentInstrumentDate = null;
	private String paymentInstrumentAmt = null;
	private String approve1Remark = null;
	private String approve2Remark = null;
	private String approve3Remark = null;
	private String currentApprovalLevel = null;

	private String selectedUserId = null;
	private String payableAmount = null;
	private String netPayableAmount = null;
	private boolean fromUserCodeFlag;

	private ArrayList channelTransferList = null;
	private ArrayList transferItemList = null;
	private String selectedTransfer = null;
	private String firstApprovalLimit = null;
	private String secondApprovalLimit = null;

	private String totalMRP = null;
	private String totalTax1 = null;
	private String totalTax2 = null;
	private String totalTax3 = null;
	private String totalComm = null;
	private String totalReqQty = null;
	private String totalStock = null;

	private String externalTxnNum = null;
	private String externalTxnDate = null;
	private String externalTxnExist = null;
	private String externalTxnMandatory = null;
	private String transferProfileName = null;
	private ArrayList categoryList = null;
	private String userCode = null;
	private int approvalLevel = 0;
	private String channelOwnerCategory = null;
	private String channelOwnerCategoryDesc = null;
	private String channelOwnerCategoryUserID = null;
	private String channelOwnerCategoryUserName = null;
	private boolean ownerSame = false;
	private ArrayList userList = null;
	private String userID = null;
	private String popUpUserID = null;
	private String allOrder = null;
	// to validate payment instruments
	private boolean validatePaymentInstruments = false;
	private boolean approvalDone = false;
	private String transferInitatorLoginID;

	// to load the all user's orders
	private String allUser = null;

	// to reject order confirmation
	private String rejectOrder = null;

	private String domainTypeCode = null; // for the domain type of the user
	// for EXTTXN number mandatory

	private String geoDomainNameForUser = null;
	private String geoDomainCodeForUser = null;
	private String sessionDomainCode = null;
	private long time = 0;
	private boolean isPrimaryNumber = true;
	private String toPrimaryMSISDN;
	// For mali changes - +ve commission appy
	private String receiverCreditQuantity = null;
	private String senderDebitQuantity = null;
	private String commissionQuantity = null;

	// for transfer quantity change while approval
	private String firstLevelApprovedQuantity = null;
	private String secondLevelApprovedQuantity = null;
	private String thirdLevelApprovedQuantity = null;
	private String totalInitialRequestedQuantity = null;

	private long transferMultipleOff;

	private ArrayList paymentInstrumentList = null;

	private boolean showPaymentDetails = false;
	private boolean showPaymentInstrumentType = false;

	// user life cycle
	private String channelUserStatus = null;

	private String totalCommValue = null;
	private String otfType;
	private Double otfRate;
	private Long otfValue;
	private String totalOtfValue;
	private boolean otfCountsUpdated = false;
	private String totalOthComm;
	private String dualCommissionType;
	private UserOTFCountsVO userOTFCountsVO;
	private String netPayableAmountApproval;
	private String payableAmountApproval;
	private ArrayList<ListValueVO> errorList = null;
	private ArrayList slabsList = null;
	private ArrayList<VomsCategoryVO> voucherTypeList;
	private String voucherType = null;
	private String voucherTypeDesc = null;
	private ArrayList vomsProductList = null;
	private ArrayList vomsCategoryList = null;
	private ArrayList<String> mrpList;
	private String vomsActiveMrp = null;
	private String paymentInstDesc;
	private String totalPayableAmount;
	private String totalNetPayableAmount;
	private String totalTransferedAmount;
	private ChannelTransferVO channelTransferVO;
	private String reportHeaderName;
	private boolean closeTransaction = false;
	private String segment = null;
	private String segmentDesc = null;
	// package distributor mode
	private List distributorModeList;
	private String distributorModeDesc;
	private String distributorMode;
	private String packageDetails;
	private String packageDetailsDesc;
	private List packageDetailsList;
	private String quantity;
	private String retPrice;
	private double packageTotal;
	private int slabsListSize;
	private String distributorModeValue; // to enable/disable distributor Mode option
	private boolean reconcilationFlag = false;
	private String commissionProfileID;
	private String commissionProfileVersion;
	
	
	
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public ArrayList getDomainList() {
		return domainList;
	}
	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}
	public int getListSize() {
		return listSize;
	}
	public void setListSize(int listSize) {
		this.listSize = listSize;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getTransferNum() {
		return transferNum;
	}
	public void setTransferNum(String transferNum) {
		this.transferNum = transferNum;
	}
	public String getDistributorName() {
		return distributorName;
	}
	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainNameForUserCode() {
		return domainNameForUserCode;
	}
	public void setDomainNameForUserCode(String domainNameForUserCode) {
		this.domainNameForUserCode = domainNameForUserCode;
	}
	public String getGeographicDomainName() {
		return geographicDomainName;
	}
	public void setGeographicDomainName(String geographicDomainName) {
		this.geographicDomainName = geographicDomainName;
	}
	public String getGeographicDomainCode() {
		return geographicDomainCode;
	}
	public void setGeographicDomainCode(String geographicDomainCode) {
		this.geographicDomainCode = geographicDomainCode;
	}
	public ArrayList getGeographicDomainList() {
		return geographicDomainList;
	}
	public void setGeographicDomainList(ArrayList geographicDomainList) {
		this.geographicDomainList = geographicDomainList;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNameTmp() {
		return userNameTmp;
	}
	public void setUserNameTmp(String userNameTmp) {
		this.userNameTmp = userNameTmp;
	}
	public String getPrimaryTxnNum() {
		return primaryTxnNum;
	}
	public void setPrimaryTxnNum(String primaryTxnNum) {
		this.primaryTxnNum = primaryTxnNum;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryCodeForUserCode() {
		return categoryCodeForUserCode;
	}
	public void setCategoryCodeForUserCode(String categoryCodeForUserCode) {
		this.categoryCodeForUserCode = categoryCodeForUserCode;
	}
	public String getGardeDesc() {
		return gardeDesc;
	}
	public void setGardeDesc(String gardeDesc) {
		this.gardeDesc = gardeDesc;
	}
	public String getErpCode() {
		return erpCode;
	}
	public void setErpCode(String erpCode) {
		this.erpCode = erpCode;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getCommissionProfileName() {
		return commissionProfileName;
	}
	public void setCommissionProfileName(String commissionProfileName) {
		this.commissionProfileName = commissionProfileName;
	}
	public String getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
	}
	public String getRefrenceNum() {
		return refrenceNum;
	}
	public void setRefrenceNum(String refrenceNum) {
		this.refrenceNum = refrenceNum;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getPaymentInstrumentName() {
		return paymentInstrumentName;
	}
	public void setPaymentInstrumentName(String paymentInstrumentName) {
		this.paymentInstrumentName = paymentInstrumentName;
	}
	public String getPaymentInstrumentCode() {
		return paymentInstrumentCode;
	}
	public void setPaymentInstrumentCode(String paymentInstrumentCode) {
		this.paymentInstrumentCode = paymentInstrumentCode;
	}
	public String getPaymentInstNum() {
		return paymentInstNum;
	}
	public void setPaymentInstNum(String paymentInstNum) {
		this.paymentInstNum = paymentInstNum;
	}
	public String getPaymentInstrumentDate() {
		return paymentInstrumentDate;
	}
	public void setPaymentInstrumentDate(String paymentInstrumentDate) {
		this.paymentInstrumentDate = paymentInstrumentDate;
	}
	public String getPaymentInstrumentAmt() {
		return paymentInstrumentAmt;
	}
	public void setPaymentInstrumentAmt(String paymentInstrumentAmt) {
		this.paymentInstrumentAmt = paymentInstrumentAmt;
	}
	public String getApprove1Remark() {
		return approve1Remark;
	}
	public void setApprove1Remark(String approve1Remark) {
		this.approve1Remark = approve1Remark;
	}
	public String getApprove2Remark() {
		return approve2Remark;
	}
	public void setApprove2Remark(String approve2Remark) {
		this.approve2Remark = approve2Remark;
	}
	public String getApprove3Remark() {
		return approve3Remark;
	}
	public void setApprove3Remark(String approve3Remark) {
		this.approve3Remark = approve3Remark;
	}
	public String getCurrentApprovalLevel() {
		return currentApprovalLevel;
	}
	public void setCurrentApprovalLevel(String currentApprovalLevel) {
		this.currentApprovalLevel = currentApprovalLevel;
	}
	public String getSelectedUserId() {
		return selectedUserId;
	}
	public void setSelectedUserId(String selectedUserId) {
		this.selectedUserId = selectedUserId;
	}
	public String getPayableAmount() {
		return payableAmount;
	}
	public void setPayableAmount(String payableAmount) {
		this.payableAmount = payableAmount;
	}
	public String getNetPayableAmount() {
		return netPayableAmount;
	}
	public void setNetPayableAmount(String netPayableAmount) {
		this.netPayableAmount = netPayableAmount;
	}
	public boolean isFromUserCodeFlag() {
		return fromUserCodeFlag;
	}
	public void setFromUserCodeFlag(boolean fromUserCodeFlag) {
		this.fromUserCodeFlag = fromUserCodeFlag;
	}
	public ArrayList getChannelTransferList() {
		return channelTransferList;
	}
	public void setChannelTransferList(ArrayList channelTransferList) {
		this.channelTransferList = channelTransferList;
	}
	public ArrayList getTransferItemList() {
		return transferItemList;
	}
	public void setTransferItemList(ArrayList transferItemList) {
		this.transferItemList = transferItemList;
	}
	public String getSelectedTransfer() {
		return selectedTransfer;
	}
	public void setSelectedTransfer(String selectedTransfer) {
		this.selectedTransfer = selectedTransfer;
	}
	public String getFirstApprovalLimit() {
		return firstApprovalLimit;
	}
	public void setFirstApprovalLimit(String firstApprovalLimit) {
		this.firstApprovalLimit = firstApprovalLimit;
	}
	public String getSecondApprovalLimit() {
		return secondApprovalLimit;
	}
	public void setSecondApprovalLimit(String secondApprovalLimit) {
		this.secondApprovalLimit = secondApprovalLimit;
	}
	public String getTotalMRP() {
		return totalMRP;
	}
	public void setTotalMRP(String totalMRP) {
		this.totalMRP = totalMRP;
	}
	public String getTotalTax1() {
		return totalTax1;
	}
	public void setTotalTax1(String totalTax1) {
		this.totalTax1 = totalTax1;
	}
	public String getTotalTax2() {
		return totalTax2;
	}
	public void setTotalTax2(String totalTax2) {
		this.totalTax2 = totalTax2;
	}
	public String getTotalTax3() {
		return totalTax3;
	}
	public void setTotalTax3(String totalTax3) {
		this.totalTax3 = totalTax3;
	}
	public String getTotalComm() {
		return totalComm;
	}
	public void setTotalComm(String totalComm) {
		this.totalComm = totalComm;
	}
	public String getTotalReqQty() {
		return totalReqQty;
	}
	public void setTotalReqQty(String totalReqQty) {
		this.totalReqQty = totalReqQty;
	}
	public String getTotalStock() {
		return totalStock;
	}
	public void setTotalStock(String totalStock) {
		this.totalStock = totalStock;
	}
	public String getExternalTxnNum() {
		return externalTxnNum;
	}
	public void setExternalTxnNum(String externalTxnNum) {
		this.externalTxnNum = externalTxnNum;
	}
	public String getExternalTxnDate() {
		return externalTxnDate;
	}
	public void setExternalTxnDate(String externalTxnDate) {
		this.externalTxnDate = externalTxnDate;
	}
	public String getExternalTxnExist() {
		return externalTxnExist;
	}
	public void setExternalTxnExist(String externalTxnExist) {
		this.externalTxnExist = externalTxnExist;
	}
	public String getExternalTxnMandatory() {
		return externalTxnMandatory;
	}
	public void setExternalTxnMandatory(String externalTxnMandatory) {
		this.externalTxnMandatory = externalTxnMandatory;
	}
	public String getTransferProfileName() {
		return transferProfileName;
	}
	public void setTransferProfileName(String transferProfileName) {
		this.transferProfileName = transferProfileName;
	}
	public ArrayList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public int getApprovalLevel() {
		return approvalLevel;
	}
	public void setApprovalLevel(int approvalLevel) {
		this.approvalLevel = approvalLevel;
	}
	public String getChannelOwnerCategory() {
		return channelOwnerCategory;
	}
	public void setChannelOwnerCategory(String channelOwnerCategory) {
		this.channelOwnerCategory = channelOwnerCategory;
	}
	public String getChannelOwnerCategoryDesc() {
		return channelOwnerCategoryDesc;
	}
	public void setChannelOwnerCategoryDesc(String channelOwnerCategoryDesc) {
		this.channelOwnerCategoryDesc = channelOwnerCategoryDesc;
	}
	public String getChannelOwnerCategoryUserID() {
		return channelOwnerCategoryUserID;
	}
	public void setChannelOwnerCategoryUserID(String channelOwnerCategoryUserID) {
		this.channelOwnerCategoryUserID = channelOwnerCategoryUserID;
	}
	public String getChannelOwnerCategoryUserName() {
		return channelOwnerCategoryUserName;
	}
	public void setChannelOwnerCategoryUserName(String channelOwnerCategoryUserName) {
		this.channelOwnerCategoryUserName = channelOwnerCategoryUserName;
	}
	public boolean isOwnerSame() {
		return ownerSame;
	}
	public void setOwnerSame(boolean ownerSame) {
		this.ownerSame = ownerSame;
	}
	public ArrayList getUserList() {
		return userList;
	}
	public void setUserList(ArrayList userList) {
		this.userList = userList;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPopUpUserID() {
		return popUpUserID;
	}
	public void setPopUpUserID(String popUpUserID) {
		this.popUpUserID = popUpUserID;
	}
	public String getAllOrder() {
		return allOrder;
	}
	public void setAllOrder(String allOrder) {
		this.allOrder = allOrder;
	}
	public boolean isValidatePaymentInstruments() {
		return validatePaymentInstruments;
	}
	public void setValidatePaymentInstruments(boolean validatePaymentInstruments) {
		this.validatePaymentInstruments = validatePaymentInstruments;
	}
	public boolean isApprovalDone() {
		return approvalDone;
	}
	public void setApprovalDone(boolean approvalDone) {
		this.approvalDone = approvalDone;
	}
	public String getAllUser() {
		return allUser;
	}
	public void setAllUser(String allUser) {
		this.allUser = allUser;
	}
	public String getRejectOrder() {
		return rejectOrder;
	}
	public void setRejectOrder(String rejectOrder) {
		this.rejectOrder = rejectOrder;
	}
	public String getDomainTypeCode() {
		return domainTypeCode;
	}
	public void setDomainTypeCode(String domainTypeCode) {
		this.domainTypeCode = domainTypeCode;
	}
	public String getGeoDomainNameForUser() {
		return geoDomainNameForUser;
	}
	public void setGeoDomainNameForUser(String geoDomainNameForUser) {
		this.geoDomainNameForUser = geoDomainNameForUser;
	}
	public String getGeoDomainCodeForUser() {
		return geoDomainCodeForUser;
	}
	public void setGeoDomainCodeForUser(String geoDomainCodeForUser) {
		this.geoDomainCodeForUser = geoDomainCodeForUser;
	}
	public String getSessionDomainCode() {
		return sessionDomainCode;
	}
	public void setSessionDomainCode(String sessionDomainCode) {
		this.sessionDomainCode = sessionDomainCode;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public boolean isPrimaryNumber() {
		return isPrimaryNumber;
	}
	public void setPrimaryNumber(boolean isPrimaryNumber) {
		this.isPrimaryNumber = isPrimaryNumber;
	}
	public String getToPrimaryMSISDN() {
		return toPrimaryMSISDN;
	}
	public void setToPrimaryMSISDN(String toPrimaryMSISDN) {
		this.toPrimaryMSISDN = toPrimaryMSISDN;
	}
	public String getReceiverCreditQuantity() {
		return receiverCreditQuantity;
	}
	public void setReceiverCreditQuantity(String receiverCreditQuantity) {
		this.receiverCreditQuantity = receiverCreditQuantity;
	}
	public String getSenderDebitQuantity() {
		return senderDebitQuantity;
	}
	public void setSenderDebitQuantity(String senderDebitQuantity) {
		this.senderDebitQuantity = senderDebitQuantity;
	}
	public String getCommissionQuantity() {
		return commissionQuantity;
	}
	public void setCommissionQuantity(String commissionQuantity) {
		this.commissionQuantity = commissionQuantity;
	}
	public String getFirstLevelApprovedQuantity() {
		return firstLevelApprovedQuantity;
	}
	public void setFirstLevelApprovedQuantity(String firstLevelApprovedQuantity) {
		this.firstLevelApprovedQuantity = firstLevelApprovedQuantity;
	}
	public String getSecondLevelApprovedQuantity() {
		return secondLevelApprovedQuantity;
	}
	public void setSecondLevelApprovedQuantity(String secondLevelApprovedQuantity) {
		this.secondLevelApprovedQuantity = secondLevelApprovedQuantity;
	}
	public String getThirdLevelApprovedQuantity() {
		return thirdLevelApprovedQuantity;
	}
	public void setThirdLevelApprovedQuantity(String thirdLevelApprovedQuantity) {
		this.thirdLevelApprovedQuantity = thirdLevelApprovedQuantity;
	}
	public String getTotalInitialRequestedQuantity() {
		return totalInitialRequestedQuantity;
	}
	public void setTotalInitialRequestedQuantity(String totalInitialRequestedQuantity) {
		this.totalInitialRequestedQuantity = totalInitialRequestedQuantity;
	}
	public long getTransferMultipleOff() {
		return transferMultipleOff;
	}
	public void setTransferMultipleOff(long transferMultipleOff) {
		this.transferMultipleOff = transferMultipleOff;
	}
	public ArrayList getPaymentInstrumentList() {
		return paymentInstrumentList;
	}
	public void setPaymentInstrumentList(ArrayList paymentInstrumentList) {
		this.paymentInstrumentList = paymentInstrumentList;
	}
	public boolean isShowPaymentDetails() {
		return showPaymentDetails;
	}
	public void setShowPaymentDetails(boolean showPaymentDetails) {
		this.showPaymentDetails = showPaymentDetails;
	}
	public boolean isShowPaymentInstrumentType() {
		return showPaymentInstrumentType;
	}
	public void setShowPaymentInstrumentType(boolean showPaymentInstrumentType) {
		this.showPaymentInstrumentType = showPaymentInstrumentType;
	}
	public String getChannelUserStatus() {
		return channelUserStatus;
	}
	public void setChannelUserStatus(String channelUserStatus) {
		this.channelUserStatus = channelUserStatus;
	}
	public String getTotalCommValue() {
		return totalCommValue;
	}
	public void setTotalCommValue(String totalCommValue) {
		this.totalCommValue = totalCommValue;
	}
	public String getOtfType() {
		return otfType;
	}
	public void setOtfType(String otfType) {
		this.otfType = otfType;
	}
	public Double getOtfRate() {
		return otfRate;
	}
	public void setOtfRate(Double otfRate) {
		this.otfRate = otfRate;
	}
	public Long getOtfValue() {
		return otfValue;
	}
	public void setOtfValue(Long otfValue) {
		this.otfValue = otfValue;
	}
	public String getTotalOtfValue() {
		return totalOtfValue;
	}
	public void setTotalOtfValue(String totalOtfValue) {
		this.totalOtfValue = totalOtfValue;
	}
	public boolean isOtfCountsUpdated() {
		return otfCountsUpdated;
	}
	public void setOtfCountsUpdated(boolean otfCountsUpdated) {
		this.otfCountsUpdated = otfCountsUpdated;
	}
	public String getTotalOthComm() {
		return totalOthComm;
	}
	public void setTotalOthComm(String totalOthComm) {
		this.totalOthComm = totalOthComm;
	}
	public String getDualCommissionType() {
		return dualCommissionType;
	}
	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}
	public UserOTFCountsVO getUserOTFCountsVO() {
		return userOTFCountsVO;
	}
	public void setUserOTFCountsVO(UserOTFCountsVO userOTFCountsVO) {
		this.userOTFCountsVO = userOTFCountsVO;
	}
	public String getNetPayableAmountApproval() {
		return netPayableAmountApproval;
	}
	public void setNetPayableAmountApproval(String netPayableAmountApproval) {
		this.netPayableAmountApproval = netPayableAmountApproval;
	}
	public String getPayableAmountApproval() {
		return payableAmountApproval;
	}
	public void setPayableAmountApproval(String payableAmountApproval) {
		this.payableAmountApproval = payableAmountApproval;
	}
	public ArrayList<ListValueVO> getErrorList() {
		return errorList;
	}
	public void setErrorList(ArrayList<ListValueVO> errorList) {
		this.errorList = errorList;
	}
	public ArrayList getSlabsList() {
		return slabsList;
	}
	public void setSlabsList(ArrayList slabsList) {
		this.slabsList = slabsList;
	}
	public ArrayList<VomsCategoryVO> getVoucherTypeList() {
		return voucherTypeList;
	}
	public void setVoucherTypeList(ArrayList<VomsCategoryVO> voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}
	public String getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	public String getVoucherTypeDesc() {
		return voucherTypeDesc;
	}
	public void setVoucherTypeDesc(String voucherTypeDesc) {
		this.voucherTypeDesc = voucherTypeDesc;
	}
	public ArrayList getVomsProductList() {
		return vomsProductList;
	}
	public void setVomsProductList(ArrayList vomsProductList) {
		this.vomsProductList = vomsProductList;
	}
	public ArrayList getVomsCategoryList() {
		return vomsCategoryList;
	}
	public void setVomsCategoryList(ArrayList vomsCategoryList) {
		this.vomsCategoryList = vomsCategoryList;
	}
	public ArrayList<String> getMrpList() {
		return mrpList;
	}
	public void setMrpList(ArrayList<String> mrpList) {
		this.mrpList = mrpList;
	}
	public String getVomsActiveMrp() {
		return vomsActiveMrp;
	}
	public void setVomsActiveMrp(String vomsActiveMrp) {
		this.vomsActiveMrp = vomsActiveMrp;
	}
	public String getPaymentInstDesc() {
		return paymentInstDesc;
	}
	public void setPaymentInstDesc(String paymentInstDesc) {
		this.paymentInstDesc = paymentInstDesc;
	}
	public String getTotalPayableAmount() {
		return totalPayableAmount;
	}
	public void setTotalPayableAmount(String totalPayableAmount) {
		this.totalPayableAmount = totalPayableAmount;
	}
	public String getTotalNetPayableAmount() {
		return totalNetPayableAmount;
	}
	public void setTotalNetPayableAmount(String totalNetPayableAmount) {
		this.totalNetPayableAmount = totalNetPayableAmount;
	}
	public String getTotalTransferedAmount() {
		return totalTransferedAmount;
	}
	public void setTotalTransferedAmount(String totalTransferedAmount) {
		this.totalTransferedAmount = totalTransferedAmount;
	}
	public ChannelTransferVO getChannelTransferVO() {
		return channelTransferVO;
	}
	public void setChannelTransferVO(ChannelTransferVO channelTransferVO) {
		this.channelTransferVO = channelTransferVO;
	}
	public String getReportHeaderName() {
		return reportHeaderName;
	}
	public void setReportHeaderName(String reportHeaderName) {
		this.reportHeaderName = reportHeaderName;
	}
	public boolean isCloseTransaction() {
		return closeTransaction;
	}
	public void setCloseTransaction(boolean closeTransaction) {
		this.closeTransaction = closeTransaction;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getSegmentDesc() {
		return segmentDesc;
	}
	public void setSegmentDesc(String segmentDesc) {
		this.segmentDesc = segmentDesc;
	}
	public List getDistributorModeList() {
		return distributorModeList;
	}
	public void setDistributorModeList(List distributorModeList) {
		this.distributorModeList = distributorModeList;
	}
	public String getDistributorModeDesc() {
		return distributorModeDesc;
	}
	public void setDistributorModeDesc(String distributorModeDesc) {
		this.distributorModeDesc = distributorModeDesc;
	}
	public String getDistributorMode() {
		return distributorMode;
	}
	public void setDistributorMode(String distributorMode) {
		this.distributorMode = distributorMode;
	}
	public String getPackageDetails() {
		return packageDetails;
	}
	public void setPackageDetails(String packageDetails) {
		this.packageDetails = packageDetails;
	}
	public String getPackageDetailsDesc() {
		return packageDetailsDesc;
	}
	public void setPackageDetailsDesc(String packageDetailsDesc) {
		this.packageDetailsDesc = packageDetailsDesc;
	}
	public List getPackageDetailsList() {
		return packageDetailsList;
	}
	public void setPackageDetailsList(List packageDetailsList) {
		this.packageDetailsList = packageDetailsList;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getRetPrice() {
		return retPrice;
	}
	public void setRetPrice(String retPrice) {
		this.retPrice = retPrice;
	}
	public double getPackageTotal() {
		return packageTotal;
	}
	public void setPackageTotal(double packageTotal) {
		this.packageTotal = packageTotal;
	}
	public int getSlabsListSize() {
		return slabsListSize;
	}
	public void setSlabsListSize(int slabsListSize) {
		this.slabsListSize = slabsListSize;
	}
	public String getDistributorModeValue() {
		return distributorModeValue;
	}
	public void setDistributorModeValue(String distributorModeValue) {
		this.distributorModeValue = distributorModeValue;
	}
	public boolean isReconcilationFlag() {
		return reconcilationFlag;
	}
	public void setReconcilationFlag(boolean reconcilationFlag) {
		this.reconcilationFlag = reconcilationFlag;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CApprovalTxnDetailsResponseVO [networkCode=");
		builder.append(networkCode);
		builder.append(", networkName=");
		builder.append(networkName);
		builder.append(", domainList=");
		builder.append(domainList);
		builder.append(", listSize=");
		builder.append(listSize);
		builder.append(", domainCode=");
		builder.append(domainCode);
		builder.append(", transferNum=");
		builder.append(transferNum);
		builder.append(", distributorName=");
		builder.append(distributorName);
		builder.append(", domainName=");
		builder.append(domainName);
		builder.append(", domainNameForUserCode=");
		builder.append(domainNameForUserCode);
		builder.append(", geographicDomainName=");
		builder.append(geographicDomainName);
		builder.append(", geographicDomainCode=");
		builder.append(geographicDomainCode);
		builder.append(", geographicDomainList=");
		builder.append(geographicDomainList);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", userNameTmp=");
		builder.append(userNameTmp);
		builder.append(", primaryTxnNum=");
		builder.append(primaryTxnNum);
		builder.append(", categoryName=");
		builder.append(categoryName);
		builder.append(", categoryCode=");
		builder.append(categoryCode);
		builder.append(", categoryCodeForUserCode=");
		builder.append(categoryCodeForUserCode);
		builder.append(", gardeDesc=");
		builder.append(gardeDesc);
		builder.append(", erpCode=");
		builder.append(erpCode);
		builder.append(", productType=");
		builder.append(productType);
		builder.append(", commissionProfileName=");
		builder.append(commissionProfileName);
		builder.append(", transferDate=");
		builder.append(transferDate);
		builder.append(", refrenceNum=");
		builder.append(refrenceNum);
		builder.append(", address=");
		builder.append(address);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", paymentInstrumentName=");
		builder.append(paymentInstrumentName);
		builder.append(", paymentInstrumentCode=");
		builder.append(paymentInstrumentCode);
		builder.append(", paymentInstNum=");
		builder.append(paymentInstNum);
		builder.append(", paymentInstrumentDate=");
		builder.append(paymentInstrumentDate);
		builder.append(", paymentInstrumentAmt=");
		builder.append(paymentInstrumentAmt);
		builder.append(", approve1Remark=");
		builder.append(approve1Remark);
		builder.append(", approve2Remark=");
		builder.append(approve2Remark);
		builder.append(", approve3Remark=");
		builder.append(approve3Remark);
		builder.append(", currentApprovalLevel=");
		builder.append(currentApprovalLevel);
		builder.append(", selectedUserId=");
		builder.append(selectedUserId);
		builder.append(", payableAmount=");
		builder.append(payableAmount);
		builder.append(", netPayableAmount=");
		builder.append(netPayableAmount);
		builder.append(", fromUserCodeFlag=");
		builder.append(fromUserCodeFlag);
		builder.append(", channelTransferList=");
		builder.append(channelTransferList);
		builder.append(", transferItemList=");
		builder.append(transferItemList);
		builder.append(", selectedTransfer=");
		builder.append(selectedTransfer);
		builder.append(", firstApprovalLimit=");
		builder.append(firstApprovalLimit);
		builder.append(", secondApprovalLimit=");
		builder.append(secondApprovalLimit);
		builder.append(", totalMRP=");
		builder.append(totalMRP);
		builder.append(", totalTax1=");
		builder.append(totalTax1);
		builder.append(", totalTax2=");
		builder.append(totalTax2);
		builder.append(", totalTax3=");
		builder.append(totalTax3);
		builder.append(", totalComm=");
		builder.append(totalComm);
		builder.append(", totalReqQty=");
		builder.append(totalReqQty);
		builder.append(", totalStock=");
		builder.append(totalStock);
		builder.append(", externalTxnNum=");
		builder.append(externalTxnNum);
		builder.append(", externalTxnDate=");
		builder.append(externalTxnDate);
		builder.append(", externalTxnExist=");
		builder.append(externalTxnExist);
		builder.append(", externalTxnMandatory=");
		builder.append(externalTxnMandatory);
		builder.append(", transferProfileName=");
		builder.append(transferProfileName);
		builder.append(", categoryList=");
		builder.append(categoryList);
		builder.append(", userCode=");
		builder.append(userCode);
		builder.append(", approvalLevel=");
		builder.append(approvalLevel);
		builder.append(", channelOwnerCategory=");
		builder.append(channelOwnerCategory);
		builder.append(", channelOwnerCategoryDesc=");
		builder.append(channelOwnerCategoryDesc);
		builder.append(", channelOwnerCategoryUserID=");
		builder.append(channelOwnerCategoryUserID);
		builder.append(", channelOwnerCategoryUserName=");
		builder.append(channelOwnerCategoryUserName);
		builder.append(", ownerSame=");
		builder.append(ownerSame);
		builder.append(", userList=");
		builder.append(userList);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", popUpUserID=");
		builder.append(popUpUserID);
		builder.append(", allOrder=");
		builder.append(allOrder);
		builder.append(", validatePaymentInstruments=");
		builder.append(validatePaymentInstruments);
		builder.append(", approvalDone=");
		builder.append(approvalDone);
		builder.append(", allUser=");
		builder.append(allUser);
		builder.append(", rejectOrder=");
		builder.append(rejectOrder);
		builder.append(", domainTypeCode=");
		builder.append(domainTypeCode);
		builder.append(", geoDomainNameForUser=");
		builder.append(geoDomainNameForUser);
		builder.append(", geoDomainCodeForUser=");
		builder.append(geoDomainCodeForUser);
		builder.append(", sessionDomainCode=");
		builder.append(sessionDomainCode);
		builder.append(", time=");
		builder.append(time);
		builder.append(", isPrimaryNumber=");
		builder.append(isPrimaryNumber);
		builder.append(", toPrimaryMSISDN=");
		builder.append(toPrimaryMSISDN);
		builder.append(", receiverCreditQuantity=");
		builder.append(receiverCreditQuantity);
		builder.append(", senderDebitQuantity=");
		builder.append(senderDebitQuantity);
		builder.append(", commissionQuantity=");
		builder.append(commissionQuantity);
		builder.append(", firstLevelApprovedQuantity=");
		builder.append(firstLevelApprovedQuantity);
		builder.append(", secondLevelApprovedQuantity=");
		builder.append(secondLevelApprovedQuantity);
		builder.append(", thirdLevelApprovedQuantity=");
		builder.append(thirdLevelApprovedQuantity);
		builder.append(", totalInitialRequestedQuantity=");
		builder.append(totalInitialRequestedQuantity);
		builder.append(", transferMultipleOff=");
		builder.append(transferMultipleOff);
		builder.append(", paymentInstrumentList=");
		builder.append(paymentInstrumentList);
		builder.append(", showPaymentDetails=");
		builder.append(showPaymentDetails);
		builder.append(", showPaymentInstrumentType=");
		builder.append(showPaymentInstrumentType);
		builder.append(", channelUserStatus=");
		builder.append(channelUserStatus);
		builder.append(", totalCommValue=");
		builder.append(totalCommValue);
		builder.append(", otfType=");
		builder.append(otfType);
		builder.append(", otfRate=");
		builder.append(otfRate);
		builder.append(", otfValue=");
		builder.append(otfValue);
		builder.append(", totalOtfValue=");
		builder.append(totalOtfValue);
		builder.append(", otfCountsUpdated=");
		builder.append(otfCountsUpdated);
		builder.append(", totalOthComm=");
		builder.append(totalOthComm);
		builder.append(", dualCommissionType=");
		builder.append(dualCommissionType);
		builder.append(", userOTFCountsVO=");
		builder.append(userOTFCountsVO);
		builder.append(", netPayableAmountApproval=");
		builder.append(netPayableAmountApproval);
		builder.append(", payableAmountApproval=");
		builder.append(payableAmountApproval);
		builder.append(", errorList=");
		builder.append(errorList);
		builder.append(", slabsList=");
		builder.append(slabsList);
		builder.append(", voucherTypeList=");
		builder.append(voucherTypeList);
		builder.append(", voucherType=");
		builder.append(voucherType);
		builder.append(", voucherTypeDesc=");
		builder.append(voucherTypeDesc);
		builder.append(", vomsProductList=");
		builder.append(vomsProductList);
		builder.append(", vomsCategoryList=");
		builder.append(vomsCategoryList);
		builder.append(", mrpList=");
		builder.append(mrpList);
		builder.append(", vomsActiveMrp=");
		builder.append(vomsActiveMrp);
		builder.append(", paymentInstDesc=");
		builder.append(paymentInstDesc);
		builder.append(", totalPayableAmount=");
		builder.append(totalPayableAmount);
		builder.append(", totalNetPayableAmount=");
		builder.append(totalNetPayableAmount);
		builder.append(", totalTransferedAmount=");
		builder.append(totalTransferedAmount);
		builder.append(", channelTransferVO=");
		builder.append(channelTransferVO);
		builder.append(", reportHeaderName=");
		builder.append(reportHeaderName);
		builder.append(", closeTransaction=");
		builder.append(closeTransaction);
		builder.append(", segment=");
		builder.append(segment);
		builder.append(", segmentDesc=");
		builder.append(segmentDesc);
		builder.append(", distributorModeList=");
		builder.append(distributorModeList);
		builder.append(", distributorModeDesc=");
		builder.append(distributorModeDesc);
		builder.append(", distributorMode=");
		builder.append(distributorMode);
		builder.append(", packageDetails=");
		builder.append(packageDetails);
		builder.append(", packageDetailsDesc=");
		builder.append(packageDetailsDesc);
		builder.append(", packageDetailsList=");
		builder.append(packageDetailsList);
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append(", retPrice=");
		builder.append(retPrice);
		builder.append(", packageTotal=");
		builder.append(packageTotal);
		builder.append(", slabsListSize=");
		builder.append(slabsListSize);
		builder.append(", distributorModeValue=");
		builder.append(distributorModeValue);
		builder.append(", reconcilationFlag=");
		builder.append(reconcilationFlag);
		builder.append("]");
		return builder.toString();
	}
	
	public String getCommissionProfileID() {
		return commissionProfileID;
	}
	public void setCommissionProfileID(String commissionProfileID) {
		this.commissionProfileID = commissionProfileID;
	}
	public String getCommissionProfileVersion() {
		return commissionProfileVersion;
	}
	public void setCommissionProfileVersion(String commissionProfileVersion) {
		this.commissionProfileVersion = commissionProfileVersion;
	}
	public String getTransferInitatorLoginID() {
		return transferInitatorLoginID;
	}
	public void setTransferInitatorLoginID(String transferInitatorLoginID) {
		this.transferInitatorLoginID = transferInitatorLoginID;
	}
	
	

}
