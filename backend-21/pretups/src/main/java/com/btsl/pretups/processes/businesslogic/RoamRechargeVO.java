package com.btsl.pretups.processes.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class RoamRechargeVO implements Serializable {
	
	
	private String _retailerName;	
	private String  _retailerMsisdn;
	private String _retailerUniqueId;
	private String _distributorName;
	private String _distributorMsisdn;
	private String _distributorUniqueId; 
	private long  _roamRechargeAmount;
	private long _differenitalRoamRchgAmount;
	private String _retailerBaseMargin;
	private String _distributorBaseMargin;
	private long _retailerCommissionAmount;
	private long _distributorCommissionAmount;
	private long _retailerPenalty;
	private long _distributorPenalty;
	private String _serviceTax;
	private long _retailerPenaltyAmount;
	private long _distributorPenaltyAmount;
	private String _networkCode;
	private String _userCategoryCode;
	private String _ownerNetworkCode;	
	private String _ownerCategoryCode;
	private String _serviceType;
	private String _serviceTypeCode;
	
	public String getServiceTypeCode() {
		return _serviceTypeCode;
	}
	public void setServiceTypeCode(String _serviceTypeCode) {
		this._serviceTypeCode = _serviceTypeCode;
	}
	private long _penaltyTransCount;
	
	 
	public long getPenaltyTransCount() {
		return _penaltyTransCount;
	}
	public void setPenaltyTransCount(long _penaltyTransCount) {
		this._penaltyTransCount = _penaltyTransCount;
	}
	public String getServiceType() {
		return _serviceType;
	}
	public void setServiceType(String _serviceType) {
		this._serviceType = _serviceType;
	}
	private String _transferDateAsString;
	 private Date _transferDate;
	 
	 private String _serviceTax2;
	 private String _serviceOwnerTax;
	 private String _serviceOwnerTax2;
	
	
	
	public String getRetailerName() {
		return _retailerName;
	}
	public void setRetailerName(String _retailerName) {
		this._retailerName = _retailerName;
	}
	public String getRetailerMsisdn() {
		return _retailerMsisdn;
	}
	public void setRetailerMsisdn(String _retailerMsisdn) {
		this._retailerMsisdn = _retailerMsisdn;
	}
	public String getRetailerUniqueId() {
		return _retailerUniqueId;
	}
	public void setRetailerUniqueId(String _retailerUniqueId) {
		this._retailerUniqueId = _retailerUniqueId;
	}
	public String getDistributorName() {
		return _distributorName;
	}
	public void setDistributorName(String _distributorName) {
		this._distributorName = _distributorName;
	}
	public String getDistributorMsisdn() {
		return _distributorMsisdn;
	}
	public void setDistributorMsisdn(String _distributorMsisdn) {
		this._distributorMsisdn = _distributorMsisdn;
	}
	public String getDistributorUniqueId() {
		return _distributorUniqueId;
	}
	public void setDistributorUniqueId(String _distributorUniqueId) {
		this._distributorUniqueId = _distributorUniqueId;
	}
	public long getRoamRechargeAmount() {
		return _roamRechargeAmount;
	}
	public void setRoamRechargeAmount(long _roamRechargeAmount) {
		this._roamRechargeAmount = _roamRechargeAmount;
	}
	public long getDifferenitalRoamRchgAmount() {
		return _differenitalRoamRchgAmount;
	}
	public void setDifferenitalRoamRchgAmount(long _differenitalRoamRchgAmount) {
		this._differenitalRoamRchgAmount = _differenitalRoamRchgAmount;
	}
	public String getRetailerBaseMargin() {
		return _retailerBaseMargin;
	}
	public void setRetailerBaseMargin(String _retailerBaseMargin) {
		this._retailerBaseMargin = _retailerBaseMargin;
	}
	public String getDistributorBaseMargin() {
		return _distributorBaseMargin;
	}
	public void setDistributorBaseMargin(String _distributorBaseMargin) {
		this._distributorBaseMargin = _distributorBaseMargin;
	}
	public long getRetailerCommissionAmount() {
		return _retailerCommissionAmount;
	}
	public void setRetailerCommissionAmount(long _retailerCommissionAmount) {
		this._retailerCommissionAmount = _retailerCommissionAmount;
	}
	public long getDistributorCommissionAmount() {
		return _distributorCommissionAmount;
	}
	public void setDistributorCommissionAmount(long _distributorCommissionAmount) {
		this._distributorCommissionAmount = _distributorCommissionAmount;
	}
	public long getRetailerPenalty() {
		return _retailerPenalty;
	}
	public void setRetailerPenalty(long _retailerPenalty) {
		this._retailerPenalty = _retailerPenalty;
	}
	public long getDistributorPenalty() {
		return _distributorPenalty;
	}
	public void setDistributorPenalty(long _distributorPenalty) {
		this._distributorPenalty = _distributorPenalty;
	}
	public String getServiceTax() {
		return _serviceTax;
	}
	public void setServiceTax(String _serviceTax) {
		this._serviceTax = _serviceTax;
	}
	
	public String getServiceTax2() {
		return _serviceTax2;
	}
	public void setServiceTax2(String _serviceTax2) {
		this._serviceTax2 = _serviceTax2;
	}
	public long getRetailerPenaltyAmount() {
		return _retailerPenaltyAmount;
	}
	public void setRetailerPenaltyAmount(long _retailerPenaltyAmount) {
		this._retailerPenaltyAmount = _retailerPenaltyAmount;
	}
	public long getDistributorPenaltyAmount() {
		return _distributorPenaltyAmount;
	}
	public void setDistributorPenaltyAmount(long _distributorPenaltyAmount) {
		this._distributorPenaltyAmount = _distributorPenaltyAmount;
	}
	public String getNetworkCode() {
		return _networkCode;
	}
	public void setNetworkCode(String _networkCode) {
		this._networkCode = _networkCode;
	}
	public String getUserCategoryCode() {
		return _userCategoryCode;
	}
	public void setUserCategoryCode(String _userCategoryCode) {
		this._userCategoryCode = _userCategoryCode;
	}
	
	public String getOwnerNetworkCode() {
		return _ownerNetworkCode;
	}
	public void setOwnerNetworkCode(String _ownerNetworkCode) {
		this._ownerNetworkCode = _ownerNetworkCode;
	}
	public String getOwnerCategoryCode() {
		return _ownerCategoryCode;
	}
	public void setOwnerCategoryCode(String _ownerCategoryCode) {
		this._ownerCategoryCode = _ownerCategoryCode;
	}
	
	public String getTransferDateAsString() {
		return _transferDateAsString;
	}
	public void setTransferDateAsString(String _transferDateAsString) {
		this._transferDateAsString = _transferDateAsString;
	}
	public Date getTransferDate() {
		return _transferDate;
	}
	public void setTransferDate(Date _transferDate) {
		this._transferDate = _transferDate;
	}
	
	
	public String getServiceOwnerTax() {
		return _serviceOwnerTax;
	}
	public void setServiceOwnerTax(String _serviceOwnerTax) {
		this._serviceOwnerTax = _serviceOwnerTax;
	}
	
	public String getServiceOwnerTax2() {
		return _serviceOwnerTax2;
	}
	public void setServiceOwnerTax2(String _serviceOwnerTax2) {
		this._serviceOwnerTax2 = _serviceOwnerTax2;
	}
	
	

}
