package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;

/*
 * @(#)ProfileDetailsVO.java
 * Traveling object for users details
 */
public class ProfileDetailsVO {
	private String userGrade;
	private String userGradeName;
	private String transferProfile;
	private String transferRuleType;
	private String transferProfileId;
	private String transferProfilIdDesc;
	private ArrayList transferProfileList;
	private String transferRuleTypeId;
	private String transferRuleTypeIdDesc;
	private ArrayList transferRuleTypeList;
	
	private String commissionProfile;
	private String commissionProfileSetId;
	private String commissionProfileSetIdDesc;
	private ArrayList commissionProfileList;
	
	public String getUserGrade() {
		return userGrade;
	}
	public void setUserGrade(String userGrade) {
		this.userGrade = userGrade;
	}
	
	public String getUserGradeName() {
		return userGradeName;
	}
	public void setUserGradeName(String userGradeName) {
		this.userGradeName = userGradeName;
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
	public String getTransferRuleType() {
		return transferRuleType;
	}
	public void setTransferRuleType(String transferRuleType) {
		this.transferRuleType = transferRuleType;
	}
	public String getTransferProfileId() {
		return transferProfileId;
	}
	public void setTransferProfileId(String transferProfileId) {
		this.transferProfileId = transferProfileId;
	}
	public String getTransferProfilIdDesc() {
		return transferProfilIdDesc;
	}
	public void setTransferProfilIdDesc(String transferProfilIdDesc) {
		this.transferProfilIdDesc = transferProfilIdDesc;
	}
	public ArrayList getTransferProfileList() {
		return transferProfileList;
	}
	public void setTransferProfileList(ArrayList transferProfileList) {
		this.transferProfileList = transferProfileList;
	}
	public String getTransferRuleTypeId() {
		return transferRuleTypeId;
	}
	public void setTransferRuleTypeId(String transferRuleTypeId) {
		this.transferRuleTypeId = transferRuleTypeId;
	}
	public String getTransferRuleTypeIdDesc() {
		return transferRuleTypeIdDesc;
	}
	public void setTransferRuleTypeIdDesc(String transferRuleTypeIdDesc) {
		this.transferRuleTypeIdDesc = transferRuleTypeIdDesc;
	}
	public ArrayList getTransferRuleTypeList() {
		return transferRuleTypeList;
	}
	public void setTransferRuleTypeList(ArrayList transferRuleTypeList) {
		this.transferRuleTypeList = transferRuleTypeList;
	}
	public String getCommissionProfileSetId() {
		return commissionProfileSetId;
	}
	public void setCommissionProfileSetId(String commissionProfileSetId) {
		this.commissionProfileSetId = commissionProfileSetId;
	}
	public String getCommissionProfileSetIdDesc() {
		return commissionProfileSetIdDesc;
	}
	public void setCommissionProfileSetIdDesc(String commissionProfileSetIdDesc) {
		this.commissionProfileSetIdDesc = commissionProfileSetIdDesc;
	}
	public ArrayList getCommissionProfileList() {
		return commissionProfileList;
	}
	public void setCommissionProfileList(ArrayList commissionProfileList) {
		this.commissionProfileList = commissionProfileList;
	}
	@Override
	public String toString() {
		return "ProfileDetailsVO [userGrade=" + userGrade + ", transferProfile=" + transferProfile
				+ ", transferRuleType=" + transferRuleType + ", transferProfileId=" + transferProfileId
				+ ", transferProfilIdDesc=" + transferProfilIdDesc + ", transferProfileList=" + transferProfileList
				+ ", transferRuleTypeId=" + transferRuleTypeId + ", transferRuleTypeIdDesc=" + transferRuleTypeIdDesc
				+ ", transferRuleTypeList=" + transferRuleTypeList + ", commissionProfile=" + commissionProfile
				+ ", commissionProfileSetId=" + commissionProfileSetId + ", commissionProfileSetIdDesc="
				+ commissionProfileSetIdDesc + ", commissionProfileList=" + commissionProfileList + "]";
	}
	
	
	
	

}
