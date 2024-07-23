package com.btsl.pretups.channel.profile.businesslogic;

import java.util.ArrayList;

public class SelfCommEnquiryResponseVO {
	
	private String serviceAllowed;
	private String SequenceNo;
	private CommissionProfileSetVO commissionProfileSetVO;
	private CommissionProfileSetVersionVO commissionProfileSetVersionVO;
	private ArrayList<CommissionProfileCombinedVO> commissionList;
	private ArrayList <AdditionalProfileDeatilsVO> addProfileDetailList;
	private AdditionalProfileDeatilsVO additionalProfileDeatilsVO;
	private AdditionalProfileServicesVO additionalProfileServicesVO;
	private ArrayList<AdditionalProfileCombinedVO> additionalList;
	private ArrayList<OtfProfileCombinedVO> otfProfileList;
	
	public String getServiceAllowed() {
		return serviceAllowed;
	}
	public void setServiceAllowed(String serviceAllowed) {
		this.serviceAllowed = serviceAllowed;
	}
	public String getSequenceNo() {
		return SequenceNo;
	}
	public void setSequenceNo(String sequenceNo) {
		SequenceNo = sequenceNo;
	}
	public CommissionProfileSetVO getCommissionProfileSetVO() {
		return commissionProfileSetVO;
	}
	public void setCommissionProfileSetVO(
			CommissionProfileSetVO commissionProfileSetVO) {
		this.commissionProfileSetVO = commissionProfileSetVO;
	}
	public CommissionProfileSetVersionVO getCommissionProfileSetVersionVO() {
		return commissionProfileSetVersionVO;
	}
	public void setCommissionProfileSetVersionVO(
			CommissionProfileSetVersionVO commissionProfileSetVersionVO) {
		this.commissionProfileSetVersionVO = commissionProfileSetVersionVO;
	}
	public ArrayList<CommissionProfileCombinedVO> getCommissionList() {
		return commissionList;
	}
	public void setCommissionList(
			ArrayList<CommissionProfileCombinedVO> commissionList) {
		this.commissionList = commissionList;
	}
	public ArrayList<AdditionalProfileDeatilsVO> getAddProfileDetailList() {
		return addProfileDetailList;
	}
	public void setAddProfileDetailList(
			ArrayList<AdditionalProfileDeatilsVO> addProfileDetailList) {
		this.addProfileDetailList = addProfileDetailList;
	}
	public AdditionalProfileDeatilsVO getAdditionalProfileDeatilsVO() {
		return additionalProfileDeatilsVO;
	}
	public void setAdditionalProfileDeatilsVO(
			AdditionalProfileDeatilsVO additionalProfileDeatilsVO) {
		this.additionalProfileDeatilsVO = additionalProfileDeatilsVO;
	}
	public AdditionalProfileServicesVO getAdditionalProfileServicesVO() {
		return additionalProfileServicesVO;
	}
	public void setAdditionalProfileServicesVO(
			AdditionalProfileServicesVO additionalProfileServicesVO) {
		this.additionalProfileServicesVO = additionalProfileServicesVO;
	}
	public ArrayList<AdditionalProfileCombinedVO> getAdditionalList() {
		return additionalList;
	}
	public void setAdditionalList(
			ArrayList<AdditionalProfileCombinedVO> additionalList) {
		this.additionalList = additionalList;
	}
	public ArrayList<OtfProfileCombinedVO> getOtfProfileList() {
		return otfProfileList;
	}
	public void setOtfProfileList(ArrayList<OtfProfileCombinedVO> otfProfileList) {
		this.otfProfileList = otfProfileList;
	}
	
	
	
}
