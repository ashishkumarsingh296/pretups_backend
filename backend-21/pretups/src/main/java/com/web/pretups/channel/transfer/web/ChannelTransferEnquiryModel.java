package com.web.pretups.channel.transfer.web;
import java.util.ArrayList;
public class ChannelTransferEnquiryModel {
	/**
	 * @(#)ChannelTransferEnquiryForm.java
	 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
	 *                                     All Rights Reserved
	 * 
	 *                                     <description>
	 *                                     ----------------------------------------
	 *                                     --
	 *                                     ----------------------------------------
	 *                                     ---------------
	 *                                     Author Date History
	 *                                     ----------------------------------------
	 *                                     --
	 *                                     ----------------------------------------
	 *                                     ---------------
	 *                                     avinash.kamthan Aug 19, 2005 Initital
	 *                                     Creation
	 *                                     Sandeep Goel Nov 10,2005 Modification and
	 *                                     customization
	 *                                     ----------------------------------------
	 *                                     --
	 *                                     ----------------------------------------
	 *                                     ---------------
	 * 
	 */

	

	

	/**
	 * @author avinash.kamthan
	 * 
	 */
	

	    private ArrayList geoDomainList;
	    private ArrayList channelDomainList;
	    private ArrayList productsTypeList;
	    private ArrayList categoryList;

	    private String geoDomainCode;
	    private String channelDomain;
	    private String productType;
	    private String categoryCode;
	    private String geoDomainCodeDesc;
	    private String channelDomainDesc;
	    private String productTypeDesc;
	    private String categoryCodeDesc;
	    private String transferNum; // user inpu field
	    private String networkCode;
	    private String networkName;
	    private String reportHeaderName;

	    // popup
	    private ArrayList userList;
	    private String userName;
	    private int listSize;
	    private String userID;

	    // screen 2
	    private boolean ownerSame;
	    private String channelOwnerCategory;
	    private String channelOwnerCategoryDesc;
	    private String channelOwnerCategoryUserName;
	    private String channelCategoryUserName;
	    private String channelOwnerCategoryUserID;
	    private String channelCategoryUserID;

	    private String fromDate;
	    private String toDate;
	    private String statusCode;
	    private String statusDesc;
	    private ArrayList statusList;

	    // enquiry screen
	    private String selectedIndex;
	    // detail screen
	    private ArrayList transferList;

	    // view detail screen for enquiry
	    private ArrayList transferItemsList;

	    private String domainCode;
	    private String transferNumberDispaly;
	    private String distributorName;
	    private String domainName;
	    private String geographicDomainName;
	    private String primaryTxnNum;
	    private String categoryName;
	    private String gardeDesc;
	    private String erpCode;
	    private String commissionProfileName;
	    private String transferDate;
	    private String externalTxnExist;
	    private String externalTxnNum;
	    private String externalTxnDate;
	    private String refrenceNum;
	    private String address;
	    private String remarks;
	    private String paymentInstrumentName;
	    private String paymentInstrumentCode;
	    private String paymentInstNum;
	    private String paymentInstrumentDate;
	    private String paymentInstrumentAmt;
	    private String approve1Remark;
	    private String approve2Remark;
	    private String approve3Remark;
	    private String currentApprovalLevel;

	    private String selectedUserId;
	    private String payableAmount;
	    private String netPayableAmount;
	    private boolean channelUserLoginedFlag;
	    private int searchListSize;

	    private String totalMRP;
	    private String totalTax1;
	    private String totalTax2;
	    private String totalTax3;
	    private String totalComm;
	    private String totalReqQty;
	    private String totalStock;
	    private ArrayList transferTypeList;
	    private String transferTypeCode;
	    private String transferTypeValue;

	    // to store the information of transfer Category (SALE/TRANSFER).
	    private ArrayList transferCategoryList = null;
	    private String transferCategoryCode = null;
	    private String transferCategoryDesc = null;
	    private String transferProfileName;

