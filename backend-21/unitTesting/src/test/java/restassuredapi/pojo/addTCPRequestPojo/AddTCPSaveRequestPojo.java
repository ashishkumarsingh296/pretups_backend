package restassuredapi.pojo.addTCPRequestPojo;

import java.util.List;

public class AddTCPSaveRequestPojo {

	public AddTCPSaveRequestPojo() {

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

	public String getProfileID() {
		return profileID;
	}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
