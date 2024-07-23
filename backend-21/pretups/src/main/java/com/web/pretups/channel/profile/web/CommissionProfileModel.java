package com.web.pretups.channel.profile.web;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;

public class CommissionProfileModel {
	
	private String domainCode;
	
	private String categoryCode;
	
	private String grphDomainCode;
	
	private String gradeCode;

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getGrphDomainCode() {
		return grphDomainCode;
	}

	public void setGrphDomainCode(String grphDomainCode) {
		this.grphDomainCode = grphDomainCode;
	}

	public String getGradeCode() {
		return gradeCode;
	}

	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}
	
	private String networkName;
	private String domainCodeDesc;
	private String categoryCodeDesc;
	private String grphDomainCodeDesc;
	private String gradeCodeDesc;
	private String profileName;
	private String shortCode;
	private String version;
	private String applicableFromDate;
	private String applicableFromHour;
	private String productCodeDesc;//commissionProfileProductVO
	private String transferMultipleOffAsString;//commissionProfileProductVO
	private String minTransferValueAsStringCommission;//commissionProfileProductVO
	private String maxTransferValueAsStringCommission;//commissionProfileProductVO
	private String taxOnFOCApplicable;//commissionProfileProductVO
	private String taxOnChannelTransfer;//commissionProfileProductVO
	 private ArrayList slabsList;
	private String startRangeAsString;
	private String endRangeAsString;
	 private ArrayList amountTypeList;
	 private String commRateAsString;
	 private String tax1Type;//CommissionProfileDeatilsVO
	 private String tax1RateAsString;//CommissionProfileDeatilsVO
	 private String tax2Type;//CommissionProfileDeatilsVO
	 private String tax2RateAsString;//CommissionProfileDeatilsVO
	 private String tax3Type;//CommissionProfileDeatilsVO
	 
	 private String commType;
	 
	 
	 public String getCommType() {
		return commType;
	}

	public void setCommType(String commType) {
		this.commType = commType;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getDomainCodeDesc() {
		return domainCodeDesc;
	}

	public void setDomainCodeDesc(String domainCodeDesc) {
		this.domainCodeDesc = domainCodeDesc;
	}

	public String getCategoryCodeDesc() {
		return categoryCodeDesc;
	}

	public void setCategoryCodeDesc(String categoryCodeDesc) {
		this.categoryCodeDesc = categoryCodeDesc;
	}

	public String getGrphDomainCodeDesc() {
		return grphDomainCodeDesc;
	}

	public void setGrphDomainCodeDesc(String grphDomainCodeDesc) {
		this.grphDomainCodeDesc = grphDomainCodeDesc;
	}

	public String getGradeCodeDesc() {
		return gradeCodeDesc;
	}

	public void setGradeCodeDesc(String gradeCodeDesc) {
		this.gradeCodeDesc = gradeCodeDesc;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApplicableFromDate() {
		return applicableFromDate;
	}

	public void setApplicableFromDate(String applicableFromDate) {
		this.applicableFromDate = applicableFromDate;
	}

	public String getApplicableFromHour() {
		return applicableFromHour;
	}

	public void setApplicableFromHour(String applicableFromHour) {
		this.applicableFromHour = applicableFromHour;
	}

	public String getProductCodeDesc() {
		return productCodeDesc;
	}

	public void setProductCodeDesc(String productCodeDesc) {
		this.productCodeDesc = productCodeDesc;
	}

	public String getTransferMultipleOffAsString() {
		return transferMultipleOffAsString;
	}

	public void setTransferMultipleOffAsString(String transferMultipleOffAsString) {
		this.transferMultipleOffAsString = transferMultipleOffAsString;
	}

	public String getMinTransferValueAsStringCommission() {
		return minTransferValueAsStringCommission;
	}

	public void setMinTransferValueAsStringCommission(
			String minTransferValueAsStringCommission) {
		this.minTransferValueAsStringCommission = minTransferValueAsStringCommission;
	}

	public String getMaxTransferValueAsStringCommission() {
		return maxTransferValueAsStringCommission;
	}

	public void setMaxTransferValueAsStringCommission(
			String maxTransferValueAsStringCommission) {
		this.maxTransferValueAsStringCommission = maxTransferValueAsStringCommission;
	}

	public String getTaxOnFOCApplicable() {
		return taxOnFOCApplicable;
	}

	public void setTaxOnFOCApplicable(String taxOnFOCApplicable) {
		this.taxOnFOCApplicable = taxOnFOCApplicable;
	}

	public String getTaxOnChannelTransfer() {
		return taxOnChannelTransfer;
	}

	public void setTaxOnChannelTransfer(String taxOnChannelTransfer) {
		this.taxOnChannelTransfer = taxOnChannelTransfer;
	}

	public ArrayList getSlabsList() {
		return slabsList;
	}

	public void setSlabsList(ArrayList slabsList) {
		this.slabsList = slabsList;
	}

	public String getStartRangeAsString() {
		return startRangeAsString;
	}

	public void setStartRangeAsString(String startRangeAsString) {
		this.startRangeAsString = startRangeAsString;
	}

	public String getEndRangeAsString() {
		return endRangeAsString;
	}

	public void setEndRangeAsString(String endRangeAsString) {
		this.endRangeAsString = endRangeAsString;
	}

	public ArrayList getAmountTypeList() {
		return amountTypeList;
	}

	public void setAmountTypeList(ArrayList amountTypeList) {
		this.amountTypeList = amountTypeList;
	}

	public String getCommRateAsString() {
		return commRateAsString;
	}

	public void setCommRateAsString(String commRateAsString) {
		this.commRateAsString = commRateAsString;
	}

	public String getTax1Type() {
		return tax1Type;
	}

	public void setTax1Type(String tax1Type) {
		this.tax1Type = tax1Type;
	}

	public String getTax1RateAsString() {
		return tax1RateAsString;
	}

	public void setTax1RateAsString(String tax1RateAsString) {
		this.tax1RateAsString = tax1RateAsString;
	}

	public String getTax2Type() {
		return tax2Type;
	}

	public void setTax2Type(String tax2Type) {
		this.tax2Type = tax2Type;
	}

	public String getTax2RateAsString() {
		return tax2RateAsString;
	}

	public void setTax2RateAsString(String tax2RateAsString) {
		this.tax2RateAsString = tax2RateAsString;
	}

	public String getTax3Type() {
		return tax3Type;
	}

	public void setTax3Type(String tax3Type) {
		this.tax3Type = tax3Type;
	}

	public String getTax3RateAsString() {
		return tax3RateAsString;
	}

	public void setTax3RateAsString(String tax3RateAsString) {
		this.tax3RateAsString = tax3RateAsString;
	}

	public String getShowAdditionalCommissionFlag() {
		return showAdditionalCommissionFlag;
	}

	public void setShowAdditionalCommissionFlag(String showAdditionalCommissionFlag) {
		this.showAdditionalCommissionFlag = showAdditionalCommissionFlag;
	}

	public ArrayList getAdditionalProfileList() {
		return additionalProfileList;
	}

	public void setAdditionalProfileList(ArrayList additionalProfileList) {
		this.additionalProfileList = additionalProfileList;
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

	public String getGatewayCode() {
		return gatewayCode;
	}

	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}

	public String getMinTransferValueAsStringAdditional() {
		return minTransferValueAsStringAdditional;
	}

	public void setMinTransferValueAsStringAdditional(
			String minTransferValueAsStringAdditional) {
		this.minTransferValueAsStringAdditional = minTransferValueAsStringAdditional;
	}

	public String getMaxTransferValueAsStringAdditional() {
		return maxTransferValueAsStringAdditional;
	}

	public void setMaxTransferValueAsStringAdditional(
			String maxTransferValueAsStringAdditional) {
		this.maxTransferValueAsStringAdditional = maxTransferValueAsStringAdditional;
	}

	public String getApplicableFromAdditional() {
		return applicableFromAdditional;
	}

	public void setApplicableFromAdditional(String applicableFromAdditional) {
		this.applicableFromAdditional = applicableFromAdditional;
	}

	public String getApplicableToAdditional() {
		return applicableToAdditional;
	}

	public void setApplicableToAdditional(String applicableToAdditional) {
		this.applicableToAdditional = applicableToAdditional;
	}

	public String getAddtnlComStatusName() {
		return addtnlComStatusName;
	}

	public void setAddtnlComStatusName(String addtnlComStatusName) {
		this.addtnlComStatusName = addtnlComStatusName;
	}

	public String getAdditionalCommissionTimeSlab() {
		return additionalCommissionTimeSlab;
	}

	public void setAdditionalCommissionTimeSlab(String additionalCommissionTimeSlab) {
		this.additionalCommissionTimeSlab = additionalCommissionTimeSlab;
	}

	public String getRoamRecharge() {
		return roamRecharge;
	}

	public void setRoamRecharge(String roamRecharge) {
		this.roamRecharge = roamRecharge;
	}

	public String getAddCommType() {
		return addCommType;
	}

	public void setAddCommType(String addCommType) {
		this.addCommType = addCommType;
	}

	public String getAddCommRateAsString() {
		return addCommRateAsString;
	}

	public void setAddCommRateAsString(String addCommRateAsString) {
		this.addCommRateAsString = addCommRateAsString;
	}

	public String getAddRoamCommType() {
		return addRoamCommType;
	}

	public void setAddRoamCommType(String addRoamCommType) {
		this.addRoamCommType = addRoamCommType;
	}

	public String getAddRoamCommRateAsString() {
		return addRoamCommRateAsString;
	}

	public void setAddRoamCommRateAsString(String addRoamCommRateAsString) {
		this.addRoamCommRateAsString = addRoamCommRateAsString;
	}

	public String getDiffrentialFactorAsString() {
		return diffrentialFactorAsString;
	}

	public void setDiffrentialFactorAsString(String diffrentialFactorAsString) {
		this.diffrentialFactorAsString = diffrentialFactorAsString;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	private String tax3RateAsString;//CommissionProfileDeatilsVO
	 private String showAdditionalCommissionFlag;
	 private ArrayList additionalProfileList;
	 private String serviceTypeDesc;//additionalProfileServicesVO
	 private String subServiceDesc;//additionalProfileServicesVO
	 public boolean isDeleteAllowed() {
		return deleteAllowed;
	}

	public void setDeleteAllowed(boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}

	private String gatewayCode;//additionalProfileServicesVO
	 private String minTransferValueAsStringAdditional;//additionalProfileServicesVO
	 private String maxTransferValueAsStringAdditional;//additionalProfileServicesVO
	 private String applicableFromAdditional;//additionalProfileServicesVO
	 private String applicableToAdditional;//additionalProfileServicesVO
	 private String addtnlComStatusName;//additionalProfileServicesVO
	 private String additionalCommissionTimeSlab;//additionalProfileServicesVO
	 private String roamRecharge;
	 private String addCommType;
	 private String addCommRateAsString;
	 private String addRoamCommType;
	 private String addRoamCommRateAsString;
	 private String diffrentialFactorAsString;
	 private String requestType;
	 private ArrayList commissionProfileList;

	public ArrayList getCommissionProfileList() {
		return commissionProfileList;
	}

	public void setCommissionProfileList(ArrayList commissionProfileList) {
		this.commissionProfileList = commissionProfileList;
	}

	private boolean deleteAllowed;
	
	private ArrayList<ListValueVO> domainList;

	public ArrayList<ListValueVO> getDomainList() {
		return domainList;
	}

	public void setDomainList(ArrayList<ListValueVO> domainList) {
		this.domainList = domainList;
	}
	
	private ArrayList<CategoryVO> categoryList;

	public ArrayList<CategoryVO> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<CategoryVO> categoryList) {
		this.categoryList = categoryList;
	}
	
	private ArrayList<GeographicalDomainVO> geographyList;

	public ArrayList<GeographicalDomainVO> getGeographyList() {
		return geographyList;
	}

	public void setGeographyList(ArrayList<GeographicalDomainVO> geographyList) {
		this.geographyList = geographyList;
	}
	
	private ArrayList<GradeVO> gradeList;

	public ArrayList<GradeVO> getGradeList() {
		return gradeList;
	}

	public void setGradeList(ArrayList<GradeVO> gradeList) {
		this.gradeList = gradeList;
	}
	
	  private ArrayList<CommissionProfileSetVO> selectCommProfileSetList;

	public ArrayList<CommissionProfileSetVO> getSelectCommProfileSetList() {
		return selectCommProfileSetList;
	}

	public void setSelectCommProfileSetList(ArrayList<CommissionProfileSetVO> selectCommProfileSetList) {
		this.selectCommProfileSetList = selectCommProfileSetList;
	}
	
	private String code;
	private String oldCode;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOldCode() {
		return oldCode;
	}

	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}
	
	private List<CommissionProfileSetVO> commissionProfileSetVOs;

	public List<CommissionProfileSetVO> getCommissionProfileSetVOs() {
		return commissionProfileSetVOs;
	}

	public void setCommissionProfileSetVOs(
			List<CommissionProfileSetVO> commissionProfileSetVOs) {
		this.commissionProfileSetVOs = commissionProfileSetVOs;
	}
	
	private int resultCount;

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	
	private Integer defaultProfileIndex;

	public Integer getDefaultProfileIndex() {
		return defaultProfileIndex;
	}

	public void setDefaultProfileIndex(Integer defaultProfileIndex) {
		this.defaultProfileIndex = defaultProfileIndex;
	}

	
}
