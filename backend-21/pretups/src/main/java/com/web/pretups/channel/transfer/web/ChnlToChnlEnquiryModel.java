package com.web.pretups.channel.transfer.web;

import java.util.ArrayList;

import com.btsl.common.ListValueVO;

public class ChnlToChnlEnquiryModel {

	


	/**
	 * @# ChnlToChnlEnquiryForm.java
	 * 
	 *    Created by Created on History
	 *    --------------------------------------------------------------------------
	 *    ------
	 *    Ankit Zindal Oct 7, 2005 Initial creation
	 *    Sandeep Goel Nov 10,2005 Modification and customization
	 *    Sandeep Goel May 23,2006 Modification and customization
	 *    --------------------------------------------------------------------------
	 *    ------
	 *    Copyright(c) 2005 Bharti Telesoft Ltd.
	 */
	
	    private String fromDate;
	    private String toDate;
	    private ArrayList<ListValueVO> transferTypeList;
	    private String transferTypeCode;
	    private String transferTypeValue;
	    private String fromUserCode;
	    private String tmpFromUserCode;
	    private String toUserCode;
	    private String tmpToUserCode;
	    private String transferNum;
	    private ArrayList transferList;
	    private String selectedIndex;
	    private String totalMRP;
	    private String totalTax1;
	    private String totalTax2;
	    private String totalTax3;
	    private String totalComm;
	    private String totalReqQty;
	    private String totalStock;
	    private ArrayList transferItemsList;
	    private String transferNumber;
	    private String networkCode;
	    private String networkCodeFor;
	    private String grphDomainCode;
	    private String domainCode;
	    private String senderGradeCode;
	    private String receiverGradeCode;
	    private String fromUserID;
	    private String toUserID;
	    private String transferDate;
	    private String referenceNo;
	    private String requestedQty;
	    private String channelUserRamarks;
	    private String type;
	    private String payableAmt;
	    private String netPayableAmt;
	    private String pmtInstType;
	    private String pmtInstNo;
	    private String pmtInstDate;
	    private String pmtInstAmt;
	    private String tax1;
	    private String tax2;
	    private String tax3;
	    private String commission;
	    private String productType;
	    private String fromUserName;
	    private String toUserName;
	    private String transferCategoryCodeDesc;
	    private String transferSubType;
	    private String sourceTypeDesc;
	    private String controlledTxn;

	    private String receiverGgraphicalDomainCode; // for c2c enquiry
	    private String receiverDomainCode;// for c2c enquiry
	    private String senderCatName;// for c2c enquiry
	    private String receiverCategoryDesc;// for c2c enquiry
	    private String currentDateFlag = null;
	    // For Mali CR--- +ve Commision Apply
	    private String commissionQuantity = null;
	    private String receiverCreditQuantity = null;
	    private String senderDebitQuantity = null;

	    // ADDED BY Vikram
	    private boolean isStaffEnquiry = false;
	    private String loginId;
	    // added for validating popup screens
	    private long time;
	    private ArrayList userList = null;
	    private String userId = null;
	    private String activeUserName;

	    private String zoneCode;
	    private String parentCategoryCode;
	    private String parentUserID;
	    private ArrayList domainList = null;
	    private ArrayList parentCategoryList = null;
	    private ArrayList zoneList = null;
	    private String parentUserName;
	    private ArrayList parentUserList = null;
	    private String loginUserID = null;
	    private String categorySeqNo;
	    private String userType;
	    private String loggedInUserCategoryCode;
	    private String loggedInUserCategoryName;
	    private String loggedInUserName;
	    private String domainName;
	    private String zoneName;
	    private String userName;
	    private String activeUserId;
	    // Added By Babu Kunwar For showing post/pre balance in C2C Transfers
	    private String senderPostStock = null;
	    

		private String receiverPostStock = null;
	    private String senderPreviousStock = null;
	    private String receiverPreviousStock = null;
	    // added by gaurav for cell id and switch id
	    // for enquiry
	    private String cellId;
	    private String switchId;
	    private String sosSettlementDate;
	    private String totOtf;
	    private long otfAmount;
	    private double otfRate;
	    private String otfType;
	    // added by Himanshu 
	    private String jspPath;
	    private boolean networkCodeCheck;
	    
	    
	    public String getTotOtf() {
			return totOtf;
		}

