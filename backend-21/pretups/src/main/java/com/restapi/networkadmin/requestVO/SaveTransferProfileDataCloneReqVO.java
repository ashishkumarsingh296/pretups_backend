package com.restapi.networkadmin.requestVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.btsl.common.MasterErrorList;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.BTSLUtil;

public class SaveTransferProfileDataCloneReqVO {

	public SaveTransferProfileDataCloneReqVO() {

	}

	private String networkCode;
	private String domainCode;
	private String categoryCode;
	private String profileName;
	private String shortName;
	private String description;
	private String defaultProfile;
	private String profileID;
	private String status;
	private boolean subscriberOutCountFlag;

	private List<TransferProfileProductReqVO> productBalancelist;

	private String dailyInCount; // Daily channel transfer in count
	private String dailyInValue; // Daily channel transfer in value
	private String dailyOutCount; // Daily channel transfer out count
	private String dailyOutValue; // Daily channel transfer out value

	private String weeklyInCount; // Week channel transfer in count
	private String weeklyInValue; // Week channel transfer in value
	private String weeklyOutCount; // Week channel transfer out count
	private String weeklyOutValue; // Week channel transfer out value

	private String dailySubscriberOutCount; // Daily subscriber out count
	private String weeklySubscriberOutCount; // Weekly subscriber out count
	private String monthlySubscriberOutCount; // Monthly subscriber out count

	private String dailySubscriberOutValue; // Daily subscriber out value
	private String weeklySubscriberOutValue; // Weekly subscriber out value
	private String monthlySubscriberOutValue; // Monthly subscriber out value

	private String monthlyInCount; // Monthly channel transfer in count
	private String monthlyInValue; // Monthly channel transfer in Value
	private String monthlyOutCount; // Monthly channel transfer out count
	private String monthlyOutValue; // Monthly channel transfer out value

	// Alerting variables
	private String dailyInAltCount; // Daily channel Transfer in alerting count
	private String dailyInAltValue; // Daily channel Transfer in alerting value
	private String dailyOutAltCount; // Daily channel Transfer out alerting count
	private String dailyOutAltValue; // Daily channel Transfer out alerting value

	private String weeklyInAltCount; // Weekly channel Transfer in alerting count
	private String weeklyInAltValue; // Weekly channel Transfer in alerting value
	private String weeklyOutAltCount; // Weekly channel Transfer out alerting count
	private String weeklyOutAltValue; // Weekly channel Transfer out alerting value

	private String dailySubscriberOutAltCount; // Daily subscriber out alerting count
	private String weeklySubscriberOutAltCount; // Weekly subscriber out alerting count
	private String monthlySubscriberOutAltCount; // Monthly subscriber out alerting count

	private String dailySubscriberOutAltValue; // Daily subscriber out alerting value
	private String weeklySubscriberOutAltValue; // Weekly subscriber out alerting value
	private String monthlySubscriberOutAltValue; // Monthly subscriber out alerting value

	private String monthlyInAltCount; // Monthly channel transfer in alerting count
	private String monthlyInAltValue; // Monthly channel transfer in alerting value
	private String monthlyOutAltCount; // Monthly channel transfer out alerting count
	private String monthlyOutAltValue; // Monthly channel transfer out alerting value

	private String unctrlDailyInCount; // OUTSIDE daily in count
	private String unctrlDailyInValue; // OUTSIDE daily in value
	private String unctrlDailyOutCount;// OUTSIDE daily out count
	private String unctrlDailyOutValue; // OUTSIDE daily out value

	private String unctrlWeeklyInCount; // OUTSIDE weekly in count
	private String unctrlWeeklyInValue; // OUTSIDE weekly in value
	private String unctrlWeeklyOutCount; // OUTSIDE weekly out count
	private String unctrlWeeklyOutValue; // OUTSIDE weekly out value

	private String unctrlMonthlyInCount; // OUTSIDE Monthly in count
	private String unctrlMonthlyInValue; // OUTSIDE Monthly in value
	private String unctrlMonthlyOutCount; // OUTSIDE Monthly out count
	private String unctrlMonthlyOutValue;// OUTSIDE Monthly out value

	private String unctrlDailyInAltCount; // Alerting daily transfer in count
	private String unctrlDailyInAltValue; // Alerting daily transfer in value
	private String unctrlDailyOutAltCount;// Alerting daily transfer out count
	private String unctrlDailyOutAltValue; // Alerting daily transfer out value

	private String unctrlWeeklyInAltCount; // Alerting weekly transfer in count
	private String unctrlWeeklyInAltValue; // Alerting weekly transfer in value
	private String unctrlWeeklyOutAltCount; // Alerting weekly transfer out count
	private String unctrlWeeklyOutAltValue; // Alerting weekly transfer out value

	private String unctrlMonthlyInAltCount; // Alerting monthly transfer in count
	private String unctrlMonthlyInAltValue; // Alerting monthly transfer in value
	private String unctrlMonthlyOutAltCount; // Alerting monthly transfer out count
	private String unctrlMonthlyOutAltValue; // Alerting monthly transfer out value

	// 6.4 changes

	private String dailySubscriberInCount; // Daily Subscriber in count
	private String weeklySubscriberInCount; // Weekly Subscriber in count
	private String monthlySubscriberInCount;// Monthly subscriber in count

	private String dailySubscriberInValue; // Daily Subscriber in value
	private String weeklySubscriberInValue; // Weekly Subscriber in value
	private String monthlySubscriberInValue; // Monthly Subscriber in value

	private String dailySubscriberInAltCount; // Daily Subscriber in alerting count
	private String weeklySubscriberInAltCount; // Weekly Subscriber in alerting count
	private String monthlySubscriberInAltCount; // Monthly Subscriber in alerting count

	private String dailySubscriberInAltValue; // Daily Subscriber in alerting value
	private String weeklySubscriberInAltValue; // Weekly Subscriber in alerting value
	private String monthlySubscriberInAltValue; // Monthly Subscriber in alerting value
	private String unctrlTransferFlag;
	private String action; //1) VALIDATE 202,400  2) CONFIRM 200

	public String getUnctrlTransferFlag() {
		return unctrlTransferFlag;
	}

