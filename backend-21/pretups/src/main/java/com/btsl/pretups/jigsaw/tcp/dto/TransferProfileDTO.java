package com.btsl.pretups.jigsaw.tcp.dto;

public class TransferProfileDTO {
	private String profileId;
	/*private String shortName;
	private String profileName;
	*/
	private String status;
	/*private String description;
	private String dailyTransferInCount;
	private String dailyTransferInValue;
	private String weeklyTransferInCount;
	private String weeklyTransferInValue;
	private String monthlyTransferInCount;
	private String monthlyTransferInValue;
	private String dailyTransferOutCount;
	private String failyTransferOutValue;
	private String weeklyTransferOutCount;
	private String weeklyTransferOutValue;
	private String monthlyTransferOutCount;
	private String monthlyTransferOutValue;
	private String outsideDailyInCount;
	private String outsideDailyInValue;
	private String outsideWeeklyInCount;
	private String outsideWeeklyInValue;
	private String outsideMonthlyInCount;
	private String outsideMonthlyInValue;
	private String outsideDailyOutCount;
	private String outsideDailyOutValue;
	private String outsideWeeklyOutCount;
	private String outsideWeeklyOutValue;
	private String outsideMonthlyOutCount;
	private String outsideMonthlyOutValue;
	private String createdBy;
	private String createdOn;
	private String modifiedBy;
	private String modifiedOn;
	*/
	private String networkCode;
	private String categoryCode;
	/*private String dailySubscriberOutCount;
	private String dailySubscriberOutValue;
	private String weeklySubscriberOutCount;
	private String weeklySubscriberOutValue;
	private String monthlySubscriberOutCount;
	private String monthlySubscriberOutValue;
	private String altDailyTransferInCount;
	private String altDailyTransferInValue;
	private String altDailyWeeklyInCount;
	private String altDailyWeeklyInValue;
	private String altDailyMonthlyInCount;
	private String altDailyMonthlyInValue;
	private String altDailyTransferOutCount;
	private String altDailyTransferOutValue;
	private String altWeeklyTransferOutCount;
	private String altWeeklyTransferOutValue;
	private String altMonthlyTransferOutCount;
	private String altMonthlyTransferOutValue;
	private String altOutsideDailyOutCount;
	private String altOutsideDailyOutValue;
	private String altOutsideWeeklyInCount;
	private String altOutsideWeeklyInValue;
	private String altOutsideMonthlyInCount;
	private String altOutsideMonthlyInValue;
	private String altOutsidedailyOutCount;
	private String altOutsidedailyOutValue;
	private String altOutsideWeeklyOutCount;
	private String altOutsideWeeklyOutValue;
	private String altOutsideMonthlyOutCount;
	private String altOutsideMonthlyOutValue;
	private String altDailySubsOutCount;
	private String altDailySubsOutValue;
	private String altWeeklySubsOutCount;
	private String altWeeklySubsOutValue;
	private String altMonthlySubsOutCount;
	private String altMonthlySubsOutValue;
	*/
	private String parentProfileId;
	/*private String isDefault;
	private String dailySubsInCount;
	private String dailySubsInValue;
	private String weeklySubsInCount;
	private String weeklySubsInValue;
	private String monthlySubsInCount;
	private String monthlySubsInValue;
	private String altDailySubsInCount;
	private String altDailySubsInValue;
	private String altWeeklySubsInCount;
	private String altWeeklySubsInValue;
	private String altMonthlySubsInCount;
	private String altMonthlySubsInValue;
	*/
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getParentProfileId() {
		return parentProfileId;
	}
	public void setParentProfileId(String parentProfileId) {
		this.parentProfileId = parentProfileId;
	}
	
	
}
