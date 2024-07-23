package com.web.pretups.channel.transfer.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;


/**
 * @author pankaj.kumar
 *
 */
public class ChannelTransferAckModel implements Serializable {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	/**
	 * @(#)ChannelTransferAskModel.java
	                                     
	 *              pankaj kumar 
	
	 */
	        private List<?> geoDomainList;
		    private List<?> channelDomainList;
		    private List<?> productsTypeList;
		    private List<?> categoryList;
		    private long    requiredQuantity;
		    private String geoDomainCode;
		    private String channelDomain;
		    private String productType;
		    private String categoryCode;
		    private String geoDomainCodeDesc;
		    private String channelDomainDesc;
		    private String productTypeDesc;
		    private String categoryCodeDesc;
		    private String transferNum=null; 
		    private String networkCode;
		    private String networkName;
		    private String reportHeaderName;
		    private String  graphicalDomainCode;
		    private String  gegoraphyDomainName;
		    private String userType;
		    private String staffReport = "N";
		    
			private List<?> userList;
		    private String userName;
		    private int listSize;
		    private String userID;
		    private Date payInstrumentDate;
		    private int transferListSize;
		   
            private String externalCode;
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
		    private List<?> statusList;
	        private String status;
		    private String  transferInOrOut;
		    private String selectedIndex;
		    private long unitValue;
			private String transferDateAsString;
			
			
		  public String getTransferDateAsString() {
				return transferDateAsString;
			}


			public void setTransferDateAsString(String transferDateAsString) {
				this.transferDateAsString = transferDateAsString;
			}


		public long getUnitValue() {
				return unitValue;
			}


			public void setUnitValue(long unitValue) {
				this.unitValue = unitValue;
			}

		private List<ChannelTransferVO> transferList;
		
		 
		    public String getTransferInOrOut() {
				return transferInOrOut;
			}


			public void setTransferInOrOut(String transferInOrOut) {
				this.transferInOrOut = transferInOrOut;
			}
		
			private List<?> transferItemsList;
	
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
		    private List<?> transferTypeList;
		    private String transferTypeCode;
		    private String transferTypeValue;
		    private List<?> transferCategoryList = null;
		    private String transferCategoryCode = null;
		    private String transferCategoryDesc = null;
		    private String transferProfileName;
		    private String geoDomainNameForUser = null;
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
		    // For Mali CR--- ve Commision Apply
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
		    private boolean isReportingDB = false;
		    // Added by Amit Raheja for txn reversal
		    private String revTransferNum;
	
		    private boolean  showPaymentDetails = false;
		    
		    private String transactionMode = "N";
		    private String sosSettlementDate;
		    private String sosStatus;
		    private String totalOtf;
	        private String msisdn = null;
		    private String transferType = null;
	        private String productCode = null;
		    private String mrp = null;
	        private String productName = null;
	        private String tax1Value;
	        private String tax1Rate = null;
	        private String tax1Type = null;
	        private String tax2Value;
	        private String tax2Rate = null;
	        private String tax2Type = null;
	        private String commissionType;
		   	private String commissionRate;  
		   	private String commissionValue;
	        private Long   payableAmounts;
	        private Long   netPayableAmounts;
		    private String paymentInstType;
	        private String payInstrumentType = null;
		    private String payInstrumentNum = null;
	        private String payInstrumentAmt =null;
			private String transferMRP = null;
	        private String approvedQuantity = null;
	        private String transferCategory = null;
	        private String rptCode;
	        private String externalTranDate;
	        private String receiverCrQtyAsString;
	        private String otfTypePctOrAMt;
	    	private double otfRate;
	        private long otfAmount;
	        private String profileNames;
	        private Date externalTxnDates;
	        private Long receiverCrQty;
	        
	        
	        public Long getReceiverCrQty() {
				return receiverCrQty;
			}


			public void setReceiverCrQty(Long receiverCrQty) {
				this.receiverCrQty = receiverCrQty;
			}


			public Date getExternalTxnDates() {
				return externalTxnDates;
			}


			public void setExternalTxnDates(Date externalTxnDates) {
				externalTxnDates = externalTxnDates;
			}


			public String getProfileNames() {
				return profileNames;
			}


			public void setProfileNames(String profileNames) {
				this.profileNames = profileNames;
			}


			public String getOtfTypePctOrAMt() {
				return otfTypePctOrAMt;
			}


			public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
				this.otfTypePctOrAMt = otfTypePctOrAMt;
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