	public void setUnctrlTransferFlag(String unctrlTransferFlag) {
		this.unctrlTransferFlag = unctrlTransferFlag;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

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

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDefaultProfile() {
		return defaultProfile;
	}

	public void setDefaultProfile(String defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	public List<TransferProfileProductReqVO> getProductBalancelist() {
		return productBalancelist;
	}

	public void setProductBalancelist(List<TransferProfileProductReqVO> productBalancelist) {
		this.productBalancelist = productBalancelist;
	}

	public String getDailyInCount() {
		return dailyInCount;
	}

	public void setDailyInCount(String dailyInCount) {
		this.dailyInCount = dailyInCount;
	}

	public String getDailyInValue() {
		return dailyInValue;
	}

	public void setDailyInValue(String dailyInValue) {
		this.dailyInValue = dailyInValue;
	}

	public String getDailyOutCount() {
		return dailyOutCount;
	}

	public void setDailyOutCount(String dailyOutCount) {
		this.dailyOutCount = dailyOutCount;
	}

	public String getDailyOutValue() {
		return dailyOutValue;
	}

	public void setDailyOutValue(String dailyOutValue) {
		this.dailyOutValue = dailyOutValue;
	}

	public String getWeeklyInCount() {
		return weeklyInCount;
	}

	public void setWeeklyInCount(String weeklyInCount) {
		this.weeklyInCount = weeklyInCount;
	}

	public String getWeeklyInValue() {
		return weeklyInValue;
	}

	public void setWeeklyInValue(String weeklyInValue) {
		this.weeklyInValue = weeklyInValue;
	}

	public String getWeeklyOutCount() {
		return weeklyOutCount;
	}

	public void setWeeklyOutCount(String weeklyOutCount) {
		this.weeklyOutCount = weeklyOutCount;
	}

	public String getWeeklyOutValue() {
		return weeklyOutValue;
	}

	public void setWeeklyOutValue(String weeklyOutValue) {
		this.weeklyOutValue = weeklyOutValue;
	}

	public String getDailySubscriberOutCount() {
		return dailySubscriberOutCount;
	}

	public void setDailySubscriberOutCount(String dailySubscriberOutCount) {
		this.dailySubscriberOutCount = dailySubscriberOutCount;
	}

	public String getWeeklySubscriberOutCount() {
		return weeklySubscriberOutCount;
	}

	public void setWeeklySubscriberOutCount(String weeklySubscriberOutCount) {
		this.weeklySubscriberOutCount = weeklySubscriberOutCount;
	}

	public String getMonthlySubscriberOutCount() {
		return monthlySubscriberOutCount;
	}

	public void setMonthlySubscriberOutCount(String monthlySubscriberOutCount) {
		this.monthlySubscriberOutCount = monthlySubscriberOutCount;
	}

	public String getDailySubscriberOutValue() {
		return dailySubscriberOutValue;
	}

	public void setDailySubscriberOutValue(String dailySubscriberOutValue) {
		this.dailySubscriberOutValue = dailySubscriberOutValue;
	}

	public String getWeeklySubscriberOutValue() {
		return weeklySubscriberOutValue;
	}

	public void setWeeklySubscriberOutValue(String weeklySubscriberOutValue) {
		this.weeklySubscriberOutValue = weeklySubscriberOutValue;
	}

	public String getMonthlySubscriberOutValue() {
		return monthlySubscriberOutValue;
	}

	public void setMonthlySubscriberOutValue(String monthlySubscriberOutValue) {
		this.monthlySubscriberOutValue = monthlySubscriberOutValue;
	}

	public String getMonthlyInCount() {
		return monthlyInCount;
	}

	public void setMonthlyInCount(String monthlyInCount) {
		this.monthlyInCount = monthlyInCount;
	}

	public String getMonthlyInValue() {
		return monthlyInValue;
	}

	public void setMonthlyInValue(String monthlyInValue) {
		this.monthlyInValue = monthlyInValue;
	}

	public String getMonthlyOutCount() {
		return monthlyOutCount;
	}

	public void setMonthlyOutCount(String monthlyOutCount) {
		this.monthlyOutCount = monthlyOutCount;
	}

	public String getMonthlyOutValue() {
		return monthlyOutValue;
	}

	public void setMonthlyOutValue(String monthlyOutValue) {
		this.monthlyOutValue = monthlyOutValue;
	}

	public String getDailyInAltCount() {
		return dailyInAltCount;
	}

	public void setDailyInAltCount(String dailyInAltCount) {
		this.dailyInAltCount = dailyInAltCount;
	}

	public String getDailyInAltValue() {
		return dailyInAltValue;
	}

	public void setDailyInAltValue(String dailyInAltValue) {
		this.dailyInAltValue = dailyInAltValue;
	}

	public String getDailyOutAltCount() {
		return dailyOutAltCount;
	}

	public void setDailyOutAltCount(String dailyOutAltCount) {
		this.dailyOutAltCount = dailyOutAltCount;
	}

	public String getDailyOutAltValue() {
		return dailyOutAltValue;
	}

	public void setDailyOutAltValue(String dailyOutAltValue) {
		this.dailyOutAltValue = dailyOutAltValue;
	}

	public String getWeeklyInAltCount() {
		return weeklyInAltCount;
	}

	public void setWeeklyInAltCount(String weeklyInAltCount) {
		this.weeklyInAltCount = weeklyInAltCount;
	}

	public String getWeeklyInAltValue() {
		return weeklyInAltValue;
	}

	public void setWeeklyInAltValue(String weeklyInAltValue) {
		this.weeklyInAltValue = weeklyInAltValue;
	}

	public String getWeeklyOutAltCount() {
		return weeklyOutAltCount;
	}

	public void setWeeklyOutAltCount(String weeklyOutAltCount) {
		this.weeklyOutAltCount = weeklyOutAltCount;
	}

	public String getWeeklyOutAltValue() {
		return weeklyOutAltValue;
	}

	public void setWeeklyOutAltValue(String weeklyOutAltValue) {
		this.weeklyOutAltValue = weeklyOutAltValue;
	}

	public String getDailySubscriberOutAltCount() {
		return dailySubscriberOutAltCount;
	}

	public void setDailySubscriberOutAltCount(String dailySubscriberOutAltCount) {
		this.dailySubscriberOutAltCount = dailySubscriberOutAltCount;
	}

	public String getWeeklySubscriberOutAltCount() {
		return weeklySubscriberOutAltCount;
	}

	public void setWeeklySubscriberOutAltCount(String weeklySubscriberOutAltCount) {
		this.weeklySubscriberOutAltCount = weeklySubscriberOutAltCount;
	}

	public String getMonthlySubscriberOutAltCount() {
		return monthlySubscriberOutAltCount;
	}

	public void setMonthlySubscriberOutAltCount(String monthlySubscriberOutAltCount) {
		this.monthlySubscriberOutAltCount = monthlySubscriberOutAltCount;
	}

	public String getDailySubscriberOutAltValue() {
		return dailySubscriberOutAltValue;
	}

	public void setDailySubscriberOutAltValue(String dailySubscriberOutAltValue) {
		this.dailySubscriberOutAltValue = dailySubscriberOutAltValue;
	}

	public String getWeeklySubscriberOutAltValue() {
		return weeklySubscriberOutAltValue;
	}

	public void setWeeklySubscriberOutAltValue(String weeklySubscriberOutAltValue) {
		this.weeklySubscriberOutAltValue = weeklySubscriberOutAltValue;
	}

	public String getMonthlySubscriberOutAltValue() {
		return monthlySubscriberOutAltValue;
	}

	public void setMonthlySubscriberOutAltValue(String monthlySubscriberOutAltValue) {
		this.monthlySubscriberOutAltValue = monthlySubscriberOutAltValue;
	}

	public String getMonthlyInAltCount() {
		return monthlyInAltCount;
	}

	public void setMonthlyInAltCount(String monthlyInAltCount) {
		this.monthlyInAltCount = monthlyInAltCount;
	}

	public String getMonthlyInAltValue() {
		return monthlyInAltValue;
	}

	public void setMonthlyInAltValue(String monthlyInAltValue) {
		this.monthlyInAltValue = monthlyInAltValue;
	}

	public String getMonthlyOutAltCount() {
		return monthlyOutAltCount;
	}

	public void setMonthlyOutAltCount(String monthlyOutAltCount) {
		this.monthlyOutAltCount = monthlyOutAltCount;
	}

	public String getMonthlyOutAltValue() {
		return monthlyOutAltValue;
	}

	public void setMonthlyOutAltValue(String monthlyOutAltValue) {
		this.monthlyOutAltValue = monthlyOutAltValue;
	}

	public String getUnctrlDailyInCount() {
		return unctrlDailyInCount;
	}

	public void setUnctrlDailyInCount(String unctrlDailyInCount) {
		this.unctrlDailyInCount = unctrlDailyInCount;
	}

	public String getUnctrlDailyInValue() {
		return unctrlDailyInValue;
	}

	public void setUnctrlDailyInValue(String unctrlDailyInValue) {
		this.unctrlDailyInValue = unctrlDailyInValue;
	}

	public String getUnctrlDailyOutCount() {
		return unctrlDailyOutCount;
	}

	public void setUnctrlDailyOutCount(String unctrlDailyOutCount) {
		this.unctrlDailyOutCount = unctrlDailyOutCount;
	}

	public String getUnctrlDailyOutValue() {
		return unctrlDailyOutValue;
	}

	public void setUnctrlDailyOutValue(String unctrlDailyOutValue) {
		this.unctrlDailyOutValue = unctrlDailyOutValue;
	}

	public String getUnctrlWeeklyInCount() {
		return unctrlWeeklyInCount;
	}

	public void setUnctrlWeeklyInCount(String unctrlWeeklyInCount) {
		this.unctrlWeeklyInCount = unctrlWeeklyInCount;
	}

	public String getUnctrlWeeklyInValue() {
		return unctrlWeeklyInValue;
	}

	public void setUnctrlWeeklyInValue(String unctrlWeeklyInValue) {
		this.unctrlWeeklyInValue = unctrlWeeklyInValue;
	}

	public String getUnctrlWeeklyOutCount() {
		return unctrlWeeklyOutCount;
	}

	public void setUnctrlWeeklyOutCount(String unctrlWeeklyOutCount) {
		this.unctrlWeeklyOutCount = unctrlWeeklyOutCount;
	}

	public String getUnctrlWeeklyOutValue() {
		return unctrlWeeklyOutValue;
	}

	public void setUnctrlWeeklyOutValue(String unctrlWeeklyOutValue) {
		this.unctrlWeeklyOutValue = unctrlWeeklyOutValue;
	}

	public String getUnctrlMonthlyInCount() {
		return unctrlMonthlyInCount;
	}

	public void setUnctrlMonthlyInCount(String unctrlMonthlyInCount) {
		this.unctrlMonthlyInCount = unctrlMonthlyInCount;
	}

	public String getUnctrlMonthlyInValue() {
		return unctrlMonthlyInValue;
	}

	public void setUnctrlMonthlyInValue(String unctrlMonthlyInValue) {
		this.unctrlMonthlyInValue = unctrlMonthlyInValue;
	}

	public String getUnctrlMonthlyOutCount() {
		return unctrlMonthlyOutCount;
	}

	public void setUnctrlMonthlyOutCount(String unctrlMonthlyOutCount) {
		this.unctrlMonthlyOutCount = unctrlMonthlyOutCount;
	}

	public String getUnctrlMonthlyOutValue() {
		return unctrlMonthlyOutValue;
	}

	public void setUnctrlMonthlyOutValue(String unctrlMonthlyOutValue) {
		this.unctrlMonthlyOutValue = unctrlMonthlyOutValue;
	}

	public String getUnctrlDailyInAltCount() {
		return unctrlDailyInAltCount;
	}

	public void setUnctrlDailyInAltCount(String unctrlDailyInAltCount) {
		this.unctrlDailyInAltCount = unctrlDailyInAltCount;
	}

	public String getUnctrlDailyInAltValue() {
		return unctrlDailyInAltValue;
	}

	public void setUnctrlDailyInAltValue(String unctrlDailyInAltValue) {
		this.unctrlDailyInAltValue = unctrlDailyInAltValue;
	}

	public String getUnctrlDailyOutAltCount() {
		return unctrlDailyOutAltCount;
	}

	public void setUnctrlDailyOutAltCount(String unctrlDailyOutAltCount) {
		this.unctrlDailyOutAltCount = unctrlDailyOutAltCount;
	}

	public String getUnctrlDailyOutAltValue() {
		return unctrlDailyOutAltValue;
	}

	public void setUnctrlDailyOutAltValue(String unctrlDailyOutAltValue) {
		this.unctrlDailyOutAltValue = unctrlDailyOutAltValue;
	}

	public String getUnctrlWeeklyInAltCount() {
		return unctrlWeeklyInAltCount;
	}

	public void setUnctrlWeeklyInAltCount(String unctrlWeeklyInAltCount) {
		this.unctrlWeeklyInAltCount = unctrlWeeklyInAltCount;
	}

	public String getUnctrlWeeklyInAltValue() {
		return unctrlWeeklyInAltValue;
	}

	public void setUnctrlWeeklyInAltValue(String unctrlWeeklyInAltValue) {
		this.unctrlWeeklyInAltValue = unctrlWeeklyInAltValue;
	}

	public String getUnctrlWeeklyOutAltCount() {
		return unctrlWeeklyOutAltCount;
	}

	public void setUnctrlWeeklyOutAltCount(String unctrlWeeklyOutAltCount) {
		this.unctrlWeeklyOutAltCount = unctrlWeeklyOutAltCount;
	}

	public String getUnctrlWeeklyOutAltValue() {
		return unctrlWeeklyOutAltValue;
	}

	public void setUnctrlWeeklyOutAltValue(String unctrlWeeklyOutAltValue) {
		this.unctrlWeeklyOutAltValue = unctrlWeeklyOutAltValue;
	}

	public String getUnctrlMonthlyInAltCount() {
		return unctrlMonthlyInAltCount;
	}

	public void setUnctrlMonthlyInAltCount(String unctrlMonthlyInAltCount) {
		this.unctrlMonthlyInAltCount = unctrlMonthlyInAltCount;
	}

	public String getUnctrlMonthlyInAltValue() {
		return unctrlMonthlyInAltValue;
	}

	public void setUnctrlMonthlyInAltValue(String unctrlMonthlyInAltValue) {
		this.unctrlMonthlyInAltValue = unctrlMonthlyInAltValue;
	}

	public String getUnctrlMonthlyOutAltCount() {
		return unctrlMonthlyOutAltCount;
	}

	public void setUnctrlMonthlyOutAltCount(String unctrlMonthlyOutAltCount) {
		this.unctrlMonthlyOutAltCount = unctrlMonthlyOutAltCount;
	}

	public String getUnctrlMonthlyOutAltValue() {
		return unctrlMonthlyOutAltValue;
	}

	public void setUnctrlMonthlyOutAltValue(String unctrlMonthlyOutAltValue) {
		this.unctrlMonthlyOutAltValue = unctrlMonthlyOutAltValue;
	}

	public String getDailySubscriberInCount() {
		return dailySubscriberInCount;
	}

	public void setDailySubscriberInCount(String dailySubscriberInCount) {
		this.dailySubscriberInCount = dailySubscriberInCount;
	}

	public String getWeeklySubscriberInCount() {
		return weeklySubscriberInCount;
	}

	public void setWeeklySubscriberInCount(String weeklySubscriberInCount) {
		this.weeklySubscriberInCount = weeklySubscriberInCount;
	}

	public String getMonthlySubscriberInCount() {
		return monthlySubscriberInCount;
	}

	public void setMonthlySubscriberInCount(String monthlySubscriberInCount) {
		this.monthlySubscriberInCount = monthlySubscriberInCount;
	}

	public String getDailySubscriberInValue() {
		return dailySubscriberInValue;
	}

	public void setDailySubscriberInValue(String dailySubscriberInValue) {
		this.dailySubscriberInValue = dailySubscriberInValue;
	}

	public String getWeeklySubscriberInValue() {
		return weeklySubscriberInValue;
	}

	public void setWeeklySubscriberInValue(String weeklySubscriberInValue) {
		this.weeklySubscriberInValue = weeklySubscriberInValue;
	}

	public String getMonthlySubscriberInValue() {
		return monthlySubscriberInValue;
	}

	public void setMonthlySubscriberInValue(String monthlySubscriberInValue) {
		this.monthlySubscriberInValue = monthlySubscriberInValue;
	}

	public String getDailySubscriberInAltCount() {
		return dailySubscriberInAltCount;
	}

	public void setDailySubscriberInAltCount(String dailySubscriberInAltCount) {
		this.dailySubscriberInAltCount = dailySubscriberInAltCount;
	}

	public String getWeeklySubscriberInAltCount() {
		return weeklySubscriberInAltCount;
	}

	public void setWeeklySubscriberInAltCount(String weeklySubscriberInAltCount) {
		this.weeklySubscriberInAltCount = weeklySubscriberInAltCount;
	}

	public String getMonthlySubscriberInAltCount() {
		return monthlySubscriberInAltCount;
	}

	public void setMonthlySubscriberInAltCount(String monthlySubscriberInAltCount) {
		this.monthlySubscriberInAltCount = monthlySubscriberInAltCount;
	}

	public String getDailySubscriberInAltValue() {
		return dailySubscriberInAltValue;
	}

	public void setDailySubscriberInAltValue(String dailySubscriberInAltValue) {
		this.dailySubscriberInAltValue = dailySubscriberInAltValue;
	}

	public String getWeeklySubscriberInAltValue() {
		return weeklySubscriberInAltValue;
	}

	public void setWeeklySubscriberInAltValue(String weeklySubscriberInAltValue) {
		this.weeklySubscriberInAltValue = weeklySubscriberInAltValue;
	}

	public String getMonthlySubscriberInAltValue() {
		return monthlySubscriberInAltValue;
	}

	public void setMonthlySubscriberInAltValue(String monthlySubscriberInAltValue) {
		this.monthlySubscriberInAltValue = monthlySubscriberInAltValue;
	}

	public List<MasterErrorList> validateFormData(Locale locale) {

		List<MasterErrorList> listOfErrors = new ArrayList<MasterErrorList>();

		final String shortname = shortName.trim();
		validateProductBalancelist(listOfErrors, locale);
		validationStepOne(listOfErrors, locale);
		validationSteptwo(listOfErrors, locale);
		validationStepthree(listOfErrors, locale);
		validationStepFour(listOfErrors, locale);
		validationstepfive(listOfErrors, locale);
		validationstepsix(listOfErrors, locale);
		validationStepSeven(listOfErrors, locale);
		validationstepeight(listOfErrors, locale);
		validationStepNine(listOfErrors, locale);
		validationStepTen(listOfErrors, locale);

		return listOfErrors;
	}

	private void validateProductBalancelist(List listOfErrors, Locale locale) {

		if ((productBalancelist != null) && (!productBalancelist.isEmpty())) {
			TransferProfileProductReqVO profileProductVO = null;
			double minBalance = -1;
			double maxBalance = -1;
			double c2sMinTxnAmt = -1;
			double c2sMaxTxnAmt = -1;
			double altBalance = -1;
			int allowedMaxPercentage = 0;
			for (int i = 0, j = productBalancelist.size(); i < j; i++) {
				profileProductVO = (TransferProfileProductReqVO) productBalancelist.get(i);
				minBalance = Double.parseDouble(profileProductVO.getMinBalance());
				maxBalance = Double.parseDouble(profileProductVO.getMaxBalance());
				if (minBalance > maxBalance) {
//                   errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profile.transferprofiledetail.minimax", new String[] { profileProductVO
//                       .getProductName() }));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.minimax");
					error.setErrorMsg(
							RestAPIStringParser.getMessage(locale, "error.profile.transferprofiledetail.minimax",
									new String[] { profileProductVO.getProductName() }));
					listOfErrors.add(error);
				}
				c2sMinTxnAmt = Double.parseDouble(profileProductVO.getC2sMinTxnAmt());
				c2sMaxTxnAmt = Double.parseDouble(profileProductVO.getC2sMaxTxnAmt());
				if (c2sMinTxnAmt > c2sMaxTxnAmt) {
//                   errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profile.transferprofiledetail.c2sminimaxtsnamt",
//                       new String[] { profileProductVO.getProductName() }));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.c2sminimaxtsnamt");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.c2sminimaxtsnamt",
							new String[] { profileProductVO.getProductName() }));
					listOfErrors.add(error);

				}
				altBalance = Double.parseDouble(profileProductVO.getAltBalance());
				if (altBalance < minBalance) {
//                   errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profile.transferprofiledetail.invalidminalertingbalance",
//                       new String[] { profileProductVO.getProductName() }));

					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.invalidminalertingbalance");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.invalidminalertingbalance",
							new String[] { profileProductVO.getProductName() }));
					listOfErrors.add(error);

				}
				if (altBalance >= maxBalance) {
//                   errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profile.transferprofiledetail.invalidmaxalertingbalance",
//                       new String[] { profileProductVO.getProductName() }));

					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.invalidmaxalertingbalance");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.invalidmaxalertingbalance",
							new String[] { profileProductVO.getProductName() }));
					listOfErrors.add(error);

				}
				allowedMaxPercentage = Integer.parseInt(profileProductVO.getAllowedMaxPercentage());
				if (allowedMaxPercentage <= 0 || allowedMaxPercentage > 100) {
//                   errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profile.transferprofiledetail.invalidpercentage",
//                       new String[] { profileProductVO.getProductName() }));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.invalidpercentage");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.invalidpercentage",
							new String[] { profileProductVO.getProductName() }));
					listOfErrors.add(error);
				}
			}
		}

	}

	private void validationStepOne(List listOfErrors, Locale locale) {

		if ((!BTSLUtil.isNullString(dailyInCount)) && (!BTSLUtil.isNullString(weeklyInCount))
				&& (!BTSLUtil.isNullString(monthlyInCount))) {
			final long dailyInCount1 = Long.parseLong(dailyInCount);
			final long weeklyInCount1 = Long.parseLong(weeklyInCount);
			final long monthlyInCount1 = Long.parseLong(monthlyInCount);
			if (dailyInCount1 > weeklyInCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyincount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyweeklyincount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyincount", null));
				listOfErrors.add(error);
			}
			if (dailyInCount1 > monthlyInCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyincount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyincount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyincount", null));
				listOfErrors.add(error);
			}
			if (weeklyInCount1 > monthlyInCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyincount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyincount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyincount", null));
				listOfErrors.add(error);
			}
		}
	}

	private void validationSteptwo(List listOfErrors, Locale locale) {
		// /////transfer in alerting count
		if ((!BTSLUtil.isNullString(dailyInAltCount)) && (!BTSLUtil.isNullString(weeklyInAltCount))
				&& (!BTSLUtil.isNullString(monthlyInAltCount))) {
			final long dailyInAltCount1 = Long.parseLong(dailyInAltCount);
			final long weeklyInAltCount1 = Long.parseLong(weeklyInAltCount);
			final long monthlyInAltCount1 = Long.parseLong(monthlyInAltCount);
			if (dailyInAltCount1 > weeklyInAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyinaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyweeklyinaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyinaltcount", null));
				listOfErrors.add(error);
			}
			if (dailyInAltCount1 > monthlyInAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyinaltcount"));

				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyinaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyinaltcount", null));
				listOfErrors.add(error);

			}
			if (weeklyInAltCount1 > monthlyInAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyinaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyinaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyinaltcount", null));
				listOfErrors.add(error);
			}

			if (dailyInAltCount1 > Long.parseLong(dailyInCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyalertcountinvalid"));//
				// /

				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyalertcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyalertcountinvalid", null));
				listOfErrors.add(error);

			}
			if (weeklyInAltCount1 > Long.parseLong(weeklyInCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklyalertcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklyalertcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklyalertcountinvalid", null));
				listOfErrors.add(error);

			}
			if (monthlyInAltCount1 > Long.parseLong(monthlyInCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlyalertcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlyalertcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlyalertcountinvalid", null));
				listOfErrors.add(error);

			}
		}

	}

	private void validationStepthree(List listOfErrors, Locale locale) {
		if ((!BTSLUtil.isNullString(dailyOutCount)) && (!BTSLUtil.isNullString(weeklyOutCount))
				&& (!BTSLUtil.isNullString(monthlyOutCount))) {
			final long dailyOutCount1 = Long.parseLong(dailyOutCount);
			final long weeklyOutCount1 = Long.parseLong(weeklyOutCount);
			final long monthlyOutCount1 = Long.parseLong(monthlyOutCount);
			if (dailyOutCount1 > weeklyOutCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyoutcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyweeklyoutcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyoutcount", null));
				listOfErrors.add(error);
			}
			if (dailyOutCount1 > monthlyOutCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyoutcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyoutcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyoutcount", null));
				listOfErrors.add(error);
			}
			if (weeklyOutCount1 > monthlyOutCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyoutcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyoutcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyoutcount", null));
				listOfErrors.add(error);
			}
		}

	}

	private void validationStepFour(List listOfErrors, Locale locale) {
		// ////////channel transfer out alerting count
		if ((!BTSLUtil.isNullString(dailyOutAltCount)) && (!BTSLUtil.isNullString(weeklyOutAltCount))
				&& (!BTSLUtil.isNullString(monthlyOutAltCount))) {
			final long dailyOutAltCount1 = Long.parseLong(dailyOutAltCount);
			final long weeklyOutAltCount1 = Long.parseLong(weeklyOutAltCount);
			final long monthlyOutAltCount1 = Long.parseLong(monthlyOutAltCount);

			if (dailyOutAltCount1 > weeklyOutAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyoutaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyoutcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyoutaltcount", null));
				listOfErrors.add(error);

			}
			if (dailyOutAltCount1 > monthlyOutAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyoutaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyoutaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyoutaltcount", null));
				listOfErrors.add(error);

			}
			if (weeklyOutAltCount1 > monthlyOutAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyoutaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyoutaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyoutaltcount", null));
				listOfErrors.add(error);
			}

			if (dailyOutAltCount1 > Long.parseLong(dailyOutCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailychannelalertcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailychannelalertcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailychannelalertcountinvalid", null));
				listOfErrors.add(error);

			}
			if (weeklyOutAltCount1 > Long.parseLong(weeklyOutCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklychannelalertcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklychannelalertcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklychannelalertcountinvalid", null));
				listOfErrors.add(error);

			}
			if (monthlyOutAltCount1 > Long.parseLong(monthlyOutCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlychannelalertcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlychannelalertcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlychannelalertcountinvalid", null));
				listOfErrors.add(error);

			}
		}
		if ((!BTSLUtil.isNullString(dailySubscriberOutCount)) && (!BTSLUtil.isNullString(weeklySubscriberOutCount))
				&& (!BTSLUtil.isNullString(monthlySubscriberOutCount))) {
			final long dailySubscriberOutCount1 = Long.parseLong(dailySubscriberOutCount);
			final long weeklySubscriberOutCount1 = Long.parseLong(weeklySubscriberOutCount);
			final long monthlySubscriberOutCount1 = Long.parseLong(monthlySubscriberOutCount);
			if (dailySubscriberOutCount1 > weeklySubscriberOutCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyoutcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyoutcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyoutcount", null));
				listOfErrors.add(error);
			}
			if (dailySubscriberOutCount1 > monthlySubscriberOutCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyoutcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyoutcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyoutcount", null));
				listOfErrors.add(error);

			}
			if (weeklySubscriberOutCount1 > monthlySubscriberOutCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyoutcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyoutcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyoutcount", null));
				listOfErrors.add(error);
			}
		}

	}

	private void validationstepfive(List listOfErrors, Locale locale) {
		// ////////////Subscriber Alerting Count

		if ((!BTSLUtil.isNullString(dailySubscriberOutAltCount))
				&& (!BTSLUtil.isNullString(weeklySubscriberOutAltCount))
				&& (!BTSLUtil.isNullString(monthlySubscriberOutAltCount))) {
			final long dailySubscriberOutAltCount1 = Long.parseLong(dailySubscriberOutAltCount);
			final long weeklySubscriberOutAltCount1 = Long.parseLong(weeklySubscriberOutAltCount);
			final long monthlySubscriberOutAltCount1 = Long.parseLong(monthlySubscriberOutAltCount);
			if (dailySubscriberOutAltCount1 > weeklySubscriberOutAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyoutaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyoutaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyoutaltcount", null));
				listOfErrors.add(error);

			}
			if (dailySubscriberOutAltCount1 > monthlySubscriberOutAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyoutaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyoutaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyoutaltcount", null));
				listOfErrors.add(error);
			}
			if (weeklySubscriberOutAltCount1 > monthlySubscriberOutAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltcount", null));
				listOfErrors.add(error);

			}

			if (dailySubscriberOutAltCount1 > Long.parseLong(dailySubscriberOutCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailysubscriberaltcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailysubscriberaltcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailysubscriberaltcountinvalid", null));
				listOfErrors.add(error);

			}
			if (weeklySubscriberOutAltCount1 > Long.parseLong(weeklySubscriberOutCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklysubscriberaltcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklysubscriberaltcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklysubscriberaltcountinvalid", null));
				listOfErrors.add(error);
			}
			if (monthlySubscriberOutAltCount1 > Long.parseLong(monthlySubscriberOutCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlysubscriberaltcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlysubscriberaltcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlysubscriberaltcountinvalid", null));
				listOfErrors.add(error);
			}
		}

		if ((!BTSLUtil.isNullString(dailyInValue)) && (!BTSLUtil.isNullString(weeklyInValue))
				&& (!BTSLUtil.isNullString(monthlyInValue))) {
			final double dailyInValue1 = Double.parseDouble(dailyInValue);
			final double weeklyInValue1 = Double.parseDouble(weeklyInValue);
			final double monthlyInValue1 = Double.parseDouble(monthlyInValue);
			if (dailyInValue1 > weeklyInValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyinvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyweeklyinvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyinvalue", null));
				listOfErrors.add(error);
			}
			if (dailyInValue1 > monthlyInValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyinvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyinvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyinvalue", null));
				listOfErrors.add(error);
			}
			if (weeklyInValue1 > monthlyInValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyinvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyinvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyinvalue", null));
				listOfErrors.add(error);
			}
		}

	}

	private void validationstepsix(List listOfErrors, Locale locale) {
		// /////Transfer In Alerting Value

		if ((!BTSLUtil.isNullString(dailyInAltValue)) && (!BTSLUtil.isNullString(weeklyInAltValue))
				&& (!BTSLUtil.isNullString(monthlyInAltValue))) {
			final double dailyInAltValue1 = Double.parseDouble(dailyInAltValue);
			final double weeklyInAltValue1 = Double.parseDouble(weeklyInAltValue);
			final double monthlyInAltValue1 = Double.parseDouble(monthlyInAltValue);

			if (dailyInAltValue1 > weeklyInAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyinaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyweeklyinaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyinaltvalue", null));
				listOfErrors.add(error);

			}
			if (dailyInAltValue1 > monthlyInAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyinaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyinaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyinaltvalue", null));
				listOfErrors.add(error);
			}
			if (weeklyInAltValue1 > monthlyInAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyinaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyinaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyinaltvalue", null));
				listOfErrors.add(error);
			}

			if (dailyInAltValue1 > Double.parseDouble(dailyInValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyaltvalueinvalid", null));
				listOfErrors.add(error);

			}
			if (weeklyInAltValue1 > Double.parseDouble(weeklyInValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklyaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklyaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklyaltvalueinvalid", null));
				listOfErrors.add(error);

			}
			if (monthlyInAltValue1 > Double.parseDouble(monthlyInValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlyaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlyaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlyaltvalueinvalid", null));
				listOfErrors.add(error);
			}
		}

		if ((!BTSLUtil.isNullString(dailyOutValue)) && (!BTSLUtil.isNullString(weeklyOutValue))
				&& (!BTSLUtil.isNullString(monthlyOutValue))) {
			final double dailyOutValue1 = Double.parseDouble(dailyOutValue);
			final double weeklyOutValue1 = Double.parseDouble(weeklyOutValue);
			final double monthlyOutValue1 = Double.parseDouble(monthlyOutValue);
			if (dailyOutValue1 > weeklyOutValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyoutvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyweeklyoutvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyoutvalue", null));
				listOfErrors.add(error);
			}
			if (dailyOutValue1 > monthlyOutValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyoutvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyoutvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyoutvalue", null));
				listOfErrors.add(error);
			}
			if (weeklyOutValue1 > monthlyOutValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyoutvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyoutvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyoutvalue", null));
				listOfErrors.add(error);
			}
		}

	}

	private void validationStepSeven(List listOfErrors, Locale locale) {
		// /channel out alerting value
		if ((!BTSLUtil.isNullString(dailyOutAltValue)) && (!BTSLUtil.isNullString(weeklyOutAltValue))
				&& (!BTSLUtil.isNullString(monthlyOutAltValue))) {
			final double dailyOutAltValue1 = Double.parseDouble(dailyOutAltValue);
			final double weeklyOutAltValue1 = Double.parseDouble(weeklyOutAltValue);
			final double monthlyOutAltValue1 = Double.parseDouble(monthlyOutAltValue);
			if (dailyOutAltValue1 > weeklyOutAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailyweeklyoutaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailyweeklyoutaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailyweeklyoutaltvalue", null));
				listOfErrors.add(error);
			}
			if (dailyOutAltValue1 > monthlyOutAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailymonthlyoutaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailymonthlyoutaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailymonthlyoutaltvalue", null));
				listOfErrors.add(error);
			}
			if (weeklyOutAltValue1 > monthlyOutAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklymonthlyoutaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklymonthlyoutaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklymonthlyoutaltvalue", null));
				listOfErrors.add(error);
			}

			if (dailyOutAltValue1 > Double.parseDouble(dailyOutValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailychannelalertvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailychannelalertvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailychannelalertvalueinvalid", null));
				listOfErrors.add(error);
			}
			if (weeklyOutAltValue1 > Double.parseDouble(weeklyOutValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklychannelalertvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklychannelalertvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklychannelalertvalueinvalid", null));
				listOfErrors.add(error);

			}
			if (monthlyOutAltValue1 > Double.parseDouble(monthlyOutValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlychannelalertvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlychannelalertvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlychannelalertvalueinvalid", null));
				listOfErrors.add(error);
			}
		}
		if ((!BTSLUtil.isNullString(dailySubscriberOutValue)) && (!BTSLUtil.isNullString(weeklySubscriberOutValue))
				&& (!BTSLUtil.isNullString(monthlySubscriberOutValue))) {
			final double dailySubscriberOutValue1 = Double.parseDouble(dailySubscriberOutValue);
			final double weeklySubscriberOutValue1 = Double.parseDouble(weeklySubscriberOutValue);
			final double monthlySubscriberOutValue1 = Double.parseDouble(monthlySubscriberOutValue);
			if (dailySubscriberOutValue1 > weeklySubscriberOutValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyoutvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyoutvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyoutvalue", null));
				listOfErrors.add(error);

			}
			if (dailySubscriberOutValue1 > monthlySubscriberOutValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyoutvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyoutvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyoutvalue", null));
				listOfErrors.add(error);
			}
			if (weeklySubscriberOutValue1 > monthlySubscriberOutValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyoutvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyoutvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyoutvalue", null));
				listOfErrors.add(error);

			}
		}

	}

	private void validationstepeight(List listOfErrors, Locale locale) {
///Subscriber Alerting Value
		if ((!BTSLUtil.isNullString(dailySubscriberOutAltValue))
				&& (!BTSLUtil.isNullString(weeklySubscriberOutAltValue))
				&& (!BTSLUtil.isNullString(monthlySubscriberOutAltValue))) {
			final double dailySubscriberOutAltValue1 = Double.parseDouble(dailySubscriberOutAltValue);
			final double weeklySubscriberOutAltValue1 = Double.parseDouble(weeklySubscriberOutAltValue);
			final double monthlySubscriberOutAltValue1 = Double.parseDouble(monthlySubscriberOutAltValue);
			if (dailySubscriberOutAltValue1 > weeklySubscriberOutAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyoutaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyoutaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyoutaltvalue", null));
				listOfErrors.add(error);
			}
			if (dailySubscriberOutAltValue1 > monthlySubscriberOutAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyoutaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyoutaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyoutaltvalue", null));
				listOfErrors.add(error);
			}
			if (weeklySubscriberOutAltValue1 > monthlySubscriberOutAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltvalue", null));
				listOfErrors.add(error);
			}

			if (dailySubscriberOutAltValue1 > Double.parseDouble(dailySubscriberOutValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailysubscriberaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailysubscriberaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailysubscriberaltvalueinvalid", null));
				listOfErrors.add(error);
			}
			if (weeklySubscriberOutAltValue1 > Double.parseDouble(weeklySubscriberOutValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklysubscriberaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklysubscriberaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklysubscriberaltvalueinvalid", null));
				listOfErrors.add(error);
			}
			if (monthlySubscriberOutAltValue1 > Double.parseDouble(monthlySubscriberOutValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlysubscriberaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlysubscriberaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlysubscriberaltvalueinvalid", null));
				listOfErrors.add(error);
			}
		}
	}

	private void validationStepNine(List listOfErrors, Locale locale) {
		// 6.4 changes
		if ((!BTSLUtil.isNullString(dailySubscriberInCount)) && (!BTSLUtil.isNullString(weeklySubscriberInCount))
				&& (!BTSLUtil.isNullString(monthlySubscriberInCount))) {
			final long dailySubscriberInCount1 = Long.parseLong(dailySubscriberInCount);
			final long weeklySubscriberInCount1 = Long.parseLong(weeklySubscriberInCount);
			final long monthlySubscriberInCount1 = Long.parseLong(monthlySubscriberInCount);
			if (dailySubscriberInCount1 > weeklySubscriberInCount1) {

				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyincount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyincount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyincount", null));
				listOfErrors.add(error);
			}
			if (dailySubscriberInCount1 > monthlySubscriberInCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyincount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyincount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyincount", null));
				listOfErrors.add(error);

			}
			if (weeklySubscriberInCount1 > monthlySubscriberInCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyincount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyincount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyincount", null));
				listOfErrors.add(error);
			}
		}

		if ((!BTSLUtil.isNullString(dailySubscriberInAltCount)) && (!BTSLUtil.isNullString(weeklySubscriberInAltCount))
				&& (!BTSLUtil.isNullString(monthlySubscriberInAltCount))) {
			final long dailySubscriberInAltCount1 = Long.parseLong(dailySubscriberInAltCount);
			final long weeklySubscriberInAltCount1 = Long.parseLong(weeklySubscriberInAltCount);
			final long monthlySubscriberInAltCount1 = Long.parseLong(monthlySubscriberInAltCount);
			if (dailySubscriberInAltCount1 > weeklySubscriberInAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyinaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyinaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyinaltcount", null));
				listOfErrors.add(error);
			}
			if (dailySubscriberInAltCount1 > monthlySubscriberInAltCount1) {
				// 0errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyinaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyinaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyinaltcount", null));
				listOfErrors.add(error);

			}
			if (weeklySubscriberInAltCount1 > monthlySubscriberInAltCount1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyinaltcount"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyinaltcount");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyinaltcount", null));
				listOfErrors.add(error);
			}

			if (dailySubscriberInAltCount1 > Long.parseLong(dailySubscriberInCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailysubscriberinaltcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailysubscriberinaltcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailysubscriberinaltcountinvalid", null));
				listOfErrors.add(error);

			}
			if (weeklySubscriberInAltCount1 > Long.parseLong(weeklySubscriberInCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.weeklysubscriberinaltcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklysubscriberinaltcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklysubscriberinaltcountinvalid", null));
				listOfErrors.add(error);
			}
			if (monthlySubscriberInAltCount1 > Long.parseLong(monthlySubscriberInCount)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlysubscriberinaltcountinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlysubscriberinaltcountinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlysubscriberinaltcountinvalid", null));
				listOfErrors.add(error);
			}
		}

		if ((!BTSLUtil.isNullString(dailySubscriberInValue)) && (!BTSLUtil.isNullString(weeklySubscriberInValue))
				&& (!BTSLUtil.isNullString(monthlySubscriberInValue))) {
			final double dailySubscriberInValue1 = Double.parseDouble(dailySubscriberInValue);
			final double weeklySubscriberInValue1 = Double.parseDouble(weeklySubscriberInValue);
			final double monthlySubscriberInValue1 = Double.parseDouble(monthlySubscriberInValue);
			if (dailySubscriberInValue1 > weeklySubscriberInValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyinvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyinvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyinvalue", null));
				listOfErrors.add(error);
			}
			if (dailySubscriberInValue1 > monthlySubscriberInValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyinvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyinvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyinvalue", null));
				listOfErrors.add(error);
			}
			if (weeklySubscriberInValue1 > monthlySubscriberInValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyinvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyinvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyinvalue", null));
				listOfErrors.add(error);
			}
		}

		if ((!BTSLUtil.isNullString(dailySubscriberInAltValue)) && (!BTSLUtil.isNullString(weeklySubscriberInAltValue))
				&& (!BTSLUtil.isNullString(monthlySubscriberInAltValue))) {
			final double dailySubscriberInAltValue1 = Double.parseDouble(dailySubscriberInAltValue);
			final double weeklySubscriberInAltValue1 = Double.parseDouble(weeklySubscriberInAltValue);
			final double monthlySubscriberInAltValue1 = Double.parseDouble(monthlySubscriberInAltValue);
			if (dailySubscriberInAltValue1 > weeklySubscriberInAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailyweeklyinaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailyweeklyinaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailyweeklyinaltvalue", null));
				listOfErrors.add(error);
			}
			if (dailySubscriberInAltValue1 > monthlySubscriberInAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberdailymonthlyinaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberdailymonthlyinaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberdailymonthlyinaltvalue", null));
				listOfErrors.add(error);
			}
			if (weeklySubscriberInAltValue1 > monthlySubscriberInAltValue1) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.subscriberweeklymonthlyinaltvalue"));
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.subscriberweeklymonthlyinaltvalue");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.subscriberweeklymonthlyinaltvalue", null));
				listOfErrors.add(error);

			}

			if (dailySubscriberInAltValue1 > Double.parseDouble(dailySubscriberInValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.dailysubscriberinaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.dailysubscriberinaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.dailysubscriberinaltvalueinvalid", null));
				listOfErrors.add(error);
			}
			if (weeklySubscriberInAltValue1 > Double.parseDouble(weeklySubscriberInValue)) {
///            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.profile.transferprofiledetail.weeklysubscriberinaltvalueinvalid"));// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.weeklysubscriberinaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.weeklysubscriberinaltvalueinvalid", null));
				listOfErrors.add(error);

			}
			if (monthlySubscriberInAltValue1 > Double.parseDouble(monthlySubscriberInValue)) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("error.profile.transferprofiledetail.monthlysubscriberinaltvalueinvalid"));//
				// /
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode("error.profile.transferprofiledetail.monthlysubscriberinaltvalueinvalid");
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						"error.profile.transferprofiledetail.monthlysubscriberinaltvalueinvalid", null));
				listOfErrors.add(error);
			}
		}

	}

	private void validationStepTen(List listOfErrors, Locale locale) {
//uncontrol flag

		if (unctrlTransferFlag.equals("Y")) {
			if ((!BTSLUtil.isNullString(unctrlDailyInCount)) && (!BTSLUtil.isNullString(unctrlWeeklyInCount))
					&& (!BTSLUtil.isNullString(unctrlMonthlyInCount))) {
				final long dailyInCount1 = Long.parseLong(unctrlDailyInCount);
				final long weeklyInCount1 = Long.parseLong(unctrlWeeklyInCount);
				final long monthlyInCount1 = Long.parseLong(unctrlMonthlyInCount);
				if (dailyInCount1 > weeklyInCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyincount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyincount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyincount", null));
					listOfErrors.add(error);

				}
				if (dailyInCount1 > monthlyInCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyincount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailymonthlyincount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailymonthlyincount", null));
					listOfErrors.add(error);

				}
				if (weeklyInCount1 > monthlyInCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyincount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyincount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyincount", null));
					listOfErrors.add(error);
				}
			}

			// /unctrl transfer in alerting count

			if ((!BTSLUtil.isNullString(unctrlDailyInAltCount)) && (!BTSLUtil.isNullString(unctrlWeeklyInAltCount))
					&& (!BTSLUtil.isNullString(unctrlMonthlyInAltCount))) {
				final long dailyInAltCount1 = Long.parseLong(unctrlDailyInAltCount);
				final long weeklyInAltCount1 = Long.parseLong(unctrlWeeklyInAltCount);
				final long monthlyInAltCount1 = Long.parseLong(unctrlMonthlyInAltCount);
				if (dailyInAltCount1 > weeklyInAltCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyinaltcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyinaltcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyinaltcount", null));
					listOfErrors.add(error);
				}
				if (dailyInAltCount1 > monthlyInAltCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyinaltcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailymonthlyinaltcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailymonthlyinaltcount", null));
					listOfErrors.add(error);

				}
				if (weeklyInAltCount1 > monthlyInAltCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyinaltcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyinaltcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyinaltcount", null));
					listOfErrors.add(error);
				}

				if (dailyInAltCount1 > Long.parseLong(unctrlDailyInCount)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.dailyaltcountinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.dailyaltcountinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.dailyaltcountinvalid", null));
					listOfErrors.add(error);

				}
				if (weeklyInAltCount1 > Long.parseLong(unctrlWeeklyInCount)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.weeklyaltcountinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.weeklyaltcountinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.weeklyaltcountinvalid", null));
					listOfErrors.add(error);
				}
				if (monthlyInAltCount1 > Long.parseLong(unctrlMonthlyInCount)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.monthlyaltcountinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.monthlyaltcountinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.monthlyaltcountinvalid", null));
					listOfErrors.add(error);
				}
			}

			if ((!BTSLUtil.isNullString(unctrlDailyOutCount)) && (!BTSLUtil.isNullString(unctrlWeeklyOutCount))
					&& (!BTSLUtil.isNullString(unctrlMonthlyOutCount))) {
				final long dailyOutCount1 = Long.parseLong(unctrlDailyOutCount);
				final long weeklyOutCount1 = Long.parseLong(unctrlWeeklyOutCount);
				final long monthlyOutCount1 = Long.parseLong(unctrlMonthlyOutCount);
				if (dailyOutCount1 > weeklyOutCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyoutcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyoutcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyoutcount", null));
					listOfErrors.add(error);

				}
				if (dailyOutCount1 > monthlyOutCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyoutcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailymonthlyoutcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailymonthlyoutcount", null));
					listOfErrors.add(error);
				}
				if (weeklyOutCount1 > monthlyOutCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyoutcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyoutcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyoutcount", null));
					listOfErrors.add(error);
				}
			}
			// /// unctrl channel transfer out alert Count
			if ((!BTSLUtil.isNullString(unctrlDailyOutAltCount)) && (!BTSLUtil.isNullString(unctrlWeeklyOutAltCount))
					&& (!BTSLUtil.isNullString(unctrlMonthlyOutAltCount))) {
				final long dailyOutAltCount1 = Long.parseLong(unctrlDailyOutAltCount);
				final long weeklyOutAltCount1 = Long.parseLong(unctrlWeeklyOutAltCount);
				final long monthlyOutAltCount1 = Long.parseLong(unctrlMonthlyOutAltCount);
				if (dailyOutAltCount1 > weeklyOutAltCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyoutaltcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyoutaltcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyoutaltcount", null));
					listOfErrors.add(error);
				}
				if (dailyOutAltCount1 > monthlyOutAltCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyoutaltcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailymonthlyoutaltcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailymonthlyoutaltcount", null));
					listOfErrors.add(error);

				}
				if (weeklyOutAltCount1 > monthlyOutAltCount1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyoutaltcount"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyoutaltcount");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyoutaltcount", null));
					listOfErrors.add(error);
				}
				if (dailyOutAltCount1 > Long.parseLong(unctrlDailyOutCount)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.dailychannelaltcountinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.dailychannelaltcountinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.dailychannelaltcountinvalid", null));
					listOfErrors.add(error);

				}
				if (weeklyOutAltCount1 > Long.parseLong(unctrlWeeklyOutCount)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.weeklychannelaltcountinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.weeklychannelaltcountinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.weeklychannelaltcountinvalid", null));
					listOfErrors.add(error);
				}
				if (monthlyOutAltCount1 > Long.parseLong(unctrlMonthlyOutCount)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.monthlychannelaltcountinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.monthlychannelaltcountinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.monthlychannelaltcountinvalid", null));
					listOfErrors.add(error);

				}
			}

			if ((!BTSLUtil.isNullString(unctrlDailyInValue)) && (!BTSLUtil.isNullString(unctrlWeeklyInValue))
					&& (!BTSLUtil.isNullString(unctrlMonthlyInValue))) {
				final double dailyInValue1 = Double.parseDouble(unctrlDailyInValue);
				final double weeklyInValue1 = Double.parseDouble(unctrlWeeklyInValue);
				final double monthlyInValue1 = Double.parseDouble(unctrlMonthlyInValue);
				if (dailyInValue1 > weeklyInValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyinvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyinvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyinvalue", null));
					listOfErrors.add(error);
				}
				if (dailyInValue1 > monthlyInValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyinvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailymonthlyinvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailymonthlyinvalue", null));
					listOfErrors.add(error);

				}
				if (weeklyInValue1 > monthlyInValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyinvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyinvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyinvalue", null));
					listOfErrors.add(error);

				}
			}
			// //unctrl Transfer in alerting value
			if ((!BTSLUtil.isNullString(unctrlDailyInAltValue)) && (!BTSLUtil.isNullString(unctrlWeeklyInAltValue))
					&& (!BTSLUtil.isNullString(unctrlMonthlyInAltValue))) {
				final double dailyInAltValue1 = Double.parseDouble(unctrlDailyInAltValue);
				final double weeklyInAltValue1 = Double.parseDouble(unctrlWeeklyInAltValue);
				final double monthlyInAltValue1 = Double.parseDouble(unctrlMonthlyInAltValue);
				if (dailyInAltValue1 > weeklyInAltValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyinaltvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyinaltvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyinaltvalue", null));
					listOfErrors.add(error);
				}
				if (dailyInAltValue1 > monthlyInAltValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyinaltvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyinaltvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyinaltvalue", null));
					listOfErrors.add(error);
				}
				if (weeklyInAltValue1 > monthlyInAltValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyinaltvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyinaltvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyinaltvalue", null));
					listOfErrors.add(error);
				}
				if (dailyInAltValue1 > Double.parseDouble(unctrlDailyInValue)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyaltvalueinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyaltvalueinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyaltvalueinvalid", null));
					listOfErrors.add(error);

				}
				if (weeklyInAltValue1 > Double.parseDouble(unctrlWeeklyInValue)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklyaltvalueinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklyaltvalueinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklyaltvalueinvalid", null));
					listOfErrors.add(error);

				}
				if (monthlyInAltValue1 > Double.parseDouble(unctrlMonthlyInValue)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidemonthlyaltvalueinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklyaltvalueinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklyaltvalueinvalid", null));
					listOfErrors.add(error);
				}
			}
			if ((!BTSLUtil.isNullString(unctrlDailyOutValue)) && (!BTSLUtil.isNullString(unctrlWeeklyOutValue))
					&& (!BTSLUtil.isNullString(unctrlMonthlyOutValue))) {
				final double dailyOutValue1 = Double.parseDouble(unctrlDailyOutValue);
				final double weeklyOutValue1 = Double.parseDouble(unctrlWeeklyOutValue);
				final double monthlyOutValue1 = Double.parseDouble(unctrlMonthlyOutValue);
				if (dailyOutValue1 > weeklyOutValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyoutvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailyweeklyoutvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailyweeklyoutvalue", null));
					listOfErrors.add(error);
				}
				if (dailyOutValue1 > monthlyOutValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyoutvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailymonthlyoutvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailymonthlyoutvalue", null));
					listOfErrors.add(error);
				}
				if (weeklyOutValue1 > monthlyOutValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyoutvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyoutvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyoutvalue", null));
					listOfErrors.add(error);
				}
			}

			// //unctrl channel transferout alerting value
			if ((!BTSLUtil.isNullString(unctrlDailyOutAltValue)) && (!BTSLUtil.isNullString(unctrlWeeklyOutAltValue))
					&& (!BTSLUtil.isNullString(unctrlMonthlyOutAltValue))) {
				final double dailyOutAltValue1 = Double.parseDouble(unctrlDailyOutAltValue);
				final double weeklyOutAltValue1 = Double.parseDouble(unctrlWeeklyOutAltValue);
				final double monthlyOutAltValue1 = Double.parseDouble(unctrlMonthlyOutAltValue);
				if (dailyOutAltValue1 > weeklyOutAltValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailyweeklyoutaltvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyoutvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyoutvalue", null));
					listOfErrors.add(error);
				}
				if (dailyOutAltValue1 > monthlyOutAltValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailymonthlyoutaltvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailymonthlyoutaltvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailymonthlyoutaltvalue", null));
					listOfErrors.add(error);
				}
				if (weeklyOutAltValue1 > monthlyOutAltValue1) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklymonthlyoutaltvalue"));
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklymonthlyoutaltvalue");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklymonthlyoutaltvalue", null));
					listOfErrors.add(error);
				}
				if (dailyOutAltValue1 > Double.parseDouble(unctrlDailyOutValue)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidedailychannelalertvalueinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidedailychannelalertvalueinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidedailychannelalertvalueinvalid", null));
					listOfErrors.add(error);
				}
				if (weeklyOutAltValue1 > Double.parseDouble(unctrlWeeklyOutValue)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsideweeklychannelalertvalueinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsideweeklychannelalertvalueinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsideweeklychannelalertvalueinvalid", null));
					listOfErrors.add(error);

				}
				if (monthlyOutAltValue1 > Double.parseDouble(unctrlMonthlyOutValue)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("error.profile.transferprofiledetail.outsidemonthlychannelalertvalueinvalid"));//
					// /
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode("error.profile.transferprofiledetail.outsidemonthlychannelalertvalueinvalid");
					error.setErrorMsg(RestAPIStringParser.getMessage(locale,
							"error.profile.transferprofiledetail.outsidemonthlychannelalertvalueinvalid", null));
					listOfErrors.add(error);
				}
			}

		}

	}

	public boolean isSubscriberOutCountFlag() {
		return subscriberOutCountFlag;
	}

	public void setSubscriberOutCountFlag(boolean subscriberOutCountFlag) {
		this.subscriberOutCountFlag = subscriberOutCountFlag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProfileID() {
		return profileID;
	}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