		public void setTotOtf(String totOtf) {
			this.totOtf = totOtf;
		}

		public long getOtfAmount() {
			return otfAmount;
		}

		public void setOtfAmount(long otfAmount) {
			this.otfAmount = otfAmount;
		}

		public double getOtfRate() {
			return otfRate;
		}

		public void setOtfRate(double otfRate) {
			this.otfRate = otfRate;
		}

		public String getOtfType() {
			return otfType;
		}

		public void setOtfType(String otfType) {
			this.otfType = otfType;
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

		private String sosStatus;

	    /**
	     * @return Returns the fromUserName.
	     */
	    public String getFromUserName() {
	        return fromUserName;
	    }

	    /**
	     * @param fromUserName
	     *            The fromUserName to set.
	     */
	    public void setFromUserName(String fromUserName) {
	        this.fromUserName = fromUserName;
	    }

	    /**
	     * @return Returns the toUserName.
	     */
	    public String getToUserName() {
	        return toUserName;
	    }

	    /**
	     * @param toUserName
	     *            The toUserName to set.
	     */
	    public void setToUserName(String toUserName) {
	        this.toUserName = toUserName;
	    }

	    /**
	     * @return Returns the channelUserRamarks.
	     */
	    public String getChannelUserRamarks() {
	        return channelUserRamarks;
	    }

	    /**
	     * @param channelUserRamarks
	     *            The channelUserRamarks to set.
	     */
	    public void setChannelUserRamarks(String channelUserRamarks) {
	        this.channelUserRamarks = channelUserRamarks;
	    }

	    /**
	     * @return Returns the commission.
	     */
	    public String getCommission() {
	        return commission;
	    }

	    /**
	     * @param commission
	     *            The commission to set.
	     */
	    public void setCommission(String commission) {
	        this.commission = commission;
	    }

	    /**
	     * @return Returns the domainCode.
	     */
	    public String getDomainCode() {
	        return domainCode;
	    }

	    /**
	     * @param domainCode
	     *            The domainCode to set.
	     */
	    public void setDomainCode(String domainCode) {
	        this.domainCode = domainCode;
	    }

	    /**
	     * @return Returns the fromUserID.
	     */
	    public String getFromUserID() {
	        return fromUserID;
	    }

	    /**
	     * @param fromUserID
	     *            The fromUserID to set.
	     */
	    public void setFromUserID(String fromUserID) {
	        this.fromUserID = fromUserID;
	    }

	    /**
	     * @return Returns the grphDomainCode.
	     */
	    public String getGrphDomainCode() {
	        return grphDomainCode;
	    }

	    /**
	     * @param grphDomainCode
	     *            The grphDomainCode to set.
	     */
	    public void setGrphDomainCode(String grphDomainCode) {
	        this.grphDomainCode = grphDomainCode;
	    }

	    /**
	     * @return Returns the netPayableAmt.
	     */
	    public String getNetPayableAmt() {
	        return netPayableAmt;
	    }

	    /**
	     * @param netPayableAmt
	     *            The netPayableAmt to set.
	     */
	    public void setNetPayableAmt(String netPayableAmt) {
	        this.netPayableAmt = netPayableAmt;
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
	     * @return Returns the networkCodeFor.
	     */
	    public String getNetworkCodeFor() {
	        return networkCodeFor;
	    }

	    /**
	     * @param networkCodeFor
	     *            The networkCodeFor to set.
	     */
	    public void setNetworkCodeFor(String networkCodeFor) {
	        this.networkCodeFor = networkCodeFor;
	    }

	    /**
	     * @return Returns the payableAmt.
	     */
	    public String getPayableAmt() {
	        return payableAmt;
	    }

	    /**
	     * @param payableAmt
	     *            The payableAmt to set.
	     */
	    public void setPayableAmt(String payableAmt) {
	        this.payableAmt = payableAmt;
	    }

	    /**
	     * @return Returns the pmtInstAmt.
	     */
	    public String getPmtInstAmt() {
	        return pmtInstAmt;
	    }

	    /**
	     * @param pmtInstAmt
	     *            The pmtInstAmt to set.
	     */
	    public void setPmtInstAmt(String pmtInstAmt) {
	        this.pmtInstAmt = pmtInstAmt;
	    }

	    /**
	     * @return Returns the pmtInstDate.
	     */
	    public String getPmtInstDate() {
	        return pmtInstDate;
	    }

	    /**
	     * @param pmtInstDate
	     *            The pmtInstDate to set.
	     */
	    public void setPmtInstDate(String pmtInstDate) {
	        this.pmtInstDate = pmtInstDate;
	    }

	    /**
	     * @return Returns the pmtInstNo.
	     */
	    public String getPmtInstNo() {
	        return pmtInstNo;
	    }

	    /**
	     * @param pmtInstNo
	     *            The pmtInstNo to set.
	     */
	    public void setPmtInstNo(String pmtInstNo) {
	        this.pmtInstNo = pmtInstNo;
	    }

	    /**
	     * @return Returns the pmtInstType.
	     */
	    public String getPmtInstType() {
	        return pmtInstType;
	    }

	    /**
	     * @param pmtInstType
	     *            The pmtInstType to set.
	     */
	    public void setPmtInstType(String pmtInstType) {
	        this.pmtInstType = pmtInstType;
	    }

	    /**
	     * @return Returns the productType.
	     */
	    public String getProductType() {
	        return productType;
	    }

	    /**
	     * @param productType
	     *            The productType to set.
	     */
	    public void setProductType(String productType) {
	        this.productType = productType;
	    }

	    /**
	     * @return Returns the receiverGradeCode.
	     */
	    public String getReceiverGradeCode() {
	        return receiverGradeCode;
	    }

	    /**
	     * @param receiverGradeCode
	     *            The receiverGradeCode to set.
	     */
	    public void setReceiverGradeCode(String receiverGradeCode) {
	        this.receiverGradeCode = receiverGradeCode;
	    }

	    /**
	     * @return Returns the referenceNo.
	     */
	    public String getReferenceNo() {
	        return referenceNo;
	    }

	    /**
	     * @param referenceNo
	     *            The referenceNo to set.
	     */
	    public void setReferenceNo(String referenceNo) {
	        this.referenceNo = referenceNo;
	    }

	    /**
	     * @return Returns the requestedQty.
	     */
	    public String getRequestedQty() {
	        return requestedQty;
	    }

	    /**
	     * @param requestedQty
	     *            The requestedQty to set.
	     */
	    public void setRequestedQty(String requestedQty) {
	        this.requestedQty = requestedQty;
	    }

	    /**
	     * @return Returns the senderGradeCode.
	     */
	    public String getSenderGradeCode() {
	        return senderGradeCode;
	    }

	    /**
	     * @param senderGradeCode
	     *            The senderGradeCode to set.
	     */
	    public void setSenderGradeCode(String senderGradeCode) {
	        this.senderGradeCode = senderGradeCode;
	    }

	    /**
	     * @return Returns the tax1.
	     */
	    public String getTax1() {
	        return tax1;
	    }

	    /**
	     * @param tax1
	     *            The tax1 to set.
	     */
	    public void setTax1(String tax1) {
	        this.tax1 = tax1;
	    }

	    /**
	     * @return Returns the tax2.
	     */
	    public String getTax2() {
	        return tax2;
	    }

	    /**
	     * @param tax2
	     *            The tax2 to set.
	     */
	    public void setTax2(String tax2) {
	        this.tax2 = tax2;
	    }

	    /**
	     * @return Returns the tax3.
	     */
	    public String getTax3() {
	        return tax3;
	    }

	    /**
	     * @param tax3
	     *            The tax3 to set.
	     */
	    public void setTax3(String tax3) {
	        this.tax3 = tax3;
	    }

	    /**
	     * @return Returns the toUserID.
	     */
	    public String getToUserID() {
	        return toUserID;
	    }

	    /**
	     * @param toUserID
	     *            The toUserID to set.
	     */
	    public void setToUserID(String toUserID) {
	        this.toUserID = toUserID;
	    }

	    /**
	     * @return Returns the transferDate.
	     */
	    public String getTransferDate() {
	        return transferDate;
	    }

	    /**
	     * @param transferDate
	     *            The transferDate to set.
	     */
	    public void setTransferDate(String transferDate) {
	        this.transferDate = transferDate;
	    }

	    /**
	     * @return Returns the transferNumber.
	     */
	    public String getTransferNumber() {
	        return transferNumber;
	    }

	    /**
	     * @param transferNumber
	     *            The transferNumber to set.
	     */
	    public void setTransferNumber(String transferNumber) {
	        this.transferNumber = transferNumber;
	    }

	    /**
	     * @return Returns the type.
	     */
	    public String getType() {
	        return type;
	    }

	    /**
	     * @param type
	     *            The type to set.
	     */
	    public void setType(String type) {
	        this.type = type;
	    }

	    /**
	     * @return Returns the transferItemsList.
	     */
	    public ArrayList getTransferItemsList() {
	        return transferItemsList;
	    }

	    /**
	     * @param transferItemsList
	     *            The transferItemsList to set.
	     */
	    public void setTransferItemsList(ArrayList transferItemsList) {
	        this.transferItemsList = transferItemsList;
	    }

	    /**
	     * @return Returns the totalComm.
	     */
	    public String getTotalComm() {
	        return totalComm;
	    }

	    /**
	     * @param totalComm
	     *            The totalComm to set.
	     */
	    public void setTotalComm(String totalComm) {
	        this.totalComm = totalComm;
	    }

	    /**
	     * @return Returns the totalMRP.
	     */
	    public String getTotalMRP() {
	        return totalMRP;
	    }

	    /**
	     * @param totalMRP
	     *            The totalMRP to set.
	     */
	    public void setTotalMRP(String totalMRP) {
	        this.totalMRP = totalMRP;
	    }

	    /**
	     * @return Returns the totalReqQty.
	     */
	    public String getTotalReqQty() {
	        return totalReqQty;
	    }

	    /**
	     * @param totalReqQty
	     *            The totalReqQty to set.
	     */
	    public void setTotalReqQty(String totalReqQty) {
	        this.totalReqQty = totalReqQty;
	    }

	    /**
	     * @return Returns the totalStock.
	     */
	    public String getTotalStock() {
	        return totalStock;
	    }

	    /**
	     * @param totalStock
	     *            The totalStock to set.
	     */
	    public void setTotalStock(String totalStock) {
	        this.totalStock = totalStock;
	    }

	    /**
	     * @return Returns the totalTax1.
	     */
	    public String getTotalTax1() {
	        return totalTax1;
	    }

	    /**
	     * @param totalTax1
	     *            The totalTax1 to set.
	     */
	    public void setTotalTax1(String totalTax1) {
	        this.totalTax1 = totalTax1;
	    }

	    /**
	     * @return Returns the totalTax2.
	     */
	    public String getTotalTax2() {
	        return totalTax2;
	    }

	    /**
	     * @param totalTax2
	     *            The totalTax2 to set.
	     */
	    public void setTotalTax2(String totalTax2) {
	        this.totalTax2 = totalTax2;
	    }

	    /**
	     * @return Returns the totalTax3.
	     */
	    public String getTotalTax3() {
	        return totalTax3;
	    }

	    /**
	     * @param totalTax3
	     *            The totalTax3 to set.
	     */
	    public void setTotalTax3(String totalTax3) {
	        this.totalTax3 = totalTax3;
	    }

	    public int getTransferListSize() {
	        if (transferList != null) {
	            return transferList.size();
	        }
	        return 0;
	    }

	    /**
	     * @return Returns the transferList.
	     */
	    public ArrayList getTransferList() {
	        return transferList;
	    }

	    /**
	     * @param transferList
	     *            The transferList to set.
	     */
	    public void setTransferList(ArrayList transferList) {
	        this.transferList = transferList;
	    }

	    /**
	     * @return Returns the transferNum.
	     */
	    public String getTransferNum() {
	        return transferNum;
	    }

	    /**
	     * @param transferNum
	     *            The transferNum to set.
	     */
	    public void setTransferNum(String transferNum) {
	        this.transferNum = transferNum;
	    }

	    /**
	     * @return Returns the fromDate.
	     */
	    public String getFromDate() {
	        return fromDate;
	    }

	    /**
	     * @param fromDate
	     *            The fromDate to set.
	     */
	    public void setFromDate(String fromDate) {
	        this.fromDate = fromDate;
	    }

	    /**
	     * @return Returns the fromUserCode.
	     */
	    public String getFromUserCode() {
	        return fromUserCode;
	    }

	    /**
	     * @param fromUserCode
	     *            The fromUserCode to set.
	     */
	    public void setFromUserCode(String fromUserCode) {
	        this.fromUserCode = fromUserCode;
	    }

	    /**
	     * @return Returns the toDate.
	     */
	    public String getToDate() {
	        return toDate;
	    }

	    /**
	     * @param toDate
	     *            The toDate to set.
	     */
	    public void setToDate(String toDate) {
	        this.toDate = toDate;
	    }

	    /**
	     * @return Returns the toUserCode.
	     */
	    public String getToUserCode() {
	        return toUserCode;
	    }

	    /**
	     * @param toUserCode
	     *            The toUserCode to set.
	     */
	    public void setToUserCode(String toUserCode) {
	        this.toUserCode = toUserCode;
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
	     * @return Returns the selectedIndex.
	     */
	    public String getSelectedIndex() {
	        return selectedIndex;
	    }

	    /**
	     * @param selectedIndex
	     *            The selectedIndex to set.
	     */
	    public void setSelectedIndex(String selectedIndex) {
	        this.selectedIndex = selectedIndex;
	    }

	    public String getTransferCategoryCodeDesc() {
	        return transferCategoryCodeDesc;
	    }

	    public void setTransferCategoryCodeDesc(String transferCategoryCodeDesc) {
	        this.transferCategoryCodeDesc = transferCategoryCodeDesc;
	    }

	    public String getTransferSubType() {
	        return transferSubType;
	    }

	    public void setTransferSubType(String transferSubType) {
	        this.transferSubType = transferSubType;
	    }

	    public String getTmpToUserCode() {
	        return tmpToUserCode;
	    }

	    public void setTmpToUserCode(String tmpToUserCode) {
	        this.tmpToUserCode = tmpToUserCode;
	    }

	    public String getSourceTypeDesc() {
	        return sourceTypeDesc;
	    }

	    public void setSourceTypeDesc(String sourceTypeDesc) {
	        this.sourceTypeDesc = sourceTypeDesc;
	    }

	    public String getTmpFromUserCode() {
	        return tmpFromUserCode;
	    }

	    public void setTmpFromUserCode(String tmpFromUserCode) {
	        this.tmpFromUserCode = tmpFromUserCode;
	    }

	    public String getControlledTxn() {
	        return controlledTxn;
	    }

	    public void setControlledTxn(String controlledTxn) {
	        this.controlledTxn = controlledTxn;
	    }

	    public String getReceiverDomainCode() {
	        return receiverDomainCode;
	    }

	    public void setReceiverDomainCode(String receiverDomainCode) {
	        this.receiverDomainCode = receiverDomainCode;
	    }

	    public String getReceiverGgraphicalDomainCode() {
	        return receiverGgraphicalDomainCode;
	    }

	    public void setReceiverGgraphicalDomainCode(String receiverGgraphicalDomainCode) {
	        this.receiverGgraphicalDomainCode = receiverGgraphicalDomainCode;
	    }

	    public String getSenderCatName() {
	        return senderCatName;
	    }

	    public void setSenderCatName(String senderCatName) {
	        this.senderCatName = senderCatName;
	    }

	    public String getReceiverCategoryDesc() {
	        return receiverCategoryDesc;
	    }

	    public void setReceiverCategoryDesc(String receiverCategoryDesc) {
	        this.receiverCategoryDesc = receiverCategoryDesc;
	    }

	    public void flush() {
	        fromDate = null;
	        toDate = null;
	        transferTypeList = null;
	        transferTypeCode = null;
	        transferTypeValue = null;
	        fromUserCode = null;
	        tmpFromUserCode = null;
	        toUserCode = null;
	        transferNum = null;
	        transferList = null;
	        selectedIndex = null;
	        totalMRP = null;
	        totalTax1 = null;
	        totalTax2 = null;
	        totalTax3 = null;
	        totalComm = null;
	        totalReqQty = null;
	        totalStock = null;
	        transferItemsList = null;
	        transferNumber = null;
	        networkCode = null;
	        networkCodeFor = null;
	        grphDomainCode = null;
	        domainCode = null;
	        senderGradeCode = null;
	        receiverGradeCode = null;
	        fromUserID = null;
	        toUserID = null;
	        transferDate = null;
	        referenceNo = null;
	        requestedQty = null;
	        channelUserRamarks = null;
	        type = null;
	        payableAmt = null;
	        netPayableAmt = null;
	        pmtInstType = null;
	        pmtInstNo = null;
	        pmtInstDate = null;
	        pmtInstAmt = null;
	        tax1 = null;
	        tax2 = null;
	        tax3 = null;
	        commission = null;
	        productType = null;
	        fromUserName = null;
	        toUserName = null;
	        transferCategoryCodeDesc = null;
	        transferSubType = null;
	        tmpToUserCode = null;
	        sourceTypeDesc = null;
	        controlledTxn = null;
	        receiverGgraphicalDomainCode = null; // for c2c enquiry
	        receiverDomainCode = null;// for c2c enquiry
	        senderCatName = null;// for c2c enquiry
	        receiverCategoryDesc = null;
	        currentDateFlag = null;
	        // For Mali CR--- +ve Commision Apply
	        commissionQuantity = null;
	        receiverCreditQuantity = null;
	        senderDebitQuantity = null;
	        // added by vikram
	        loginId = null;
	        userId = null;
	        activeUserName = null;
	    }

	    public String getCurrentDateFlag() {
	        return currentDateFlag;
	    }

	    public void setCurrentDateFlag(String dateFlag) {
	        currentDateFlag = dateFlag;
	    }

	   
	    /**
	     * @return Returns the commissionQuantity.
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
	     * @param receiverCreditQuantity
	     *            The receiverCreditQuantity to set.
	     */
	    public void setReceiverCreditQuantity(String receiverCreditQuantity) {
	        this.receiverCreditQuantity = receiverCreditQuantity;
	    }

	    /**
	     * @param senderDebitQuantity
	     *            The senderDebitQuantity to set.
	     */
	    public void setSenderDebitQuantity(String senderDebitQuantity) {
	        this.senderDebitQuantity = senderDebitQuantity;
	    }

	    // ADDED BY Vikram

	    
	    /**
	     * @return the isStaffEnquiry
	     */
	    public boolean isStaffEnquiry() {
	        return isStaffEnquiry;
	    }

	    /**
	     * @param isStaffEnquiry
	     *            the isStaffEnquiry to set
	     */
	    public void setStaffEnquiry(boolean isStaffEnquiry) {
	        this.isStaffEnquiry = isStaffEnquiry;
	    }

	    /**
	     * @return the time
	     */
	    public long getTime() {
	        return time;
	    }

	    /**
	     * @param time
	     *            the time to set
	     */
	    public void setTime(long time) {
	        this.time = time;
	    }

	    /**
	     * @return the userList
	     */
	    public ArrayList getUserList() {
	        return userList;
	    }

	    /**
	     * @param userList
	     *            the userList to set
	     */
	    public void setUserList(ArrayList userList) {
	        this.userList = userList;
	    }

	    public int getResultCount() {
	        if (userList != null && !userList.isEmpty()) {
	            return userList.size();
	        } else {
	            return 0;
	        }
	    }

	    /**
	     * @return the userId
	     */
	    public String getUserId() {
	        return userId;
	    }

	    /**
	     * @param userId
	     *            the userId to set
	     */
	    public void setUserId(String userId) {
	        this.userId = userId;
	    }

	    /**
	     * @return the activeUserName
	     */
	    public String getActiveUserName() {
	        return activeUserName;
	    }

	    /**
	     * @param activeUserName
	     *            the activeUserName to set
	     */
	    public void setActiveUserName(String activeUserName) {
	        this.activeUserName = activeUserName;
	    }

	    /**
	     * @return the loginId
	     */
	    public String getLoginId() {
	        return loginId;
	    }

	    /**
	     * @param loginId
	     *            the loginId to set
	     */
	    public void setLoginId(String loginId) {
	        this.loginId = loginId;
	    }

	    /**
	     * @return Returns the categorySeqNo.
	     */
	    public String getCategorySeqNo() {
	        return categorySeqNo;
	    }

	    /**
	     * @param categorySeqNo
	     *            The categorySeqNo to set.
	     */
	    public void setCategorySeqNo(String categorySeqNo) {
	        this.categorySeqNo = categorySeqNo;
	    }

	    /**
	     * @return Returns the domainList.
	     */
	    public ArrayList getDomainList() {
	        return domainList;
	    }

	    /**
	     * @param domainList
	     *            The domainList to set.
	     */
	    public void setDomainList(ArrayList domainList) {
	        this.domainList = domainList;
	    }

	    /**
	     * @return Returns the domainName.
	     */
	    public String getDomainName() {
	        return domainName;
	    }

	    /**
	     * @param domainName
	     *            The domainName to set.
	     */
	    public void setDomainName(String domainName) {
	        this.domainName = domainName;
	    }

	    /**
	     * @return Returns the loggedInUserCategoryCode.
	     */
	    public String getLoggedInUserCategoryCode() {
	        return loggedInUserCategoryCode;
	    }

	    /**
	     * @param loggedInUserCategoryCode
	     *            The loggedInUserCategoryCode to set.
	     */
	    public void setLoggedInUserCategoryCode(String loggedInUserCategoryCode) {
	        this.loggedInUserCategoryCode = loggedInUserCategoryCode;
	    }

	    /**
	     * @return Returns the loggedInUserCategoryName.
	     */
	    public String getLoggedInUserCategoryName() {
	        return loggedInUserCategoryName;
	    }

	    /**
	     * @param loggedInUserCategoryName
	     *            The loggedInUserCategoryName to set.
	     */
	    public void setLoggedInUserCategoryName(String loggedInUserCategoryName) {
	        this.loggedInUserCategoryName = loggedInUserCategoryName;
	    }

	    /**
	     * @return Returns the loggedInUserName.
	     */
	    public String getLoggedInUserName() {
	        return loggedInUserName;
	    }

	    /**
	     * @param loggedInUserName
	     *            The loggedInUserName to set.
	     */
	    public void setLoggedInUserName(String loggedInUserName) {
	        this.loggedInUserName = loggedInUserName;
	    }

	    /**
	     * @return Returns the loginUserID.
	     */
	    public String getLoginUserID() {
	        return loginUserID;
	    }

	    /**
	     * @param loginUserID
	     *            The loginUserID to set.
	     */
	    public void setLoginUserID(String loginUserID) {
	        this.loginUserID = loginUserID;
	    }

	    /**
	     * @return Returns the parentCategoryCode.
	     */
	    public String getParentCategoryCode() {
	        return parentCategoryCode;
	    }

	    /**
	     * @param parentCategoryCode
	     *            The parentCategoryCode to set.
	     */
	    public void setParentCategoryCode(String parentCategoryCode) {
	        this.parentCategoryCode = parentCategoryCode;
	    }

	    /**
	     * @return Returns the parentCategoryList.
	     */
	    public ArrayList getParentCategoryList() {
	        return parentCategoryList;
	    }

	    /**
	     * @param parentCategoryList
	     *            The parentCategoryList to set.
	     */
	    public void setParentCategoryList(ArrayList parentCategoryList) {
	        this.parentCategoryList = parentCategoryList;
	    }

	    /**
	     * @return Returns the parentUserID.
	     */
	    public String getParentUserID() {
	        return parentUserID;
	    }

	    /**
	     * @param parentUserID
	     *            The parentUserID to set.
	     */
	    public void setParentUserID(String parentUserID) {
	        this.parentUserID = parentUserID;
	    }

	    /**
	     * @return Returns the parentUserList.
	     */
	    public ArrayList getParentUserList() {
	        return parentUserList;
	    }

	    /**
	     * @param parentUserList
	     *            The parentUserList to set.
	     */
	    public void setParentUserList(ArrayList parentUserList) {
	        this.parentUserList = parentUserList;
	    }

	    /**
	     * @return Returns the parentUserName.
	     */
	    public String getParentUserName() {
	        return parentUserName;
	    }

	    /**
	     * @param parentUserName
	     *            The parentUserName to set.
	     */
	    public void setParentUserName(String parentUserName) {
	        this.parentUserName = parentUserName;
	    }

	    /**
	     * @return Returns the userName.
	     */
	    public String getUserName() {
	        return userName;
	    }

	    /**
	     * @param userName
	     *            The userName to set.
	     */
	    public void setUserName(String userName) {
	        this.userName = userName;
	    }

	    /**
	     * @return Returns the userType.
	     */
	    public String getUserType() {
	        return userType;
	    }

	    /**
	     * @param userType
	     *            The userType to set.
	     */
	    public void setUserType(String userType) {
	        this.userType = userType;
	    }

	    /**
	     * @return Returns the zoneCode.
	     */
	    public String getZoneCode() {
	        return zoneCode;
	    }

	    /**
	     * @param zoneCode
	     *            The zoneCode to set.
	     */
	    public void setZoneCode(String zoneCode) {
	        this.zoneCode = zoneCode;
	    }

	    /**
	     * @return Returns the zoneList.
	     */
	    public ArrayList getZoneList() {
	        return zoneList;
	    }

	    /**
	     * @param zoneList
	     *            The zoneList to set.
	     */
	    public void setZoneList(ArrayList zoneList) {
	        this.zoneList = zoneList;
	    }

	    /**
	     * @return Returns the zoneName.
	     */
	    public String getZoneName() {
	        return zoneName;
	    }

	    /**
	     * @param zoneName
	     *            The zoneName to set.
	     */
	    public void setZoneName(String zoneName) {
	        this.zoneName = zoneName;
	    }

	    public int getDomainListSize() {
	        if (domainList != null) {
	            return domainList.size();
	        }
	        return 0;
	    }

	    public int getZoneListSize() {
	        if (getZoneList() != null) {
	            return (getZoneList().size());
	        } else {
	            return 0;
	        }
	    }

	    public int getParentUserListSize() {
	        if (parentUserList != null) {
	            return parentUserList.size();
	        }
	        return 0;
	    }

	    public void semiFlush() {
	        loginId = null;
	        userId = null;
	        parentUserName = null;
	        parentUserID = null;
	        userName = null;
	    }

	    /**
	     * @return Returns the activeUserId.
	     */
	    public String getActiveUserId() {
	        return activeUserId;
	    }

	    /**
	     * @param activeUserId
	     *            The activeUserId to set.
	     */
	    public void setActiveUserId(String activeUserId) {
	        this.activeUserId = activeUserId;
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

	    /**
	     * @return the cellId
	     */
	    public String getCellId() {
	        return cellId;
	    }

	    /**
	     * @param id
	     *            the cellId to set
	     */
	    public void setCellId(String id) {
	        cellId = id;
	    }

	    /**
	     * @return the switchId
	     */
	    public String getSwitchId() {
	        return switchId;
	    }

	    /**
	     * @param id
	     *            the switchId to set
	     */
	    public void setSwitchId(String id) {
	        switchId = id;
	    }

		public String getJspPath() {
			return jspPath;
		}

		public void setJspPath(String jspPath) {
			this.jspPath = jspPath;
		}

		public boolean isNetworkCodeCheck() {
			return networkCodeCheck;
		}

		public void setNetworkCodeCheck(boolean networkCodeCheck) {
			this.networkCodeCheck = networkCodeCheck;
		}
		
		
		
	

}