	    private String geoDomainNameForUser = null;

	    // parameters for the enquiry by the user mobile number (user code)
	    private String userCode = null;
	    private String fromDateForUserCode = null;
	    private String toDateForUserCode = null;
	    private String statusCodeForUserCode = null;
	    private String trfCatForUserCode = null;

	    private String trfTypeForUserCode = null;
	    private String statusDetail = null;
	    private String sessionDomainCode = null;
	    // ends here

	    private String trfTypeDetail = null;
	    private long time = 0;
	    private String currentDateFlag = null;
	    private String currentDateFlagForUserCode = null;
	    // For Mali CR--- +ve Commision Apply
	    private String commissionQuantity = null;
	    private String receiverCreditQuantity = null;
	    private String senderDebitQuantity = null;

	    // for transfer quantity change while approval
	    private String firstLevelApprovedQuantity = null;
	    private String secondLevelApprovedQuantity = null;
	    private String thirdLevelApprovedQuantity = null;
	    private String validationCheck = null;
	    // Added By Babu Kunwar For showing post/pre balance in C2C Transfers
	    private String senderPostStock = null;
	    private String receiverPostStock = null;
	    private String senderPreviousStock = null;
	    private String receiverPreviousStock = null;

	    // Added by Amit Raheja for txn reversal
	    private String revTransferNum;

	    private boolean showPaymentDetails = false;
	    
	    private String transactionMode = "N";
	    private String sosSettlementDate;
	    private String sosStatus;
	    private String totalOtf;
	    
	    
	    private String dualCommissionType;
	    private String totalOthComm;
	    
	    
	    
	    public boolean isSosCheck() {
			return sosCheck;
		}

		public void setSosCheck(boolean sosCheck) {
			this.sosCheck = sosCheck;
		}

		private boolean sosCheck;
		
		private boolean sosCheck1;
	    public boolean isSosCheck1() {
			return sosCheck1;
		}

		public void setSosCheck1(boolean sosCheck1) {
			this.sosCheck1 = sosCheck1;
		}

		public boolean isStatusCheck() {
			return statusCheck;
		}

		public void setStatusCheck(boolean statusCheck) {
			this.statusCheck = statusCheck;
		}

		private boolean statusCheck;
	    public boolean isNetworkCodecheck() {
			return networkCodecheck;
		}

		public void setNetworkCodecheck(boolean networkCodecheck) {
			this.networkCodecheck = networkCodecheck;
		}

		private boolean networkCodecheck;
	    
	    
	    public String getTotalOtf() {
			return totalOtf;
		}

		public void setTotalOtf(String totalOtf) {
			this.totalOtf = totalOtf;
		}

		public String getSosSettlementDate() {
			return sosSettlementDate;
		}

		public void setSosSettlementDate(String sosSettlementDate) {
			this.sosSettlementDate = sosSettlementDate;
		}

		public String getSosStatus() {
			return sosStatus;
		}

		public void setSosStatus(String sosStatus) {
			this.sosStatus = sosStatus;
		}

		public String getTransactionMode() {
			return transactionMode;
		}

		public void setTransactionMode(String transactionMode) {
			this.transactionMode = transactionMode;
		}

		public boolean getShowPaymentDetails() {
	        return showPaymentDetails;
	    }

	    public void setShowPaymentDetails(boolean paymentDetails) {
	        this.showPaymentDetails = paymentDetails;
	    }

	    /**
	     * @return the validationCheck
	     */
	    public String getValidationCheck() {
	        return validationCheck;
	    }

	    /**
	     * @param validationCheck
	     *            the validationCheck to set
	     */
	    public void setValidationCheck(String validationCheck) {
	        this.validationCheck = validationCheck;
	    }

	    public int getProductTypesListSize() {
	        if (productsTypeList != null) {
	            return productsTypeList.size();
	        }
	        return 0;
	    }

	    public boolean getChannelUserLoginedFlag() {
	        return channelUserLoginedFlag;
	    }

