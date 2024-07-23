package com.restapi.o2c.service;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;

public class ApprovalVO {

	private String requestType = null;
	private String networkCode = null;
	private String networkCodeDesc = null;
	private String geographicalDomainCode = null;
	private String geographicalDomainCodeDesc = null;
	private ArrayList geographicalDomainList = null;
	private String domainCode = null;
    private String domainCodeDesc = null;
    private ArrayList domainList = null;
    private String categoryCode = null;
    private String categoryCodeDesc = null;
	private ArrayList categoryList = null;
	private ArrayList productTypeList = null;
	private String productTypeCode = null;
	private String productTypeCodeDesc = null;
	private String searchCriteria = null;
	private String geoDomainCodeDescForUser = null;
	private String userGradeName = null;
	private String erpCode = null;

	private String channelUserName = null;
	private String channelUserID = null;
	private String searchUserName = null;
	private String searchUserID = null;
	private ArrayList userList = null;

	private String userName = null;
	private String userID = null;
	private String userCode = null;
	private String userMsisdn = null;
	private String userCommProfileSetID = null;
	private String userCommProfileSetVersion = null;
	private String userCommProfileName = null;
	private String userTransferProfileCode = null;
	private String userTransferProfileName = null;
	private String currentDate = null;
	private String reference = null;
	private String productQuantity = null;
	private String remarks = null;
	private String address = null;
	private String refrenceNum = null;
	private String externalTxnExist = null;
	private String externalTxnMandatory = null;
	private String externalTxnNum = null;
	private String externalTxnDate = null;
	private String transferCategory = null;
	
	private String defaultLang = null;
	private String secondLang = null;

	private String totalMRP = null;
	private String totalTax1 = null;
	private String totalTax2 = null;
    // private String _totalTax3=null;
	private String totalReqQty = null;
	private String totalTransferedAmount = null;
    // private String _totalComm=null;
	private ArrayList productListWithTaxes = null;
	private String transferNumber = null;
	private String transferDateAsString = null;
	private String totalStock = null;
	private ArrayList transferItemList = null;
	private int focOrderApprovalLevel;
	private String approve1Remark = null;
	private String approve2Remark = null;
	private String approve3Remark = null;
	private String sessionDomainCode = null;
	private String toPrimaryMSISDN = null;
	private ChannelTransferVO channelTransferVO = null;
	private String domainTypeCode = null;
	private String channelUserStatus = null;
	private String status = null;
	
