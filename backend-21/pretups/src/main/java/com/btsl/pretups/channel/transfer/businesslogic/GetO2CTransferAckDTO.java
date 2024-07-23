package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalcommSlabVO;
import com.btsl.pretups.channel.profile.businesslogic.CBCcommSlabDetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionSlabDetVO;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class GetO2CTransferAckDTO  {

	
	private String fromUserID;
	private String toUserID;
	private String  dateTime;
	private String transactionID;
	private String userName;
	private String status;
	private String domain;
	private String category;
	private String geography;
	private String mobileNumber;
	private String networkName;
	private String commissionProfile;
	private String transferProfile;
	private String transferType;
	private String transferCategory;
	private String transNumberExternal;
	private String transDateExternal;
	private String referenceNumber;
	private String erpCode;
	private String address;
	private String productShortCode;
	private String productName;
	private String denomination;
	private String quantity;
	private String approvedQuantity;
	private String level1ApprovedQuantity;
	private String level2ApprovedQuantity;
	private String level3ApprovedQuantity;
	private String tax1Rate;
	private String tax1Type;
	private String tax1Amount;
	private String tax2Rate;
	private String tax2Type;
	private String tax2Amount;
	private String tax3Rate;
	private String tds;
	private String commisionRate;
	private String commisionType;
	private String commisionAmount;
	private String receiverCreditQuantity;
	private String cbcRate;
	private String cbcType;
	private String cbcAmount;
	private String denominationAmount;
	private String payableAmount;
	private String netAmount;
	private String paymentInstrumentNumber;
	private String paymentInstrumentDate;
	private String paymentInstrumentAmount;
	private String paymentMode;
	private String firstApprovedRemarks;
	private String secondApprovedRemarks;
	private String thirdApprovedRemarks;
	private String voucherBatchNumber;
	private String vomsProductName;
	private String batchType;
	private String totalNoofVouchers;
	private String fromSerialNumber;
	private String toSerialNumber;
	private  List<O2CTransAckVoucherDetail> listVoucherDetails;

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getCommissionProfile() {
		return commissionProfile;
	}

	public void setCommissionProfile(String commissionProfile) {
		this.commissionProfile = commissionProfile;
	}

	public String getTransferProfile() {
		return transferProfile;
	}

	public void setTransferProfile(String transferProfile) {
		this.transferProfile = transferProfile;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getTransferCategory() {
		return transferCategory;
	}

	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}

	public String getTransNumberExternal() {
		return transNumberExternal;
	}

	public void setTransNumberExternal(String transNumberExternal) {
		this.transNumberExternal = transNumberExternal;
	}

	public String getTransDateExternal() {
		return transDateExternal;
	}

	public void setTransDateExternal(String transDateExternal) {
		this.transDateExternal = transDateExternal;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getErpCode() {
		return erpCode;
	}

	public void setErpCode(String erpCode) {
		this.erpCode = erpCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProductShortCode() {
		return productShortCode;
	}

	public void setProductShortCode(String productShortCode) {
		this.productShortCode = productShortCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getApprovedQuantity() {
		return approvedQuantity;
	}

	public void setApprovedQuantity(String approvedQuantity) {
		this.approvedQuantity = approvedQuantity;
	}

	public String getLevel1ApprovedQuantity() {
		return level1ApprovedQuantity;
	}

	public void setLevel1ApprovedQuantity(String level1ApprovedQuantity) {
		this.level1ApprovedQuantity = level1ApprovedQuantity;
	}

	public String getLevel2ApprovedQuantity() {
		return level2ApprovedQuantity;
	}

	public void setLevel2ApprovedQuantity(String level2ApprovedQuantity) {
		this.level2ApprovedQuantity = level2ApprovedQuantity;
	}

	public String getLevel3ApprovedQuantity() {
		return level3ApprovedQuantity;
	}

	public void setLevel3ApprovedQuantity(String level3ApprovedQuantity) {
		this.level3ApprovedQuantity = level3ApprovedQuantity;
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

	public String getTax1Amount() {
		return tax1Amount;
	}

	public void setTax1Amount(String tax1Amount) {
		this.tax1Amount = tax1Amount;
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

	public String getTax2Amount() {
		return tax2Amount;
	}

	public void setTax2Amount(String tax2Amount) {
		this.tax2Amount = tax2Amount;
	}

	public String getTds() {
		return tds;
	}

	public void setTds(String tds) {
		this.tds = tds;
	}

	public String getCommisionRate() {
		return commisionRate;
	}

	public void setCommisionRate(String commisionRate) {
		this.commisionRate = commisionRate;
	}

	public String getCommisionType() {
		return commisionType;
	}

	public void setCommisionType(String commisionType) {
		this.commisionType = commisionType;
	}

	public String getCommisionAmount() {
		return commisionAmount;
	}

	public void setCommisionAmount(String commisionAmount) {
		this.commisionAmount = commisionAmount;
	}

	public String getReceiverCreditQuantity() {
		return receiverCreditQuantity;
	}

	public void setReceiverCreditQuantity(String receiverCreditQuantity) {
		this.receiverCreditQuantity = receiverCreditQuantity;
	}

	public String getCbcRate() {
		return cbcRate;
	}

	public void setCbcRate(String cbcRate) {
		this.cbcRate = cbcRate;
	}

	public String getCbcType() {
		return cbcType;
	}

	public void setCbcType(String cbcType) {
		this.cbcType = cbcType;
	}

	public String getCbcAmount() {
		return cbcAmount;
	}

	public void setCbcAmount(String cbcAmount) {
		this.cbcAmount = cbcAmount;
	}

	public String getDenominationAmount() {
		return denominationAmount;
	}

	public void setDenominationAmount(String denominationAmount) {
		this.denominationAmount = denominationAmount;
	}

	public String getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(String payableAmount) {
		this.payableAmount = payableAmount;
	}

	public String getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}

	public String getPaymentInstrumentNumber() {
		return paymentInstrumentNumber;
	}

	public void setPaymentInstrumentNumber(String paymentInstrumentNumber) {
		this.paymentInstrumentNumber = paymentInstrumentNumber;
	}

	public String getPaymentInstrumentDate() {
		return paymentInstrumentDate;
	}

	public void setPaymentInstrumentDate(String paymentInstrumentDate) {
		this.paymentInstrumentDate = paymentInstrumentDate;
	}

	public String getPaymentInstrumentAmount() {
		return paymentInstrumentAmount;
	}

	public void setPaymentInstrumentAmount(String paymentInstrumentAmount) {
		this.paymentInstrumentAmount = paymentInstrumentAmount;
	}

	public String getFirstApprovedRemarks() {
		return firstApprovedRemarks;
	}

	public void setFirstApprovedRemarks(String firstApprovedRemarks) {
		this.firstApprovedRemarks = firstApprovedRemarks;
	}

	public String getSecondApprovedRemarks() {
		return secondApprovedRemarks;
	}

	public void setSecondApprovedRemarks(String secondApprovedRemarks) {
		this.secondApprovedRemarks = secondApprovedRemarks;
	}

	public String getThirdApprovedRemarks() {
		return thirdApprovedRemarks;
	}

	public void setThirdApprovedRemarks(String thirdApprovedRemarks) {
		this.thirdApprovedRemarks = thirdApprovedRemarks;
	}

	
	public List<O2CTransAckVoucherDetail> getListVoucherDetails() {
		return listVoucherDetails;
	}

	public String getVoucherBatchNumber() {
		return voucherBatchNumber;
	}

	public void setVoucherBatchNumber(String voucherBatchNumber) {
		this.voucherBatchNumber = voucherBatchNumber;
	}

	public String getVomsProductName() {
		return vomsProductName;
	}

	public void setVomsProductName(String vomsProductName) {
		this.vomsProductName = vomsProductName;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public String getTotalNoofVouchers() {
		return totalNoofVouchers;
	}

	public void setTotalNoofVouchers(String totalNoofVouchers) {
		this.totalNoofVouchers = totalNoofVouchers;
	}

	public String getFromSerialNumber() {
		return fromSerialNumber;
	}

	public void setFromSerialNumber(String fromSerialNumber) {
		this.fromSerialNumber = fromSerialNumber;
	}

	public String getToSerialNumber() {
		return toSerialNumber;
	}

	public void setToSerialNumber(String toSerialNumber) {
		this.toSerialNumber = toSerialNumber;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getTax3Rate() {
		return tax3Rate;
	}

	public void setTax3Rate(String tax3Rate) {
		this.tax3Rate = tax3Rate;
	}

	public void setListVoucherDetails(List<O2CTransAckVoucherDetail> listVoucherDetails) {
		this.listVoucherDetails = listVoucherDetails;
	}

	public String getFromUserID() {
		return fromUserID;
	}

	public void setFromUserID(String fromUserID) {
		this.fromUserID = fromUserID;
	}

	public String getToUserID() {
		return toUserID;
	}

	public void setToUserID(String toUserID) {
		this.toUserID = toUserID;
	}
	
	
	
	
	
	
	
	

	
}
