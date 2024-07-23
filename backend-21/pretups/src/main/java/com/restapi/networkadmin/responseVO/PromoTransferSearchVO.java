package com.restapi.networkadmin.responseVO;

import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;

public class PromoTransferSearchVO extends TransferRulesVO {
	
	private String typeDisplay;
	private String serviceClassName;
	private String subscriberStatus;
	private String serviceProvideGroup;
	private String statusDesc;
	private String serviceTypeDesc;
	private String subServiceDesc;
	private String cardGroupSetName;
	private String promoApplicableFrom;
	private String promoApplicableTo;
	private String timeSlabs;
	
	public PromoTransferSearchVO(){
		
	}
	
	
	public  static  PromoTransferSearchVO getInstancePromoTransferSearchVO() {
		return new  PromoTransferSearchVO();
	}
	
	public String getTypeDisplay() {
		return typeDisplay;
	}
	public void setTypeDisplay(String typeDisplay) {
		this.typeDisplay = typeDisplay;
	}
	public String getServiceClassName() {
		return serviceClassName;
	}
	public void setServiceClassName(String serviceClassName) {
		this.serviceClassName = serviceClassName;
	}
	public String getSubscriberStatus() {
		return subscriberStatus;
	}
	public void setSubscriberStatus(String subscriberStatus) {
		this.subscriberStatus = subscriberStatus;
	}
	public String getServiceProvideGroup() {
		return serviceProvideGroup;
	}
	public void setServiceProvideGroup(String serviceProvideGroup) {
		this.serviceProvideGroup = serviceProvideGroup;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getServiceTypeDesc() {
		return serviceTypeDesc;
	}
	public void setServiceTypeDesc(String serviceTypeDesc) {
		this.serviceTypeDesc = serviceTypeDesc;
	}
	public String getSubServiceDesc() {
		return subServiceDesc;
	}
	public void setSubServiceDesc(String subServiceDesc) {
		this.subServiceDesc = subServiceDesc;
	}
	public String getCardGroupSetName() {
		return cardGroupSetName;
	}
	public void setCardGroupSetName(String cardGroupSetName) {
		this.cardGroupSetName = cardGroupSetName;
	}
	public String getPromoApplicableFrom() {
		return promoApplicableFrom;
	}
	public void setPromoApplicableFrom(String promoApplicableFrom) {
		this.promoApplicableFrom = promoApplicableFrom;
	}
	public String getPromoApplicableTo() {
		return promoApplicableTo;
	}
	public void setPromoApplicableTo(String promoApplicableTo) {
		this.promoApplicableTo = promoApplicableTo;
	}
	public String getTimeSlabs() {
		return timeSlabs;
	}
	public void setTimeSlabs(String timeSlabs) {
		this.timeSlabs = timeSlabs;
	}
	
	
	

}