    // for Mali -- +ve commision apply
	private String receiverCrQuantity = null;
	private String senderDrQuantity = null;
	private String netCommissionQuantity = null;;
	private List<ListValueVO> walletTypeList = null;
	private String walletTypeWithWalletCode;
	private String walletCode;
	private boolean multiWallet = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue();
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getNetworkCodeDesc() {
		return networkCodeDesc;
	}
	public void setNetworkCodeDesc(String networkCodeDesc) {
		this.networkCodeDesc = networkCodeDesc;
	}
	public String getGeographicalDomainCode() {
		return geographicalDomainCode;
	}
	public void setGeographicalDomainCode(String geographicalDomainCode) {
		this.geographicalDomainCode = geographicalDomainCode;
	}
	public String getGeographicalDomainCodeDesc() {
		return geographicalDomainCodeDesc;
	}
	public void setGeographicalDomainCodeDesc(String geographicalDomainCodeDesc) {
		this.geographicalDomainCodeDesc = geographicalDomainCodeDesc;
	}
	public ArrayList getGeographicalDomainList() {
		return geographicalDomainList;
	}
	public void setGeographicalDomainList(ArrayList geographicalDomainList) {
		this.geographicalDomainList = geographicalDomainList;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getDomainCodeDesc() {
		return domainCodeDesc;
	}
	public void setDomainCodeDesc(String domainCodeDesc) {
		this.domainCodeDesc = domainCodeDesc;
	}
	public ArrayList getDomainList() {
		return domainList;
	}
	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryCodeDesc() {
		return categoryCodeDesc;
	}
	public void setCategoryCodeDesc(String categoryCodeDesc) {
		this.categoryCodeDesc = categoryCodeDesc;
	}
	public ArrayList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	public ArrayList getProductTypeList() {
		return productTypeList;
	}
	public void setProductTypeList(ArrayList productTypeList) {
		this.productTypeList = productTypeList;
	}
	public String getProductTypeCode() {
		return productTypeCode;
	}
	public void setProductTypeCode(String productTypeCode) {
		this.productTypeCode = productTypeCode;
	}
	public String getProductTypeCodeDesc() {
		return productTypeCodeDesc;
	}
	public void setProductTypeCodeDesc(String productTypeCodeDesc) {
		this.productTypeCodeDesc = productTypeCodeDesc;
	}
	public String getSearchCriteria() {
		return searchCriteria;
	}
	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
	public String getGeoDomainCodeDescForUser() {
		return geoDomainCodeDescForUser;
	}
	public void setGeoDomainCodeDescForUser(String geoDomainCodeDescForUser) {
		this.geoDomainCodeDescForUser = geoDomainCodeDescForUser;
	}
	public String getChannelUserName() {
		return channelUserName;
	}
	public void setChannelUserName(String channelUserName) {
		this.channelUserName = channelUserName;
	}
	public String getChannelUserID() {
		return channelUserID;
	}
	public void setChannelUserID(String channelUserID) {
		this.channelUserID = channelUserID;
	}
	public String getSearchUserName() {
		return searchUserName;
	}
	public void setSearchUserName(String searchUserName) {
		this.searchUserName = searchUserName;
	}
	public String getSearchUserID() {
		return searchUserID;
	}
	public void setSearchUserID(String searchUserID) {
		this.searchUserID = searchUserID;
	}
	public ArrayList getUserList() {
		return userList;
	}
	public void setUserList(ArrayList userList) {
		this.userList = userList;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserMsisdn() {
		return userMsisdn;
	}
	public void setUserMsisdn(String userMsisdn) {
		this.userMsisdn = userMsisdn;
	}
	public String getUserCommProfileSetID() {
		return userCommProfileSetID;
	}
	public void setUserCommProfileSetID(String userCommProfileSetID) {
		this.userCommProfileSetID = userCommProfileSetID;
	}
	public String getUserCommProfileSetVersion() {
		return userCommProfileSetVersion;
	}
	public void setUserCommProfileSetVersion(String userCommProfileSetVersion) {
		this.userCommProfileSetVersion = userCommProfileSetVersion;
	}
	public String getUserCommProfileName() {
		return userCommProfileName;
	}
	public void setUserCommProfileName(String userCommProfileName) {
		this.userCommProfileName = userCommProfileName;
	}
	public String getUserTransferProfileCode() {
		return userTransferProfileCode;
	}
	public void setUserTransferProfileCode(String userTransferProfileCode) {
		this.userTransferProfileCode = userTransferProfileCode;
	}
	public String getUserTransferProfileName() {
		return userTransferProfileName;
	}
	public void setUserTransferProfileName(String userTransferProfileName) {
		this.userTransferProfileName = userTransferProfileName;
	}
	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getProductQuantity() {
		return productQuantity;
	}
	public void setProductQuantity(String productQuantity) {
		this.productQuantity = productQuantity;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRefrenceNum() {
		return refrenceNum;
	}
	public void setRefrenceNum(String refrenceNum) {
		this.refrenceNum = refrenceNum;
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
	public String getTransferCategory() {
		return transferCategory;
	}
	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}
	public String getDefaultLang() {
		return defaultLang;
	}
	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}
	public String getSecondLang() {
		return secondLang;
	}
	public void setSecondLang(String secondLang) {
		this.secondLang = secondLang;
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
	public String getTotalReqQty() {
		return totalReqQty;
	}
	public void setTotalReqQty(String totalReqQty) {
		this.totalReqQty = totalReqQty;
	}
	public String getTotalTransferedAmount() {
		return totalTransferedAmount;
	}
	public void setTotalTransferedAmount(String totalTransferedAmount) {
		this.totalTransferedAmount = totalTransferedAmount;
	}
	public ArrayList getProductListWithTaxes() {
		return productListWithTaxes;
	}
	public void setProductListWithTaxes(ArrayList productListWithTaxes) {
		this.productListWithTaxes = productListWithTaxes;
	}
	public String getTransferNumber() {
		return transferNumber;
	}
	public void setTransferNumber(String transferNumber) {
		this.transferNumber = transferNumber;
	}
	public String getTransferDateAsString() {
		return transferDateAsString;
	}
	public void setTransferDateAsString(String transferDateAsString) {
		this.transferDateAsString = transferDateAsString;
	}
	public String getTotalStock() {
		return totalStock;
	}
	public void setTotalStock(String totalStock) {
		this.totalStock = totalStock;
	}
	public ArrayList getTransferItemList() {
		return transferItemList;
	}
	public void setTransferItemList(ArrayList transferItemList) {
		this.transferItemList = transferItemList;
	}
	public int getFocOrderApprovalLevel() {
		return focOrderApprovalLevel;
	}
	public void setFocOrderApprovalLevel(int focOrderApprovalLevel) {
		this.focOrderApprovalLevel = focOrderApprovalLevel;
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
	public String getSessionDomainCode() {
		return sessionDomainCode;
	}
	public void setSessionDomainCode(String sessionDomainCode) {
		this.sessionDomainCode = sessionDomainCode;
	}
	public String getToPrimaryMSISDN() {
		return toPrimaryMSISDN;
	}
	public void setToPrimaryMSISDN(String toPrimaryMSISDN) {
		this.toPrimaryMSISDN = toPrimaryMSISDN;
	}
	public ChannelTransferVO getChannelTransferVO() {
		return channelTransferVO;
	}
	public void setChannelTransferVO(ChannelTransferVO channelTransferVO) {
		this.channelTransferVO = channelTransferVO;
	}
	public String getDomainTypeCode() {
		return domainTypeCode;
	}
	public void setDomainTypeCode(String domainTypeCode) {
		this.domainTypeCode = domainTypeCode;
	}
	public String getChannelUserStatus() {
		return channelUserStatus;
	}
	public void setChannelUserStatus(String channelUserStatus) {
		this.channelUserStatus = channelUserStatus;
	}
	public String getReceiverCrQuantity() {
		return receiverCrQuantity;
	}
	public void setReceiverCrQuantity(String receiverCrQuantity) {
		this.receiverCrQuantity = receiverCrQuantity;
	}
	public String getSenderDrQuantity() {
		return senderDrQuantity;
	}
	public void setSenderDrQuantity(String senderDrQuantity) {
		this.senderDrQuantity = senderDrQuantity;
	}
	public String getNetCommissionQuantity() {
		return netCommissionQuantity;
	}
	public void setNetCommissionQuantity(String netCommissionQuantity) {
		this.netCommissionQuantity = netCommissionQuantity;
	}
	public List<ListValueVO> getWalletTypeList() {
		return walletTypeList;
	}
	public void setWalletTypeList(List<ListValueVO> walletTypeList) {
		this.walletTypeList = walletTypeList;
	}
	public String getWalletTypeWithWalletCode() {
		return walletTypeWithWalletCode;
	}
	public void setWalletTypeWithWalletCode(String walletTypeWithWalletCode) {
		this.walletTypeWithWalletCode = walletTypeWithWalletCode;
	}
	public String getWalletCode() {
		return walletCode;
	}
	public void setWalletCode(String walletCode) {
		this.walletCode = walletCode;
	}
	public boolean isMultiWallet() {
		return multiWallet;
	}
	public void setMultiWallet(boolean multiWallet) {
		this.multiWallet = multiWallet;
	}
	public String getUserGradeName() {
		return userGradeName;
	}
	public void setUserGradeName(String userGradeName) {
		this.userGradeName = userGradeName;
	}
	public String getErpCode() {
		return erpCode;
	}
	public void setErpCode(String erpCode) {
		this.erpCode = erpCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
}
