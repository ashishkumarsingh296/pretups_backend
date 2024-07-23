package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;
/*
 *  * @(#)PaymentAndServiceDetailsVO.java
 * Traveling object for users payment services
 */
import java.util.Arrays;

public class PaymentAndServiceDetailsVO {
	private String[] voucherType;
	private String[] paymentModes;
	private String inboundSuspensionRights;
	private String ouboundSuspensionRights;
	private String[] serviceInformation;
	public  String[] getUserWidgets() {
		return userWidgets;
	}
	public void setUserWidgets(String[] userWidgets) {
		this.userWidgets = userWidgets;
	}
	private String[] userWidgets;
	private String lowBalanceAlertToSelf;
	private String lowBalanceAlertToParent;
	private ArrayList voucherList;
	private String paymentType;
	private String[] paymentTypes;
	private String paymentDesc;
	private ArrayList paymentTypeList;
	private String[] serviceTypes;
	private ArrayList serviceList;
	
	
	public String getInboundSuspensionRights() {
		return inboundSuspensionRights;
	}
	public void setInboundSuspensionRights(String inboundSuspensionRights) {
		this.inboundSuspensionRights = inboundSuspensionRights;
	}
	public String getOuboundSuspensionRights() {
		return ouboundSuspensionRights;
	}
	public void setOuboundSuspensionRights(String ouboundSuspensionRights) {
		this.ouboundSuspensionRights = ouboundSuspensionRights;
	}
	public String getLowBalanceAlertToSelf() {
		return lowBalanceAlertToSelf;
	}
	public void setLowBalanceAlertToSelf(String lowBalanceAlertToSelf) {
		this.lowBalanceAlertToSelf = lowBalanceAlertToSelf;
	}
	public String getLowBalanceAlertToParent() {
		return lowBalanceAlertToParent;
	}
	public void setLowBalanceAlertToParent(String lowBalanceAlertToParent) {
		this.lowBalanceAlertToParent = lowBalanceAlertToParent;
	}
	public String getLowBalanceAlertToOthers() {
		return lowBalanceAlertToOthers;
	}
	public void setLowBalanceAlertToOthers(String lowBalanceAlertToOthers) {
		this.lowBalanceAlertToOthers = lowBalanceAlertToOthers;
	}
	private String lowBalanceAlertToOthers;
	
	public String[] getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String[] voucherType) {
		this.voucherType = voucherType;
	}
	public String[] getPaymentModes() {
		return paymentModes;
	}
	public void setPaymentModes(String[] paymentModes) {
		this.paymentModes = paymentModes;
	}
	public String[] getServiceInformation() {
		return serviceInformation;
	}
	public void setServiceInformation(String[] serviceInformation) {
		this.serviceInformation = serviceInformation;
	}
	public ArrayList getVoucherList() {
		return voucherList;
	}
	public void setVoucherList(ArrayList voucherList) {
		this.voucherList = voucherList;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentDesc() {
		return paymentDesc;
	}
	public void setPaymentDesc(String paymentDesc) {
		this.paymentDesc = paymentDesc;
	}
	public ArrayList getPaymentTypeList() {
		return paymentTypeList;
	}
	public void setPaymentTypeList(ArrayList paymentTypeList) {
		this.paymentTypeList = paymentTypeList;
	}
	public String[] getPaymentTypes() {
		return paymentTypes;
	}
	public void setPaymentTypes(String[] paymentTypes) {
		this.paymentTypes = paymentTypes;
	}
	public String[] getServiceTypes() {
		return serviceTypes;
	}
	public void setServiceTypes(String[] serviceTypes) {
		this.serviceTypes = serviceTypes;
	}
	public ArrayList getServiceList() {
		return serviceList;
	}
	public void setServiceList(ArrayList serviceList) {
		this.serviceList = serviceList;
	}
	@Override
	public String toString() {
		return "PaymentAndServiceDetailsVO [voucherType=" + Arrays.toString(voucherType) + ", paymentModes="
				+ Arrays.toString(paymentModes) + ", inboundSuspensionRights=" + inboundSuspensionRights
				+ ", ouboundSuspensionRights=" + ouboundSuspensionRights + ", serviceInformation="
				+ Arrays.toString(serviceInformation) + ", userWidgets=" + Arrays.toString(userWidgets)
				+ ", lowBalanceAlertToSelf=" + lowBalanceAlertToSelf + ", lowBalanceAlertToParent="
				+ lowBalanceAlertToParent + ", voucherList=" + voucherList + ", paymentType=" + paymentType
				+ ", paymentTypes=" + Arrays.toString(paymentTypes) + ", paymentDesc=" + paymentDesc
				+ ", paymentTypeList=" + paymentTypeList + ", serviceTypes=" + Arrays.toString(serviceTypes)
				+ ", serviceList=" + serviceList + ", lowBalanceAlertToOthers=" + lowBalanceAlertToOthers + "]";
	}
	
	


}