	    public void setChannelUserLoginedFlag(boolean channelUserLoginedFlag) {
	        this.channelUserLoginedFlag = channelUserLoginedFlag;
	    }

	    public String getFromDate() {
	        return fromDate;
	    }

	    public void setFromDate(String fromDate) {
	        this.fromDate = fromDate;
	    }

	    public String getStatusCode() {
	        return statusCode;
	    }

	    public void setStatusCode(String statusCode) {
	        this.statusCode = statusCode;
	    }

	    public ArrayList getStatusList() {
	        return statusList;
	    }

	    public void setStatusList(ArrayList statusList) {
	        this.statusList = statusList;
	    }

	    public String getToDate() {
	        return toDate;
	    }

	    public void setToDate(String toDate) {
	        this.toDate = toDate;
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

	    public String getChannelCategoryUserID() {
	        return channelCategoryUserID;
	    }

	    public void setChannelCategoryUserID(String channelCategoryUserID) {
	        this.channelCategoryUserID = channelCategoryUserID;
	    }

	    public String getChannelCategoryUserName() {
	        return channelCategoryUserName;
	    }

	    public void setChannelCategoryUserName(String channelCategoryUserName) {
	        this.channelCategoryUserName = channelCategoryUserName;
	    }

	    public String getChannelDomain() {
	        return channelDomain;
	    }

	    public void setChannelDomain(String channelDomain) {
	        this.channelDomain = channelDomain;
	    }

	    public String getChannelDomainDesc() {
	        return channelDomainDesc;
	    }

	    public void setChannelDomainDesc(String channelDomainDesc) {
	        this.channelDomainDesc = channelDomainDesc;
	    }

	    public ArrayList getChannelDomainList() {
	        return channelDomainList;
	    }

	    public void setChannelDomainList(ArrayList channelDomainList) {
	        this.channelDomainList = channelDomainList;
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

	    public String getGeoDomainCode() {
	        return geoDomainCode;
	    }

	    public void setGeoDomainCode(String geoDomainCode) {
	        this.geoDomainCode = geoDomainCode;
	    }

	    public String getGeoDomainCodeDesc() {
	        return geoDomainCodeDesc;
	    }

	    public void setGeoDomainCodeDesc(String geoDomainCodeDesc) {
	        this.geoDomainCodeDesc = geoDomainCodeDesc;
	    }

	    public ArrayList getGeoDomainList() {
	        return geoDomainList;
	    }

	    public void setGeoDomainList(ArrayList geoDomainList) {
	        this.geoDomainList = geoDomainList;
	    }

	    public boolean isOwnerSame() {
	        return ownerSame;
	    }

	    public void setOwnerSame(boolean ownerSame) {
	        this.ownerSame = ownerSame;
	    }

	    public ArrayList getProductsTypeList() {
	        return productsTypeList;
	    }

	    public void setProductsTypeList(ArrayList productsTypeList) {
	        this.productsTypeList = productsTypeList;
	    }

	    public String getProductType() {
	        return productType;
	    }

	    public void setProductType(String productType) {
	        this.productType = productType;
	    }

	    public String getProductTypeDesc() {
	        return productTypeDesc;
	    }

	    public void setProductTypeDesc(String productTypeDesc) {
	        this.productTypeDesc = productTypeDesc;
	    }

	    public String getTransferNum() {
	        return transferNum;
	    }

	    public void setTransferNum(String transferNum) {
	       this.transferNum = transferNum;
	    }

	    public String getUserID() {
	        return userID;
	    }

	    public void setUserID(String userId) {
	        this.userID = userId;
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

	    public ArrayList getTransferList() {
	        return transferList;
	    }

	    public void setTransferList(ArrayList transferList) {
	        this.transferList = transferList;
	    }

	    public String getSelectedIndex() {
	        return selectedIndex;
	    }

	    public void setSelectedIndex(String selectedIndex) {
	        this.selectedIndex = selectedIndex;
	    }

	    public String getAddress() {
	        return address;
	    }

	    public void setAddress(String address) {
	        this.address = address;
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

	    public String getCategoryName() {
	        return categoryName;
	    }

	    public void setCategoryName(String categoryName) {
	        this.categoryName = categoryName;
	    }

	    public String getCommissionProfileName() {
	        return commissionProfileName;
	    }

	    public void setCommissionProfileName(String commissionProfileName) {
	        this.commissionProfileName = commissionProfileName;
	    }

	    public String getCurrentApprovalLevel() {
	        return currentApprovalLevel;
	    }

	    public void setCurrentApprovalLevel(String currentApprovalLevel) {
	        this.currentApprovalLevel = currentApprovalLevel;
	    }

	    public String getDistributorName() {
	        return distributorName;
	    }

	    public void setDistributorName(String distributorName) {
	        this.distributorName = distributorName;
	    }

	    public String getDomainCode() {
	        return domainCode;
	    }

	    public void setDomainCode(String domainCode) {
	        this.domainCode = domainCode;
	    }

	    public String getDomainName() {
	        return domainName;
	    }

	    public void setDomainName(String domainName) {
	        this.domainName = domainName;
	    }

	    public String getErpCode() {
	        return erpCode;
	    }

	    public void setErpCode(String erpCode) {
	        this.erpCode = erpCode;
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

	    public String getExternalTxnNum() {
	        return externalTxnNum;
	    }

	    public void setExternalTxnNum(String externalTxnNum) {
	        this.externalTxnNum = externalTxnNum;
	    }

	    public String getGardeDesc() {
	        return gardeDesc;
	    }

	    public void setGardeDesc(String gardeDesc) {
	        this.gardeDesc = gardeDesc;
	    }

	    public String getGeographicDomainName() {
	        return geographicDomainName;
	    }

	    public void setGeographicDomainName(String geographicDomainName) {
	        this.geographicDomainName = geographicDomainName;
	    }

	    public String getNetPayableAmount() {
	        return netPayableAmount;
	    }

	    public void setNetPayableAmount(String netPayableAmount) {
	        this.netPayableAmount = netPayableAmount;
	    }

	    public String getPayableAmount() {
	        return payableAmount;
	    }

	    public void setPayableAmount(String payableAmount) {
	        this.payableAmount = payableAmount;
	    }

	    public String getPaymentInstNum() {
	        return paymentInstNum;
	    }

	    public void setPaymentInstNum(String paymentInstNum) {
	        this.paymentInstNum = paymentInstNum;
	    }

	    public String getPaymentInstrumentAmt() {
	        return paymentInstrumentAmt;
	    }

	    public void setPaymentInstrumentAmt(String paymentInstrumentAmt) {
	        this.paymentInstrumentAmt = paymentInstrumentAmt;
	    }

	    public String getPaymentInstrumentCode() {
	        return paymentInstrumentCode;
	    }

	    public void setPaymentInstrumentCode(String paymentInstrumentCode) {
	        this.paymentInstrumentCode = paymentInstrumentCode;
	    }

	    public String getPaymentInstrumentDate() {
	        return paymentInstrumentDate;
	    }

	    public void setPaymentInstrumentDate(String paymentInstrumentDate) {
	        this.paymentInstrumentDate = paymentInstrumentDate;
	    }

	    public String getPaymentInstrumentName() {
	        return paymentInstrumentName;
	    }

	    public void setPaymentInstrumentName(String paymentInstrumentName) {
	        this.paymentInstrumentName = paymentInstrumentName;
	    }

	    public String getPrimaryTxnNum() {
	        return primaryTxnNum;
	    }

	    public void setPrimaryTxnNum(String primaryTxnNum) {
	        this.primaryTxnNum = primaryTxnNum;
	    }

	    public String getRefrenceNum() {
	        return refrenceNum;
	    }

	    public void setRefrenceNum(String refrenceNum) {
	        this.refrenceNum = refrenceNum;
	    }

	    public String getRemarks() {
	        return remarks;
	    }

	    public void setRemarks(String remarks) {
	        this.remarks = remarks;
	    }

	    public String getSelectedUserId() {
	        return selectedUserId;
	    }

	    public void setSelectedUserId(String selectedUserId) {
	        this.selectedUserId = selectedUserId;
	    }

	    public String getTransferDate() {
	        return transferDate;
	    }

	    public void setTransferDate(String transferDate) {
	        this.transferDate = transferDate;
	    }

	    public ArrayList getTransferItemsList() {
	        return transferItemsList;
	    }

	    public void setTransferItemsList(ArrayList transferItemsList) {
	        this.transferItemsList = transferItemsList;
	    }

	    public String getTransferNumberDispaly() {
	        return transferNumberDispaly;
	    }

	    public void setTransferNumberDispaly(String transferNumberDispaly) {
	        this.transferNumberDispaly = transferNumberDispaly;
	    }

	    public int getListSize() {
	        return listSize;
	    }

	    public void setListSize(int listSize) {
	        this.listSize = listSize;
	    }

	    public int getSearchListSize() {
	        return searchListSize;
	    }

	    public void setSearchListSize(int searchListSize) {
	        this.searchListSize = searchListSize;
	    }

	    public String getTotalComm() {
	        return totalComm;
	    }

	    public void setTotalComm(String totalComm) {
	        this.totalComm = totalComm;
	    }

	    public String getTotalMRP() {
	        return totalMRP;
	    }

	    public void setTotalMRP(String totalMRP) {
	        this.totalMRP = totalMRP;
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

	  

	    /**
	     * @return Returns the transferTypeCode.
	     */
	    public String getTransferTypeCode() {
	        return transferTypeCode;
	    }

	    /**
	     * @param transferTypeCode
	     *            The transferTypeCode to set.
	     */
	    public void setTransferTypeCode(String transferTypeCode) {
	        this.transferTypeCode = transferTypeCode;
	    }

	    /**
	     * @return Returns the transferTypeValue.
	     */
	    public String getTransferTypeValue() {
	        return transferTypeValue;
	    }

	    /**
	     * @param transferTypeValue
	     *            The transferTypeValue to set.
	     */
	    public void setTransferTypeValue(String transferTypeValue) {
	        this.transferTypeValue = transferTypeValue;
	    }

	    /**
	     * @return Returns the transferTypeList.
	     */
	    public ArrayList getTransferTypeList() {
	        return transferTypeList;
	    }

	    /**
	     * @param transferTypeList
	     *            The transferTypeList to set.
	     */
	    public void setTransferTypeList(ArrayList transferTypeList) {
	        this.transferTypeList = transferTypeList;
	    }

	    public String getTransferCategoryCode() {
	        return transferCategoryCode;
	    }

	    public void setTransferCategoryCode(String transferCategoryCode) {
	        this.transferCategoryCode = transferCategoryCode;
	    }

	    public String getTransferCategoryDesc() {
	        return transferCategoryDesc;
	    }

	    public void setTransferCategoryDesc(String transferCategoryDesc) {
	        this.transferCategoryDesc = transferCategoryDesc;
	    }

	    public ArrayList getTransferCategoryList() {
	        return transferCategoryList;
	    }

	    public void setTransferCategoryList(ArrayList transferCategoryList) {
	        this.transferCategoryList = transferCategoryList;
	    }

	    public String getTransferProfileName() {
	        return transferProfileName;
	    }

	    public void setTransferProfileName(String transferProfileName) {
	        this.transferProfileName = transferProfileName;
	    }

	    public String getStatusDesc() {
	        return statusDesc;
	    }

	    public void setStatusDesc(String statusDesc) {
	        this.statusDesc = statusDesc;
	    }

	    /**
	     * @return Returns the networkCode.
	     */
	    public String getNetworkCode() {
	        return networkCode;
	    }

	    /**
	     * @param networkCode
	     *            The networkCode to set.
	     */
	    public void setNetworkCode(String networkCode) {
	        this.networkCode = networkCode;
	    }

	    /**
	     * @return Returns the networkName.
	     */
	    public String getNetworkName() {
	        return networkName;
	    }

	    /**
	     * @param networkName
	     *            The networkName to set.
	     */
	    public void setNetworkName(String networkName) {
	        this.networkName = networkName;
	    }

	    /**
	     * @return Returns the reportHeaderName.
	     */
	    public String getReportHeaderName() {
	        return reportHeaderName;
	    }

	    /**
	     * @param reportHeaderName
	     *            The reportHeaderName to set.
	     */
	    public void setReportHeaderName(String reportHeaderName) {
	        this.reportHeaderName = reportHeaderName;
	    }

	    public String getGeoDomainNameForUser() {
	        return geoDomainNameForUser;
	    }

	    public void setGeoDomainNameForUser(String geoDomainNameForUser) {
	        this.geoDomainNameForUser = geoDomainNameForUser;
	    }

	    public String getFromDateForUserCode() {
	        return fromDateForUserCode;
	    }

	    public void setFromDateForUserCode(String fromDateForUserCode) {
	        this.fromDateForUserCode = fromDateForUserCode;
	    }

	    public String getStatusCodeForUserCode() {
	        return statusCodeForUserCode;
	    }

	    public void setStatusCodeForUserCode(String statusCodeForUserCode) {
	        this.statusCodeForUserCode = statusCodeForUserCode;
	    }

	    public String getToDateForUserCode() {
	        return toDateForUserCode;
	    }

	    public void setToDateForUserCode(String toDateForUserCode) {
	        this.toDateForUserCode = toDateForUserCode;
	    }

	    public String getTrfCatForUserCode() {
	        return trfCatForUserCode;
	    }

	    public void setTrfCatForUserCode(String trfCatForUserCode) {
	        this.trfCatForUserCode = trfCatForUserCode;
	    }

	    public String getUserCode() {
	        return userCode;
	    }

	    public void setUserCode(String userCode) {
	        this.userCode = userCode;
	    }

	    public String getStatusDetail() {
	        return statusDetail;
	    }

	    public void setStatusDetail(String statusDetail) {
	        this.statusDetail = statusDetail;
	    }

	    public String getTrfTypeForUserCode() {
	        return trfTypeForUserCode;
	    }

	    public void setTrfTypeForUserCode(String trfTypeForUserCode) {
	        this.trfTypeForUserCode = trfTypeForUserCode;
	    }

	    public String getTrfTypeDetail() {
	        return trfTypeDetail;
	    }

	    public void setTrfTypeDetail(String trfTypeDetail) {
	        this.trfTypeDetail = trfTypeDetail;
	    }

	    public String getSessionDomainCode() {
	        return sessionDomainCode;
	    }

	    public void setSessionDomainCode(String sessionDomainCode) {
	        this.sessionDomainCode = sessionDomainCode;
	    }

	    /**
	     * method flush()
	     * to flush all data from the from bean .
	     * void
	     */
	    public void flush() {
	        geoDomainList = null;
	        channelDomainList = null;
	        productsTypeList = null;
	        categoryList = null;
	        geoDomainCode = null;
	        channelDomain = null;
	        productType = null;
	        categoryCode = null;
	        geoDomainCodeDesc = null;
	        channelDomainDesc = null;
	        productTypeDesc = null;
	        categoryCodeDesc = null;
	        transferNum = null;
	        userList = null;
	        userName = null;
	        listSize = 0;
	        userID = null;
	        ownerSame = false;
	        channelOwnerCategory = null;
	        channelOwnerCategoryDesc = null;
	        channelOwnerCategoryUserName = null;
	        channelCategoryUserName = null;
	        channelOwnerCategoryUserID = null;
	        channelCategoryUserID = null;
	        fromDate = null;
	        toDate = null;
	        statusCode = null;
	        statusDesc = null;
	        statusList = null;
	        selectedIndex = null;
	        transferList = null;
	        transferItemsList = null;
	        domainCode = null;
	        transferNumberDispaly = null;
	        distributorName = null;
	        domainName = null;
	        geographicDomainName = null;
	        primaryTxnNum = null;
	        categoryName = null;
	        gardeDesc = null;
	        erpCode = null;
	        commissionProfileName = null;
	        transferDate = null;
	        externalTxnExist = null;
	        externalTxnNum = null;
	        externalTxnDate = null;
	        refrenceNum = null;
	        address = null;
	        remarks = null;
	        paymentInstrumentName = null;
	        paymentInstrumentCode = null;
	        paymentInstNum = null;
	        paymentInstrumentDate = null;
	        paymentInstrumentAmt = null;
	        approve1Remark = null;
	        approve2Remark = null;
	        approve3Remark = null;
	        currentApprovalLevel = null;
	        selectedUserId = null;
	        payableAmount = null;
	        netPayableAmount = null;
	        channelUserLoginedFlag = false;
	        searchListSize = 0;
	        totalMRP = null;
	        totalTax1 = null;
	        totalTax2 = null;
	        totalTax3 = null;
	        totalComm = null;
	        totalReqQty = null;
	        totalStock = null;
	        transferTypeList = null;
	        transferTypeCode = null;
	        transferTypeValue = null;
	        transferCategoryList = null;
	        transferCategoryCode = null;
	        transferCategoryDesc = null;
	        transferProfileName = null;
	        geoDomainNameForUser = null;
	        userCode = null;
	        fromDateForUserCode = null;
	        toDateForUserCode = null;
	        statusCodeForUserCode = null;
	        trfCatForUserCode = null;
	        statusDetail = null;
	        trfTypeForUserCode = null;
	        trfTypeDetail = null;
	        sessionDomainCode = null;
	        // For Mali CR--- +ve Commision Apply
	        commissionQuantity = null;
	        receiverCreditQuantity = null;
	        senderDebitQuantity = null;

	        firstLevelApprovedQuantity = null;
	        secondLevelApprovedQuantity = null;
	        thirdLevelApprovedQuantity = null;
	    }

	    /**
	     * method flushSearchUser()
	     * To flush the user selection data.
	     * void
	     */
	    public void flushSearchUser() {
	        ownerSame = false;
	        channelOwnerCategory = null;
	        channelOwnerCategoryDesc = null;
	        channelOwnerCategoryUserName = null;
	        channelCategoryUserName = null;
	        channelOwnerCategoryUserID = null;
	        channelCategoryUserID = null;
	        userList = null;
	        userName = null;
	        listSize = 0;
	        userID = null;

	    }

	    /**
	     * To flush the contents of the detailed screen.
	     * void
	     */
	    public void flushRecordContent() {
	        transferNumberDispaly = null;
	        userName = null;
	        domainName = null;
	        geographicDomainName = null;
	        primaryTxnNum = null;
	        gardeDesc = null;
	        erpCode = null;
	        productType = null;
	        productTypeDesc = null;
	        commissionProfileName = null;
	        externalTxnDate = null;
	        externalTxnNum = null;
	        refrenceNum = null;
	        remarks = null;
	        paymentInstrumentName = null;
	        paymentInstNum = null;
	        paymentInstrumentDate = null;
	        paymentInstrumentAmt = null;
	        transferDate = null;
	        payableAmount = null;
	        netPayableAmount = null;
	        approve1Remark = null;
	        approve2Remark = null;
	        approve3Remark = null;
	        address = null;
	        transferProfileName = null;
	        transferCategoryDesc = null;
	        geoDomainCodeDesc = null;
	        channelDomainDesc = null;
	        categoryCodeDesc = null;
	        currentDateFlag = null;
	        currentDateFlagForUserCode = null;
	        // For Mali CR--- +ve Commision Apply
	        commissionQuantity = null;
	        receiverCreditQuantity = null;
	        senderDebitQuantity = null;
	    }

	    /**
	     * @return Returns the time.
	     */
	    public long getTime() {
	        return time;
	    }

	    /**
	     * @param time
	     *            The time to set.
	     */
	    public void setTime(long time) {
	        this.time = time;
	    }

	    public String getCurrentDateFlag() {
	        return currentDateFlag;
	    }

	    public void setCurrentDateFlag(String currentDateFlag) {
	        this.currentDateFlag = currentDateFlag;
	    }

	    public String getCurrentDateFlagForUserCode() {
	        return currentDateFlagForUserCode;
	    }

	    public void setCurrentDateFlagForUserCode(String currentDateFlagForUserCode) {
	        this.currentDateFlagForUserCode = currentDateFlagForUserCode;
	    }


	    /**
	     * @return Returns the commisionQuantity.
	     */
	    public String getCommissionQuantity() {
	        return commissionQuantity;
	    }

	    /**
	     * @return Returns the receiverCreditQuantity.
	     */
	    public String getReceiverCreditQuantity() {
	        return receiverCreditQuantity;
	    }

	    /**
	     * @param commisionQuantity
	     *            The commisionQuantity to set.
	     */
	    public void setCommisionQuantity(String commissionQuantity) {
	        this.commissionQuantity = commissionQuantity;
	    }

	    /**
	     * @param receiverCreditQuantity
	     *            The receiverCreditQuantity to set.
	     */
	    public void setReceiverCreditQuantity(String receiverCreditQuantity) {
	        this.receiverCreditQuantity = receiverCreditQuantity;
	    }

	    /**
	     * @return Returns the senderDebitQuantity.
	     */
	    public String getSenderDebitQuantity() {
	        return senderDebitQuantity;
	    }

	    /**
	     * @param commissionQuantity
	     *            The commissionQuantity to set.
	     */
	    public void setCommissionQuantity(String commissionQuantity) {
	        this.commissionQuantity = commissionQuantity;
	    }

	    /**
	     * @param senderDebitQuantity
	     *            The senderDebitQuantity to set.
	     */
	    public void setSenderDebitQuantity(String senderDebitQuantity) {
	        this.senderDebitQuantity = senderDebitQuantity;
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

	    public String getReceiverPostStock() {
	        return receiverPostStock;
	    }

	    public void setReceiverPostStock(String receiverPostStock) {
	        this.receiverPostStock = receiverPostStock;
	    }

	    public String getReceiverPreviousStock() {
	        return receiverPreviousStock;
	    }

	    public void setReceiverPreviousStock(String receiverPreviousStock) {
	        this.receiverPreviousStock = receiverPreviousStock;
	    }

	    public String getSenderPostStock() {
	        return senderPostStock;
	    }

	    public void setSenderPostStock(String senderPostStock) {
	        this.senderPostStock = senderPostStock;
	    }

	    public String getSenderPreviousStock() {
	        return senderPreviousStock;
	    }

	    public void setSenderPreviousStock(String senderPreviousStock) {
	        this.senderPreviousStock = senderPreviousStock;
	    }

	    public String getRevTransferNum() {
	        return revTransferNum;
	    }

	    public void setRevTransferNum(String revTransferNum) {
	        this.revTransferNum = revTransferNum;
	    }

		public void setDualCommissionType(String dualCommissionType) {
			this.dualCommissionType = dualCommissionType;
			
		}
		
		public String getDualCommissionType() {
			return dualCommissionType;
			
		}

		public void setTotalOthComm(String totalOthComm) {
			this.totalOthComm = totalOthComm;
			
		}
		
		public String getTotalOthComm() {
			return totalOthComm;
			
		}

	

	
}
