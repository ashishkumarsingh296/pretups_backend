package com.restapi.superadmin.requestVO;

import java.util.ArrayList;

import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;

public class CatTrfProfileRequestVO {
	private ArrayList<TransferProfileProductVO> productBalanceList;
	private String profileId;
	private String networkName;
	private String profileName;
	private String shortName;
	private String description;
	private String status;
	private String dailyInCount;
    private String dailyInValue;
    private String dailyOutCount;
    private String dailyOutValue;
    private String networkCode;
    private String category;

    private String weeklyInCount;
    private String weeklyInValue;
    private String weeklyOutCount;
    private String weeklyOutValue;
    
    private String dailySubscriberOutCount;
    private String weeklySubscriberOutCount;
    private String monthlySubscriberOutCount;

    private String dailySubscriberOutValue;
    private String weeklySubscriberOutValue;
    private String monthlySubscriberOutValue;

    private String monthlyInCount;
    private String monthlyInValue;
    private String monthlyOutCount;
    private String monthlyOutValue;
    
    private String dailyInAltCount;
    private String dailyInAltValue;
    private String dailyOutAltCount;
    private String dailyOutAltValue;

    private String weeklyInAltCount;
    private String weeklyInAltValue;
    private String weeklyOutAltCount;
    private String weeklyOutAltValue;
    
    private String monthlyInAltCount;
    private String monthlyInAltValue;
    private String monthlyOutAltCount;
    private String monthlyOutAltValue;

    private String dailySubscriberOutAltCount;
    private String weeklySubscriberOutAltCount;
    private String monthlySubscriberOutAltCount;

    private String dailySubscriberOutAltValue;
    private String weeklySubscriberOutAltValue;
    private String monthlySubscriberOutAltValue;

    private String dailySubscriberInCount;
    private String weeklySubscriberInCount;
    private String monthlySubscriberInCount;

    private String dailySubscriberInValue;
    private String weeklySubscriberInValue;
    private String monthlySubscriberInValue;

    private String dailySubscriberInAltCount;
    private String weeklySubscriberInAltCount;
    private String monthlySubscriberInAltCount;

    private String dailySubscriberInAltValue;
    private String weeklySubscriberInAltValue;
    private String monthlySubscriberInAltValue;

    private String unctrlDailyInAltCount;
    private String unctrlDailyInAltValue;
    private String unctrlDailyOutAltCount;
    private String unctrlDailyOutAltValue;

    private String unctrlWeeklyInAltCount;
    private String unctrlWeeklyInAltValue;
    private String unctrlWeeklyOutAltCount;
    private String unctrlWeeklyOutAltValue;
    
    private String unctrlMonthlyInAltCount;
    private String unctrlMonthlyInAltValue;
    private String unctrlMonthlyOutAltCount;
    private String unctrlMonthlyOutAltValue;
    
    
	private long lastModifiedTime;
    private String parentProfileID;

    private boolean isDefaultProfileModified;
    private String defaultCommProfile = "N";
    private String isDefault = "N";
    private String unctrlDailyInCount;
    private String unctrlDailyInValue;
    private String unctrlDailyOutCount;
    private String unctrlDailyOutValue;

    private String unctrlWeeklyInCount;
    private String unctrlWeeklyInValue;
    private String unctrlWeeklyOutCount;
    private String unctrlWeeklyOutValue;

    private String unctrlMonthlyInCount;
    private String unctrlMonthlyInValue;
    private String unctrlMonthlyOutCount;
    private String unctrlMonthlyOutValue;
    
   
    
    
    /**
	 * @return the profileId
	 */
	public String getProfileId() {
		return profileId;
	}
	/**
	 * @param profileId the profileId to set
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	/**
	 * @return the productBalanceList
	 */
	public ArrayList<TransferProfileProductVO> getProductBalanceList() {
		return productBalanceList;
	}
	/**
	 * @param productBalanceList the productBalanceList to set
	 */
	public void setProductBalanceList(ArrayList<TransferProfileProductVO> productBalanceList) {
		this.productBalanceList = productBalanceList;
	}
	
	/**
	 * @return the unctrlMonthlyInAltCount
	 */
	public String getUnctrlMonthlyInAltCount() {
		return unctrlMonthlyInAltCount;
	}
	/**
	 * @param unctrlMonthlyInAltCount the unctrlMonthlyInAltCount to set
	 */
	public void setUnctrlMonthlyInAltCount(String unctrlMonthlyInAltCount) {
		this.unctrlMonthlyInAltCount = unctrlMonthlyInAltCount;
	}
	/**
	 * @return the unctrlMonthlyInAltValue
	 */
	public String getUnctrlMonthlyInAltValue() {
		return unctrlMonthlyInAltValue;
	}
	/**
	 * @param unctrlMonthlyInAltValue the unctrlMonthlyInAltValue to set
	 */
	public void setUnctrlMonthlyInAltValue(String unctrlMonthlyInAltValue) {
		this.unctrlMonthlyInAltValue = unctrlMonthlyInAltValue;
	}
	/**
	 * @return the unctrlMonthlyOutAltCount
	 */
	public String getUnctrlMonthlyOutAltCount() {
		return unctrlMonthlyOutAltCount;
	}
	/**
	 * @param unctrlMonthlyOutAltCount the unctrlMonthlyOutAltCount to set
	 */
	public void setUnctrlMonthlyOutAltCount(String unctrlMonthlyOutAltCount) {
		this.unctrlMonthlyOutAltCount = unctrlMonthlyOutAltCount;
	}
	/**
	 * @return the unctrlMonthlyOutAltValue
	 */
	public String getUnctrlMonthlyOutAltValue() {
		return unctrlMonthlyOutAltValue;
	}
	/**
	 * @param unctrlMonthlyOutAltValue the unctrlMonthlyOutAltValue to set
	 */
	public void setUnctrlMonthlyOutAltValue(String unctrlMonthlyOutAltValue) {
		this.unctrlMonthlyOutAltValue = unctrlMonthlyOutAltValue;
	}
	