			public String getExternalCode() {
				return externalCode;
			}


			public void setExternalCode(String externalCode) {
				this.externalCode = externalCode;
			}


			public Date getPayInstrumentDate() {
				return payInstrumentDate;
			}


			public void setPayInstrumentDate(Date payInstrumentDate) {
				this.payInstrumentDate = payInstrumentDate;
			}


			public String getReceiverCrQtyAsString() {
				return receiverCrQtyAsString;
			}


			public void setReceiverCrQtyAsString(String receiverCrQtyAsString) {
				this.receiverCrQtyAsString = receiverCrQtyAsString;
			}


			public String getExternalTranDate() {
				return externalTranDate;
			}


			public void setExternalTranDate(String externalTranDate) {
				this.externalTranDate = externalTranDate;
			}


			public List<ChannelTransferVO> getTransferList() {
				return transferList;
			}


			public void setTransferList(List<ChannelTransferVO> transferList) {
				this.transferList = transferList;
			}



			public int getTransferListSize() {
				return transferListSize;
			}


			public void setTransferListSize(int transferListSize) {
				this.transferListSize = transferListSize;
			}
	        public String getStaffReport() {
				return staffReport;
			}


			public void setStaffReport(String staffReport) {
				this.staffReport = staffReport;
			}

		    
	        public String getRptCode() {
				return rptCode;
			}
	
	
			public void setRptCode(String rptCode) {
				this.rptCode = rptCode;
			}
	
	
			public String getTransferCategory() {
				return transferCategory;
			}
	
	
			public void setTransferCategory(String transferCategory) {
				this.transferCategory = transferCategory;
			}
	
	
			public String getApprovedQuantity() {
				return approvedQuantity;
			}
	
	
			public void setApprovedQuantity(String approvedQuantity) {
				this.approvedQuantity = approvedQuantity;
			}
	
	
			public String getTransferMRP() {
				return transferMRP;
			}
	
	
			public void setTransferMRP(String transferMRP) {
				this.transferMRP = transferMRP;
			}
	
	
			public String getStatus() {
				return status;
			}
	
	
			public void setStatus(String status) {
				this.status = status;
			}
	
	
			public long getRequiredQuantity() {
				return requiredQuantity;
			}
	
	
			public void setRequiredQuantity(long requiredQuantity) {
				this.requiredQuantity = requiredQuantity;
			}
	
	
			public String getUserType() {
				return userType;
			}
	
	
			public void setUserType(String userType) {
				this.userType = userType;
			}
	
	
			public String getPayInstrumentNum() {
				return payInstrumentNum;
			}
	
	
			public void setPayInstrumentNum(String payInstrumentNum) {
				this.payInstrumentNum = payInstrumentNum;
			}
	
	
			public String getPayInstrumentAmt() {
				return payInstrumentAmt;
			}
	
	
			public void setPayInstrumentAmt(String payInstrumentAmt) {
				this.payInstrumentAmt = payInstrumentAmt;
			}
	
	
			public boolean isReportingDB() {
				return isReportingDB;
			}
	
	
			public void setReportingDB(boolean isReportingDB) {
				this.isReportingDB = isReportingDB;
			}
	
	
		
	
			public String getPayInstrumentType() {
				return payInstrumentType;
			}
	
			
			
			public void setPayInstrumentType(String payInstrumentType) {
				this.payInstrumentType = payInstrumentType;
			}
	
			public String getPaymentInstType() {
				return paymentInstType;
			}
	
			public void setPaymentInstType(String paymentInstType) {
				this.paymentInstType = paymentInstType;
			}
	
			public Long getNetPayableAmounts() {
				return netPayableAmounts;
			}
	
			public void setNetPayableAmounts(Long netPayableAmounts) {
				this.netPayableAmounts = netPayableAmounts;
			}
	
			public Long getPayableAmounts() {
				return payableAmounts;
			}
	
			public void setPayableAmounts(Long payableAmounts) {
				this.payableAmounts = payableAmounts;
			}
	
			public String getCommissionType() {
				return commissionType;
			}
	