	/**
	 * @return the unctrlDailyInCount
	 */
	public String getUnctrlDailyInCount() {
		return unctrlDailyInCount;
	}
	/**
	 * @param unctrlDailyInCount the unctrlDailyInCount to set
	 */
	public void setUnctrlDailyInCount(String unctrlDailyInCount) {
		this.unctrlDailyInCount = unctrlDailyInCount;
	}
	/**
	 * @return the unctrlDailyInValue
	 */
	public String getUnctrlDailyInValue() {
		return unctrlDailyInValue;
	}
	/**
	 * @param unctrlDailyInValue the unctrlDailyInValue to set
	 */
	public void setUnctrlDailyInValue(String unctrlDailyInValue) {
		this.unctrlDailyInValue = unctrlDailyInValue;
	}
	/**
	 * @return the unctrlDailyOutCount
	 */
	public String getUnctrlDailyOutCount() {
		return unctrlDailyOutCount;
	}
	/**
	 * @param unctrlDailyOutCount the unctrlDailyOutCount to set
	 */
	public void setUnctrlDailyOutCount(String unctrlDailyOutCount) {
		this.unctrlDailyOutCount = unctrlDailyOutCount;
	}
	/**
	 * @return the unctrlDailyOutValue
	 */
	public String getUnctrlDailyOutValue() {
		return unctrlDailyOutValue;
	}
	/**
	 * @param unctrlDailyOutValue the unctrlDailyOutValue to set
	 */
	public void setUnctrlDailyOutValue(String unctrlDailyOutValue) {
		this.unctrlDailyOutValue = unctrlDailyOutValue;
	}
	/**
	 * @return the unctrlWeeklyInCount
	 */
	public String getUnctrlWeeklyInCount() {
		return unctrlWeeklyInCount;
	}
	/**
	 * @param unctrlWeeklyInCount the unctrlWeeklyInCount to set
	 */
	public void setUnctrlWeeklyInCount(String unctrlWeeklyInCount) {
		this.unctrlWeeklyInCount = unctrlWeeklyInCount;
	}
	/**
	 * @return the unctrlWeeklyInValue
	 */
	public String getUnctrlWeeklyInValue() {
		return unctrlWeeklyInValue;
	}
	/**
	 * @param unctrlWeeklyInValue the unctrlWeeklyInValue to set
	 */
	public void setUnctrlWeeklyInValue(String unctrlWeeklyInValue) {
		this.unctrlWeeklyInValue = unctrlWeeklyInValue;
	}
	/**
	 * @return the unctrlWeeklyOutCount
	 */
	public String getUnctrlWeeklyOutCount() {
		return unctrlWeeklyOutCount;
	}
	/**
	 * @param unctrlWeeklyOutCount the unctrlWeeklyOutCount to set
	 */
	public void setUnctrlWeeklyOutCount(String unctrlWeeklyOutCount) {
		this.unctrlWeeklyOutCount = unctrlWeeklyOutCount;
	}
	/**
	 * @return the unctrlWeeklyOutValue
	 */
	public String getUnctrlWeeklyOutValue() {
		return unctrlWeeklyOutValue;
	}
	/**
	 * @param unctrlWeeklyOutValue the unctrlWeeklyOutValue to set
	 */
	public void setUnctrlWeeklyOutValue(String unctrlWeeklyOutValue) {
		this.unctrlWeeklyOutValue = unctrlWeeklyOutValue;
	}
	/**
	 * @return the unctrlMonthlyInCount
	 */
	public String getUnctrlMonthlyInCount() {
		return unctrlMonthlyInCount;
	}
	/**
	 * @param unctrlMonthlyInCount the unctrlMonthlyInCount to set
	 */
	public void setUnctrlMonthlyInCount(String unctrlMonthlyInCount) {
		this.unctrlMonthlyInCount = unctrlMonthlyInCount;
	}
	/**
	 * @return the unctrlMonthlyInValue
	 */
	public String getUnctrlMonthlyInValue() {
		return unctrlMonthlyInValue;
	}
	/**
	 * @param unctrlMonthlyInValue the unctrlMonthlyInValue to set
	 */
	public void setUnctrlMonthlyInValue(String unctrlMonthlyInValue) {
		this.unctrlMonthlyInValue = unctrlMonthlyInValue;
	}
	/**
	 * @return the unctrlMonthlyOutCount
	 */
	public String getUnctrlMonthlyOutCount() {
		return unctrlMonthlyOutCount;
	}
	/**
	 * @param unctrlMonthlyOutCount the unctrlMonthlyOutCount to set
	 */
	public void setUnctrlMonthlyOutCount(String unctrlMonthlyOutCount) {
		this.unctrlMonthlyOutCount = unctrlMonthlyOutCount;
	}
	/**
	 * @return the unctrlMonthlyOutValue
	 */
	public String getUnctrlMonthlyOutValue() {
		return unctrlMonthlyOutValue;
	}
	/**
	 * @param unctrlMonthlyOutValue the unctrlMonthlyOutValue to set
	 */
	public void setUnctrlMonthlyOutValue(String unctrlMonthlyOutValue) {
		this.unctrlMonthlyOutValue = unctrlMonthlyOutValue;
	}
	/**
	 * @return the networkName
	 */
	public String getNetworkName() {
		return networkName;
	}
	/**
	 * @param networkName the networkName to set
	 */
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	/**
	 * @return the profileName
	 */
	public String getProfileName() {
		return profileName;
	}
	/**
	 * @param profileName the profileName to set
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}
	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the dailyInCount
	 */
	public String getDailyInCount() {
		return dailyInCount;
	}
	/**
	 * @param dailyInCount the dailyInCount to set
	 */
	public void setDailyInCount(String dailyInCount) {
		this.dailyInCount = dailyInCount;
	}
	/**
	 * @return the dailyInValue
	 */
	public String getDailyInValue() {
		return dailyInValue;
	}
	/**
	 * @param dailyInValue the dailyInValue to set
	 */
	public void setDailyInValue(String dailyInValue) {
		this.dailyInValue = dailyInValue;
	}
	/**
	 * @return the dailyOutCount
	 */
	public String getDailyOutCount() {
		return dailyOutCount;
	}
	/**
	 * @param dailyOutCount the dailyOutCount to set
	 */
	public void setDailyOutCount(String dailyOutCount) {
		this.dailyOutCount = dailyOutCount;
	}
	/**
	 * @return the dailyOutValue
	 */
	public String getDailyOutValue() {
		return dailyOutValue;
	}
	/**
	 * @param dailyOutValue the dailyOutValue to set
	 */
	public void setDailyOutValue(String dailyOutValue) {
		this.dailyOutValue = dailyOutValue;
	}
	/**
	 * @return the networkCode
	 */
	public String getNetworkCode() {
		return networkCode;
	}
	/**
	 * @param networkCode the networkCode to set
	 */
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the weeklyInCount
	 */
	public String getWeeklyInCount() {
		return weeklyInCount;
	}
	/**
	 * @param weeklyInCount the weeklyInCount to set
	 */
	public void setWeeklyInCount(String weeklyInCount) {
		this.weeklyInCount = weeklyInCount;
	}
	/**
	 * @return the weeklyInValue
	 */
	public String getWeeklyInValue() {
		return weeklyInValue;
	}
	/**
	 * @param weeklyInValue the weeklyInValue to set
	 */
	public void setWeeklyInValue(String weeklyInValue) {
		this.weeklyInValue = weeklyInValue;
	}
	/**
	 * @return the weeklyOutCount
	 */
	public String getWeeklyOutCount() {
		return weeklyOutCount;
	}
	/**
	 * @param weeklyOutCount the weeklyOutCount to set
	 */
	public void setWeeklyOutCount(String weeklyOutCount) {
		this.weeklyOutCount = weeklyOutCount;
	}
	/**
	 * @return the weeklyOutValue
	 */
	public String getWeeklyOutValue() {
		return weeklyOutValue;
	}
	/**
	 * @param weeklyOutValue the weeklyOutValue to set
	 */
	public void setWeeklyOutValue(String weeklyOutValue) {
		this.weeklyOutValue = weeklyOutValue;
	}
	/**
	 * @return the dailySubscriberOutCount
	 */
	public String getDailySubscriberOutCount() {
		return dailySubscriberOutCount;
	}
	/**
	 * @param dailySubscriberOutCount the dailySubscriberOutCount to set
	 */
	public void setDailySubscriberOutCount(String dailySubscriberOutCount) {
		this.dailySubscriberOutCount = dailySubscriberOutCount;
	}
	/**
	 * @return the weeklySubscriberOutCount
	 */
	public String getWeeklySubscriberOutCount() {
		return weeklySubscriberOutCount;
	}
	/**
	 * @param weeklySubscriberOutCount the weeklySubscriberOutCount to set
	 */
	public void setWeeklySubscriberOutCount(String weeklySubscriberOutCount) {
		this.weeklySubscriberOutCount = weeklySubscriberOutCount;
	}
	/**
	 * @return the monthlySubscriberOutCount
	 */
	public String getMonthlySubscriberOutCount() {
		return monthlySubscriberOutCount;
	}
	/**
	 * @param monthlySubscriberOutCount the monthlySubscriberOutCount to set
	 */
	public void setMonthlySubscriberOutCount(String monthlySubscriberOutCount) {
		this.monthlySubscriberOutCount = monthlySubscriberOutCount;
	}
	/**
	 * @return the dailySubscriberOutValue
	 */
	public String getDailySubscriberOutValue() {
		return dailySubscriberOutValue;
	}
	/**
	 * @param dailySubscriberOutValue the dailySubscriberOutValue to set
	 */
	public void setDailySubscriberOutValue(String dailySubscriberOutValue) {
		this.dailySubscriberOutValue = dailySubscriberOutValue;
	}
	/**
	 * @return the weeklySubscriberOutValue
	 */
	public String getWeeklySubscriberOutValue() {
		return weeklySubscriberOutValue;
	}
	/**
	 * @param weeklySubscriberOutValue the weeklySubscriberOutValue to set
	 */
	public void setWeeklySubscriberOutValue(String weeklySubscriberOutValue) {
		this.weeklySubscriberOutValue = weeklySubscriberOutValue;
	}
	/**
	 * @return the monthlySubscriberOutValue
	 */
	public String getMonthlySubscriberOutValue() {
		return monthlySubscriberOutValue;
	}
	/**
	 * @param monthlySubscriberOutValue the monthlySubscriberOutValue to set
	 */
	public void setMonthlySubscriberOutValue(String monthlySubscriberOutValue) {
		this.monthlySubscriberOutValue = monthlySubscriberOutValue;
	}
	/**
	 * @return the monthlyInCount
	 */
	public String getMonthlyInCount() {
		return monthlyInCount;
	}
	/**
	 * @param monthlyInCount the monthlyInCount to set
	 */
	public void setMonthlyInCount(String monthlyInCount) {
		this.monthlyInCount = monthlyInCount;
	}
	/**
	 * @return the monthlyInValue
	 */
	public String getMonthlyInValue() {
		return monthlyInValue;
	}
	/**
	 * @param monthlyInValue the monthlyInValue to set
	 */
	public void setMonthlyInValue(String monthlyInValue) {
		this.monthlyInValue = monthlyInValue;
	}
	/**
	 * @return the monthlyOutCount
	 */
	public String getMonthlyOutCount() {
		return monthlyOutCount;
	}
	/**
	 * @param monthlyOutCount the monthlyOutCount to set
	 */
	public void setMonthlyOutCount(String monthlyOutCount) {
		this.monthlyOutCount = monthlyOutCount;
	}
	/**
	 * @return the monthlyOutValue
	 */
	public String getMonthlyOutValue() {
		return monthlyOutValue;
	}
	/**
	 * @param monthlyOutValue the monthlyOutValue to set
	 */
	public void setMonthlyOutValue(String monthlyOutValue) {
		this.monthlyOutValue = monthlyOutValue;
	}
	/**
	 * @return the dailyInAltCount
	 */
	public String getDailyInAltCount() {
		return dailyInAltCount;
	}
	/**
	 * @param dailyInAltCount the dailyInAltCount to set
	 */
	public void setDailyInAltCount(String dailyInAltCount) {
		this.dailyInAltCount = dailyInAltCount;
	}
	/**
	 * @return the dailyInAltValue
	 */
	public String getDailyInAltValue() {
		return dailyInAltValue;
	}
	/**
	 * @param dailyInAltValue the dailyInAltValue to set
	 */
	public void setDailyInAltValue(String dailyInAltValue) {
		this.dailyInAltValue = dailyInAltValue;
	}
	/**
	 * @return the dailyOutAltCount
	 */
	public String getDailyOutAltCount() {
		return dailyOutAltCount;
	}
	/**
	 * @param dailyOutAltCount the dailyOutAltCount to set
	 */
	public void setDailyOutAltCount(String dailyOutAltCount) {
		this.dailyOutAltCount = dailyOutAltCount;
	}
	/**
	 * @return the dailyOutAltValue
	 */
	public String getDailyOutAltValue() {
		return dailyOutAltValue;
	}
	/**
	 * @param dailyOutAltValue the dailyOutAltValue to set
	 */
	public void setDailyOutAltValue(String dailyOutAltValue) {
		this.dailyOutAltValue = dailyOutAltValue;
	}
	/**
	 * @return the weeklyInAltCount
	 */
	public String getWeeklyInAltCount() {
		return weeklyInAltCount;
	}
	/**
	 * @param weeklyInAltCount the weeklyInAltCount to set
	 */
	public void setWeeklyInAltCount(String weeklyInAltCount) {
		this.weeklyInAltCount = weeklyInAltCount;
	}
	/**
	 * @return the weeklyInAltValue
	 */
	public String getWeeklyInAltValue() {
		return weeklyInAltValue;
	}
	/**
	 * @param weeklyInAltValue the weeklyInAltValue to set
	 */
	public void setWeeklyInAltValue(String weeklyInAltValue) {
		this.weeklyInAltValue = weeklyInAltValue;
	}
	/**
	 * @return the weeklyOutAltCount
	 */
	public String getWeeklyOutAltCount() {
		return weeklyOutAltCount;
	}
	/**
	 * @param weeklyOutAltCount the weeklyOutAltCount to set
	 */
	public void setWeeklyOutAltCount(String weeklyOutAltCount) {
		this.weeklyOutAltCount = weeklyOutAltCount;
	}
	/**
	 * @return the weeklyOutAltValue
	 */
	public String getWeeklyOutAltValue() {
		return weeklyOutAltValue;
	}
	/**
	 * @param weeklyOutAltValue the weeklyOutAltValue to set
	 */
	public void setWeeklyOutAltValue(String weeklyOutAltValue) {
		this.weeklyOutAltValue = weeklyOutAltValue;
	}
	/**
	 * @return the monthlyInAltCount
	 */
	public String getMonthlyInAltCount() {
		return monthlyInAltCount;
	}
	/**
	 * @param monthlyInAltCount the monthlyInAltCount to set
	 */
	public void setMonthlyInAltCount(String monthlyInAltCount) {
		this.monthlyInAltCount = monthlyInAltCount;
	}
	/**
	 * @return the monthlyInAltValue
	 */
	public String getMonthlyInAltValue() {
		return monthlyInAltValue;
	}
	/**
	 * @param monthlyInAltValue the monthlyInAltValue to set
	 */
	public void setMonthlyInAltValue(String monthlyInAltValue) {
		this.monthlyInAltValue = monthlyInAltValue;
	}
	/**
	 * @return the monthlyOutAltCount
	 */
	public String getMonthlyOutAltCount() {
		return monthlyOutAltCount;
	}
	/**
	 * @param monthlyOutAltCount the monthlyOutAltCount to set
	 */
	public void setMonthlyOutAltCount(String monthlyOutAltCount) {
		this.monthlyOutAltCount = monthlyOutAltCount;
	}
	/**
	 * @return the monthlyOutAltValue
	 */
	public String getMonthlyOutAltValue() {
		return monthlyOutAltValue;
	}
	/**
	 * @param monthlyOutAltValue the monthlyOutAltValue to set
	 */
	public void setMonthlyOutAltValue(String monthlyOutAltValue) {
		this.monthlyOutAltValue = monthlyOutAltValue;
	}
	/**
	 * @return the dailySubscriberOutAltCount
	 */
	public String getDailySubscriberOutAltCount() {
		return dailySubscriberOutAltCount;
	}
	/**
	 * @param dailySubscriberOutAltCount the dailySubscriberOutAltCount to set
	 */
	public void setDailySubscriberOutAltCount(String dailySubscriberOutAltCount) {
		this.dailySubscriberOutAltCount = dailySubscriberOutAltCount;
	}
	/**
	 * @return the weeklySubscriberOutAltCount
	 */
	public String getWeeklySubscriberOutAltCount() {
		return weeklySubscriberOutAltCount;
	}
	/**
	 * @param weeklySubscriberOutAltCount the weeklySubscriberOutAltCount to set
	 */
	public void setWeeklySubscriberOutAltCount(String weeklySubscriberOutAltCount) {
		this.weeklySubscriberOutAltCount = weeklySubscriberOutAltCount;
	}
	/**
	 * @return the monthlySubscriberOutAltCount
	 */
	public String getMonthlySubscriberOutAltCount() {
		return monthlySubscriberOutAltCount;
	}
	/**
	 * @param monthlySubscriberOutAltCount the monthlySubscriberOutAltCount to set
	 */
	public void setMonthlySubscriberOutAltCount(String monthlySubscriberOutAltCount) {
		this.monthlySubscriberOutAltCount = monthlySubscriberOutAltCount;
	}
	/**
	 * @return the dailySubscriberOutAltValue
	 */
	public String getDailySubscriberOutAltValue() {
		return dailySubscriberOutAltValue;
	}
	/**
	 * @param dailySubscriberOutAltValue the dailySubscriberOutAltValue to set
	 */
	public void setDailySubscriberOutAltValue(String dailySubscriberOutAltValue) {
		this.dailySubscriberOutAltValue = dailySubscriberOutAltValue;
	}
	/**
	 * @return the weeklySubscriberOutAltValue
	 */
	public String getWeeklySubscriberOutAltValue() {
		return weeklySubscriberOutAltValue;
	}
	/**
	 * @param weeklySubscriberOutAltValue the weeklySubscriberOutAltValue to set
	 */
	public void setWeeklySubscriberOutAltValue(String weeklySubscriberOutAltValue) {
		this.weeklySubscriberOutAltValue = weeklySubscriberOutAltValue;
	}
	/**
	 * @return the monthlySubscriberOutAltValue
	 */
	public String getMonthlySubscriberOutAltValue() {
		return monthlySubscriberOutAltValue;
	}
	/**
	 * @param monthlySubscriberOutAltValue the monthlySubscriberOutAltValue to set
	 */
	public void setMonthlySubscriberOutAltValue(String monthlySubscriberOutAltValue) {
		this.monthlySubscriberOutAltValue = monthlySubscriberOutAltValue;
	}
	/**
	 * @return the dailySubscriberInCount
	 */
	public String getDailySubscriberInCount() {
		return dailySubscriberInCount;
	}
	/**
	 * @param dailySubscriberInCount the dailySubscriberInCount to set
	 */
	public void setDailySubscriberInCount(String dailySubscriberInCount) {
		this.dailySubscriberInCount = dailySubscriberInCount;
	}
	/**
	 * @return the weeklySubscriberInCount
	 */
	public String getWeeklySubscriberInCount() {
		return weeklySubscriberInCount;
	}
	/**
	 * @param weeklySubscriberInCount the weeklySubscriberInCount to set
	 */
	public void setWeeklySubscriberInCount(String weeklySubscriberInCount) {
		this.weeklySubscriberInCount = weeklySubscriberInCount;
	}
	/**
	 * @return the monthlySubscriberInCount
	 */
	public String getMonthlySubscriberInCount() {
		return monthlySubscriberInCount;
	}
	/**
	 * @param monthlySubscriberInCount the monthlySubscriberInCount to set
	 */
	public void setMonthlySubscriberInCount(String monthlySubscriberInCount) {
		this.monthlySubscriberInCount = monthlySubscriberInCount;
	}
	/**
	 * @return the dailySubscriberInValue
	 */
	public String getDailySubscriberInValue() {
		return dailySubscriberInValue;
	}
	/**
	 * @param dailySubscriberInValue the dailySubscriberInValue to set
	 */
	public void setDailySubscriberInValue(String dailySubscriberInValue) {
		this.dailySubscriberInValue = dailySubscriberInValue;
	}
	/**
	 * @return the weeklySubscriberInValue
	 */
	public String getWeeklySubscriberInValue() {
		return weeklySubscriberInValue;
	}
	/**
	 * @param weeklySubscriberInValue the weeklySubscriberInValue to set
	 */
	public void setWeeklySubscriberInValue(String weeklySubscriberInValue) {
		this.weeklySubscriberInValue = weeklySubscriberInValue;
	}
	/**
	 * @return the monthlySubscriberInValue
	 */
	public String getMonthlySubscriberInValue() {
		return monthlySubscriberInValue;
	}
	/**
	 * @param monthlySubscriberInValue the monthlySubscriberInValue to set
	 */
	public void setMonthlySubscriberInValue(String monthlySubscriberInValue) {
		this.monthlySubscriberInValue = monthlySubscriberInValue;
	}
	/**
	 * @return the dailySubscriberInAltCount
	 */
	public String getDailySubscriberInAltCount() {
		return dailySubscriberInAltCount;
	}
	/**
	 * @param dailySubscriberInAltCount the dailySubscriberInAltCount to set
	 */
	public void setDailySubscriberInAltCount(String dailySubscriberInAltCount) {
		this.dailySubscriberInAltCount = dailySubscriberInAltCount;
	}
	/**
	 * @return the weeklySubscriberInAltCount
	 */
	public String getWeeklySubscriberInAltCount() {
		return weeklySubscriberInAltCount;
	}
	/**
	 * @param weeklySubscriberInAltCount the weeklySubscriberInAltCount to set
	 */
	public void setWeeklySubscriberInAltCount(String weeklySubscriberInAltCount) {
		this.weeklySubscriberInAltCount = weeklySubscriberInAltCount;
	}
	/**
	 * @return the monthlySubscriberInAltCount
	 */
	public String getMonthlySubscriberInAltCount() {
		return monthlySubscriberInAltCount;
	}
	/**
	 * @param monthlySubscriberInAltCount the monthlySubscriberInAltCount to set
	 */
	public void setMonthlySubscriberInAltCount(String monthlySubscriberInAltCount) {
		this.monthlySubscriberInAltCount = monthlySubscriberInAltCount;
	}
	/**
	 * @return the dailySubscriberInAltValue
	 */
	public String getDailySubscriberInAltValue() {
		return dailySubscriberInAltValue;
	}
	/**
	 * @param dailySubscriberInAltValue the dailySubscriberInAltValue to set
	 */
	public void setDailySubscriberInAltValue(String dailySubscriberInAltValue) {
		this.dailySubscriberInAltValue = dailySubscriberInAltValue;
	}
	/**
	 * @return the weeklySubscriberInAltValue
	 */
	public String getWeeklySubscriberInAltValue() {
		return weeklySubscriberInAltValue;
	}
	/**
	 * @param weeklySubscriberInAltValue the weeklySubscriberInAltValue to set
	 */
	public void setWeeklySubscriberInAltValue(String weeklySubscriberInAltValue) {
		this.weeklySubscriberInAltValue = weeklySubscriberInAltValue;
	}
	/**
	 * @return the monthlySubscriberInAltValue
	 */
	public String getMonthlySubscriberInAltValue() {
		return monthlySubscriberInAltValue;
	}
	/**
	 * @param monthlySubscriberInAltValue the monthlySubscriberInAltValue to set
	 */
	public void setMonthlySubscriberInAltValue(String monthlySubscriberInAltValue) {
		this.monthlySubscriberInAltValue = monthlySubscriberInAltValue;
	}
	/**
	 * @return the unctrlDailyInAltCount
	 */
	public String getUnctrlDailyInAltCount() {
		return unctrlDailyInAltCount;
	}
	/**
	 * @param unctrlDailyInAltCount the unctrlDailyInAltCount to set
	 */
	public void setUnctrlDailyInAltCount(String unctrlDailyInAltCount) {
		this.unctrlDailyInAltCount = unctrlDailyInAltCount;
	}
	/**
	 * @return the unctrlDailyInAltValue
	 */
	public String getUnctrlDailyInAltValue() {
		return unctrlDailyInAltValue;
	}
	/**
	 * @param unctrlDailyInAltValue the unctrlDailyInAltValue to set
	 */
	public void setUnctrlDailyInAltValue(String unctrlDailyInAltValue) {
		this.unctrlDailyInAltValue = unctrlDailyInAltValue;
	}
	/**
	 * @return the unctrlDailyOutAltCount
	 */
	public String getUnctrlDailyOutAltCount() {
		return unctrlDailyOutAltCount;
	}
	/**
	 * @param unctrlDailyOutAltCount the unctrlDailyOutAltCount to set
	 */
	public void setUnctrlDailyOutAltCount(String unctrlDailyOutAltCount) {
		this.unctrlDailyOutAltCount = unctrlDailyOutAltCount;
	}
	/**
	 * @return the unctrlDailyOutAltValue
	 */
	public String getUnctrlDailyOutAltValue() {
		return unctrlDailyOutAltValue;
	}
	/**
	 * @param unctrlDailyOutAltValue the unctrlDailyOutAltValue to set
	 */
	public void setUnctrlDailyOutAltValue(String unctrlDailyOutAltValue) {
		this.unctrlDailyOutAltValue = unctrlDailyOutAltValue;
	}
	/**
	 * @return the unctrlWeeklyInAltCount
	 */
	public String getUnctrlWeeklyInAltCount() {
		return unctrlWeeklyInAltCount;
	}
	/**
	 * @param unctrlWeeklyInAltCount the unctrlWeeklyInAltCount to set
	 */
	public void setUnctrlWeeklyInAltCount(String unctrlWeeklyInAltCount) {
		this.unctrlWeeklyInAltCount = unctrlWeeklyInAltCount;
	}
	/**
	 * @return the unctrlWeeklyInAltValue
	 */
	public String getUnctrlWeeklyInAltValue() {
		return unctrlWeeklyInAltValue;
	}
	/**
	 * @param unctrlWeeklyInAltValue the unctrlWeeklyInAltValue to set
	 */
	public void setUnctrlWeeklyInAltValue(String unctrlWeeklyInAltValue) {
		this.unctrlWeeklyInAltValue = unctrlWeeklyInAltValue;
	}
	/**
	 * @return the unctrlWeeklyOutAltCount
	 */
	public String getUnctrlWeeklyOutAltCount() {
		return unctrlWeeklyOutAltCount;
	}
	/**
	 * @param unctrlWeeklyOutAltCount the unctrlWeeklyOutAltCount to set
	 */
	public void setUnctrlWeeklyOutAltCount(String unctrlWeeklyOutAltCount) {
		this.unctrlWeeklyOutAltCount = unctrlWeeklyOutAltCount;
	}
	/**
	 * @return the unctrlWeeklyOutAltValue
	 */
	public String getUnctrlWeeklyOutAltValue() {
		return unctrlWeeklyOutAltValue;
	}
	/**
	 * @param unctrlWeeklyOutAltValue the unctrlWeeklyOutAltValue to set
	 */
	public void setUnctrlWeeklyOutAltValue(String unctrlWeeklyOutAltValue) {
		this.unctrlWeeklyOutAltValue = unctrlWeeklyOutAltValue;
	}
	/**
	 * @return the lastModifiedTime
	 */
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	/**
	 * @return the parentProfileID
	 */
	public String getParentProfileID() {
		return parentProfileID;
	}
	/**
	 * @param parentProfileID the parentProfileID to set
	 */
	public void setParentProfileID(String parentProfileID) {
		this.parentProfileID = parentProfileID;
	}
	/**
	 * @return the isDefaultProfileModified
	 */
	public boolean isDefaultProfileModified() {
		return isDefaultProfileModified;
	}
	/**
	 * @param isDefaultProfileModified the isDefaultProfileModified to set
	 */
	public void setDefaultProfileModified(boolean isDefaultProfileModified) {
		this.isDefaultProfileModified = isDefaultProfileModified;
	}
	/**
	 * @return the defaultCommProfile
	 */
	public String getDefaultCommProfile() {
		return defaultCommProfile;
	}
	/**
	 * @param defaultCommProfile the defaultCommProfile to set
	 */
	public void setDefaultCommProfile(String defaultCommProfile) {
		this.defaultCommProfile = defaultCommProfile;
	}
	/**
	 * @return the isDefault
	 */
	public String getIsDefault() {
		return isDefault;
	}
	/**
	 * @param isDefault the isDefault to set
	 */
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CatTrfProfileRequestVO [productBalanceList=");
		builder.append(productBalanceList);
		builder.append(", profileId=");
		builder.append(profileId);
		builder.append(", networkName=");
		builder.append(networkName);
		builder.append(", profileName=");
		builder.append(profileName);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", description=");
		builder.append(description);
		builder.append(", status=");
		builder.append(status);
		builder.append(", dailyInCount=");
		builder.append(dailyInCount);
		builder.append(", dailyInValue=");
		builder.append(dailyInValue);
		builder.append(", dailyOutCount=");
		builder.append(dailyOutCount);
		builder.append(", dailyOutValue=");
		builder.append(dailyOutValue);
		builder.append(", networkCode=");
		builder.append(networkCode);
		builder.append(", category=");
		builder.append(category);
		builder.append(", weeklyInCount=");
		builder.append(weeklyInCount);
		builder.append(", weeklyInValue=");
		builder.append(weeklyInValue);
		builder.append(", weeklyOutCount=");
		builder.append(weeklyOutCount);
		builder.append(", weeklyOutValue=");
		builder.append(weeklyOutValue);
		builder.append(", dailySubscriberOutCount=");
		builder.append(dailySubscriberOutCount);
		builder.append(", weeklySubscriberOutCount=");
		builder.append(weeklySubscriberOutCount);
		builder.append(", monthlySubscriberOutCount=");
		builder.append(monthlySubscriberOutCount);
		builder.append(", dailySubscriberOutValue=");
		builder.append(dailySubscriberOutValue);
		builder.append(", weeklySubscriberOutValue=");
		builder.append(weeklySubscriberOutValue);
		builder.append(", monthlySubscriberOutValue=");
		builder.append(monthlySubscriberOutValue);
		builder.append(", monthlyInCount=");
		builder.append(monthlyInCount);
		builder.append(", monthlyInValue=");
		builder.append(monthlyInValue);
		builder.append(", monthlyOutCount=");
		builder.append(monthlyOutCount);
		builder.append(", monthlyOutValue=");
		builder.append(monthlyOutValue);
		builder.append(", dailyInAltCount=");
		builder.append(dailyInAltCount);
		builder.append(", dailyInAltValue=");
		builder.append(dailyInAltValue);
		builder.append(", dailyOutAltCount=");
		builder.append(dailyOutAltCount);
		builder.append(", dailyOutAltValue=");
		builder.append(dailyOutAltValue);
		builder.append(", weeklyInAltCount=");
		builder.append(weeklyInAltCount);
		builder.append(", weeklyInAltValue=");
		builder.append(weeklyInAltValue);
		builder.append(", weeklyOutAltCount=");
		builder.append(weeklyOutAltCount);
		builder.append(", weeklyOutAltValue=");
		builder.append(weeklyOutAltValue);
		builder.append(", monthlyInAltCount=");
		builder.append(monthlyInAltCount);
		builder.append(", monthlyInAltValue=");
		builder.append(monthlyInAltValue);
		builder.append(", monthlyOutAltCount=");
		builder.append(monthlyOutAltCount);
		builder.append(", monthlyOutAltValue=");
		builder.append(monthlyOutAltValue);
		builder.append(", dailySubscriberOutAltCount=");
		builder.append(dailySubscriberOutAltCount);
		builder.append(", weeklySubscriberOutAltCount=");
		builder.append(weeklySubscriberOutAltCount);
		builder.append(", monthlySubscriberOutAltCount=");
		builder.append(monthlySubscriberOutAltCount);
		builder.append(", dailySubscriberOutAltValue=");
		builder.append(dailySubscriberOutAltValue);
		builder.append(", weeklySubscriberOutAltValue=");
		builder.append(weeklySubscriberOutAltValue);
		builder.append(", monthlySubscriberOutAltValue=");
		builder.append(monthlySubscriberOutAltValue);
		builder.append(", dailySubscriberInCount=");
		builder.append(dailySubscriberInCount);
		builder.append(", weeklySubscriberInCount=");
		builder.append(weeklySubscriberInCount);
		builder.append(", monthlySubscriberInCount=");
		builder.append(monthlySubscriberInCount);
		builder.append(", dailySubscriberInValue=");
		builder.append(dailySubscriberInValue);
		builder.append(", weeklySubscriberInValue=");
		builder.append(weeklySubscriberInValue);
		builder.append(", monthlySubscriberInValue=");
		builder.append(monthlySubscriberInValue);
		builder.append(", dailySubscriberInAltCount=");
		builder.append(dailySubscriberInAltCount);
		builder.append(", weeklySubscriberInAltCount=");
		builder.append(weeklySubscriberInAltCount);
		builder.append(", monthlySubscriberInAltCount=");
		builder.append(monthlySubscriberInAltCount);
		builder.append(", dailySubscriberInAltValue=");
		builder.append(dailySubscriberInAltValue);
		builder.append(", weeklySubscriberInAltValue=");
		builder.append(weeklySubscriberInAltValue);
		builder.append(", monthlySubscriberInAltValue=");
		builder.append(monthlySubscriberInAltValue);
		builder.append(", unctrlDailyInAltCount=");
		builder.append(unctrlDailyInAltCount);
		builder.append(", unctrlDailyInAltValue=");
		builder.append(unctrlDailyInAltValue);
		builder.append(", unctrlDailyOutAltCount=");
		builder.append(unctrlDailyOutAltCount);
		builder.append(", unctrlDailyOutAltValue=");
		builder.append(unctrlDailyOutAltValue);
		builder.append(", unctrlWeeklyInAltCount=");
		builder.append(unctrlWeeklyInAltCount);
		builder.append(", unctrlWeeklyInAltValue=");
		builder.append(unctrlWeeklyInAltValue);
		builder.append(", unctrlWeeklyOutAltCount=");
		builder.append(unctrlWeeklyOutAltCount);
		builder.append(", unctrlWeeklyOutAltValue=");
		builder.append(unctrlWeeklyOutAltValue);
		builder.append(", unctrlMonthlyInAltCount=");
		builder.append(unctrlMonthlyInAltCount);
		builder.append(", unctrlMonthlyInAltValue=");
		builder.append(unctrlMonthlyInAltValue);
		builder.append(", unctrlMonthlyOutAltCount=");
		builder.append(unctrlMonthlyOutAltCount);
		builder.append(", unctrlMonthlyOutAltValue=");
		builder.append(unctrlMonthlyOutAltValue);
		builder.append(", lastModifiedTime=");
		builder.append(lastModifiedTime);
		builder.append(", parentProfileID=");
		builder.append(parentProfileID);
		builder.append(", isDefaultProfileModified=");
		builder.append(isDefaultProfileModified);
		builder.append(", defaultCommProfile=");
		builder.append(defaultCommProfile);
		builder.append(", isDefault=");
		builder.append(isDefault);
		builder.append(", unctrlDailyInCount=");
		builder.append(unctrlDailyInCount);
		builder.append(", unctrlDailyInValue=");
		builder.append(unctrlDailyInValue);
		builder.append(", unctrlDailyOutCount=");
		builder.append(unctrlDailyOutCount);
		builder.append(", unctrlDailyOutValue=");
		builder.append(unctrlDailyOutValue);
		builder.append(", unctrlWeeklyInCount=");
		builder.append(unctrlWeeklyInCount);
		builder.append(", unctrlWeeklyInValue=");
		builder.append(unctrlWeeklyInValue);
		builder.append(", unctrlWeeklyOutCount=");
		builder.append(unctrlWeeklyOutCount);
		builder.append(", unctrlWeeklyOutValue=");
		builder.append(unctrlWeeklyOutValue);
		builder.append(", unctrlMonthlyInCount=");
		builder.append(unctrlMonthlyInCount);
		builder.append(", unctrlMonthlyInValue=");
		builder.append(unctrlMonthlyInValue);
		builder.append(", unctrlMonthlyOutCount=");
		builder.append(unctrlMonthlyOutCount);
		builder.append(", unctrlMonthlyOutValue=");
		builder.append(unctrlMonthlyOutValue);
		builder.append("]");
		return builder.toString();
	}
    
    
    
    
}