			public void setCommissionType(String commissionType) {
				this.commissionType = commissionType;
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
	
			public String getTax1Value() {
				return tax1Value;
			}
	
			public void setTax1Value(String tax1Value) {
				this.tax1Value = tax1Value;
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
	
			public String getTax2Value() {
				return tax2Value;
			}
	
			public void setTax2Value(String tax2Value) {
				this.tax2Value = tax2Value;
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
	
			public String getProductName() {
				return productName;
			}
	
			public void setProductName(String productName) {
				this.productName = productName;
			}
	
			public String getMrp() {
				return mrp;
			}
	
			public void setMrp(String mrp) {
				this.mrp = mrp;
			}
	
			public String getProductCode() {
				return productCode;
			}
	
			public void setProductCode(String productCode) {
				this.productCode = productCode;
			}
	
			public String getTransferType() {
				return transferType;
			}
	
			public void setTransferType(String transferType) {
				this.transferType = transferType;
			}
	
			public String getGraphicalDomainCode() {
				return graphicalDomainCode;
			}
	
			public void setGraphicalDomainCode(String graphicalDomainCode) {
				this.graphicalDomainCode = graphicalDomainCode;
			}
	
			public String getGegoraphyDomainName() {
				return gegoraphyDomainName;
			}
	
			public void setGegoraphyDomainName(String gegoraphyDomainName) {
				this.gegoraphyDomainName = gegoraphyDomainName;
			}
	
			public String getMsisdn() {
				return msisdn;
			}
	
			public void setMsisdn(String msisdn) {
				this.msisdn = msisdn;
			}
	
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
		        showPaymentDetails = paymentDetails;
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
	
		    public List getStatusList() {
		        return statusList;
		    }
	
		    public void setStatusList(List statusList) {
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
	
		    public List getCategoryList() {
		        return categoryList;
		    }
	
		    public void setCategoryList(List categoryList) {
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
	
		    public List getChannelDomainList() {
		        return channelDomainList;
		    }
	
		    public void setChannelDomainList(List channelDomainList) {
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
	
		    public List getGeoDomainList() {
		        return geoDomainList;
		    }
	
		    public void setGeoDomainList(List geoDomainList) {
		    	this.geoDomainList = geoDomainList;
		    }
	
		    public boolean isOwnerSame() {
		        return ownerSame;
		    }
	
		    public void setOwnerSame(boolean ownerSame) {
		    	this.ownerSame = ownerSame;
		    }
	
		    public List getProductsTypeList() {
		        return productsTypeList;
		    }
	
		    public void setProductsTypeList(List productsTypeList) {
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
		        return  transferNum;
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
	
		    public List getUserList() {
		        return userList;
		    }
	
		    public void setUserList(List userList) {
		    	this.userList = userList;
		    }
	
		    public String getUserName() {
		        return userName;
		    }
	
		    public void setUserName(String userName) {
		    	this.userName = userName;
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
	
		    public List getTransferItemsList() {
		        return transferItemsList;
		    }
	
		    public void setTransferItemsList(List transferItemsList) {
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
	
		    
		    public String getTransferTypeCode() {
		        return transferTypeCode;
		    }
	
		    public void setTransferTypeCode(String transferTypeCode) {
		    	this.transferTypeCode = transferTypeCode;
		    }
	
		   
		    public String getTransferTypeValue() {
		        return transferTypeValue;
		    }
	
		  
		    public void setTransferTypeValue(String transferTypeValue) {
		    	this.transferTypeValue = transferTypeValue;
		    }
	
		   
		    public List getTransferTypeList() {
		        return transferTypeList;
		    }
	
		   
		    public void setTransferTypeList(List transferTypeList) {
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
	
		    public List getTransferCategoryList() {
		        return transferCategoryList;
		    }
	
		    public void setTransferCategoryList(List transferCategoryList) {
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
	
		   
		    public String getReportHeaderName() {
		        return reportHeaderName;
		    }
	
		    
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
		        return this.trfCatForUserCode;
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
	
		  
		  
		    public long getTime() {
		        return time;
		    }
	
		   
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
	
		   
	
		    public String getCommissionQuantity() {
		        return commissionQuantity;
		    }
	
		  
		    public String getReceiverCreditQuantity() {
		        return receiverCreditQuantity;
		    }
	
		    
		    public void setCommisionQuantity(String commissionQuantity) {
		        this.commissionQuantity = commissionQuantity;
		    }
	
		    public void setReceiverCreditQuantity(String receiverCreditQuantity) {
		        this.receiverCreditQuantity = receiverCreditQuantity;
		    }
	
		 
		    public String getSenderDebitQuantity() {
		        return senderDebitQuantity;
		    }
	
		    public void setCommissionQuantity(String commissionQuantity) {
		    	this.commissionQuantity = commissionQuantity;
		    }
	
		   
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
	
		}
