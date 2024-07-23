package com.btsl.pretups.p2p.subscriber.businesslogic;
/* BuddyVO.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Abhijit Singh Chauhan              21/06/2005         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.util.Date;


public class P2PSubscriberCounterVO implements Serializable{

	private String serviceName;
	private String serviceType;
	private long DailyTransferCount;
	private long MonthlyTransferCount;
	private long WeeklyTransferCount;
	private long PrevDailyTransferCount;
	private long PrevMonthlyTransferCount;
	private long PrevWeeklyTransferCount;
	private long DailyTransferAmount;
	private long MonthlyTransferAmount;
	private long WeeklyTransferAmount;
	private long PrevDailyTransferAmount;
	private long PrevMonthlyTransferAmount;
	private long PrevWeeklyTransferAmount;
	private Date PrevTransferDate;
	private Date PrevTransferWeekDate;
	private Date PrevTransferMonthlyDate;
	private long InvalidVoucherPinCount;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "P2PSubscriberCounterVO [serviceName=" + serviceName + ", serviceType=" + serviceType
				+ ", DailyTransferCount=" + DailyTransferCount + ", MonthlyTransferCount=" + MonthlyTransferCount
				+ ", WeeklyTransferCount=" + WeeklyTransferCount + ", PrevDailyTransferCount=" + PrevDailyTransferCount
				+ ", PrevMonthlyTransferCount=" + PrevMonthlyTransferCount + ", PrevWeeklyTransferCount="
				+ PrevWeeklyTransferCount + ", DailyTransferAmount=" + DailyTransferAmount + ", MonthlyTransferAmount="
				+ MonthlyTransferAmount + ", WeeklyTransferAmount=" + WeeklyTransferAmount
				+ ", PrevDailyTransferAmount=" + PrevDailyTransferAmount + ", PrevMonthlyTransferAmount="
				+ PrevMonthlyTransferAmount + ", PrevWeeklyTransferAmount=" + PrevWeeklyTransferAmount
				+ ", PrevTransferDate=" + PrevTransferDate + ", PrevTransferWeekDate=" + PrevTransferWeekDate
				+ ", PrevTransferMonthlyDate=" + PrevTransferMonthlyDate 
				+", InvalidVoucherPinCount="+InvalidVoucherPinCount+"]";
	}
	/**
	 * 
	 * @return
	 */
	public long getInvalidVoucherPinCount() {
		return InvalidVoucherPinCount;
	}
	/**
	 * 
	 * @param invalidVoucherPinCount
	 */
	public void setInvalidVoucherPinCount(long invalidVoucherPinCount) {
		InvalidVoucherPinCount = invalidVoucherPinCount;
	}

	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	/**
	 * @return the dailyTransferCount
	 */
	public long getDailyTransferCount() {
		return DailyTransferCount;
	}
	/**
	 * @param dailyTransferCount the dailyTransferCount to set
	 */
	public void setDailyTransferCount(long dailyTransferCount) {
		DailyTransferCount = dailyTransferCount;
	}
	/**
	 * @return the monthlyTransferCount
	 */
	public long getMonthlyTransferCount() {
		return MonthlyTransferCount;
	}
	/**
	 * @param monthlyTransferCount the monthlyTransferCount to set
	 */
	public void setMonthlyTransferCount(long monthlyTransferCount) {
		MonthlyTransferCount = monthlyTransferCount;
	}
	/**
	 * @return the weeklyTransferCount
	 */
	public long getWeeklyTransferCount() {
		return WeeklyTransferCount;
	}
	/**
	 * @param weeklyTransferCount the weeklyTransferCount to set
	 */
	public void setWeeklyTransferCount(long weeklyTransferCount) {
		WeeklyTransferCount = weeklyTransferCount;
	}
	/**
	 * @return the prevDailyTransferCount
	 */
	public long getPrevDailyTransferCount() {
		return PrevDailyTransferCount;
	}
	/**
	 * @param prevDailyTransferCount the prevDailyTransferCount to set
	 */
	public void setPrevDailyTransferCount(long prevDailyTransferCount) {
		PrevDailyTransferCount = prevDailyTransferCount;
	}
	/**
	 * @return the prevMonthlyTransferCount
	 */
	public long getPrevMonthlyTransferCount() {
		return PrevMonthlyTransferCount;
	}
	/**
	 * @param prevMonthlyTransferCount the prevMonthlyTransferCount to set
	 */
	public void setPrevMonthlyTransferCount(long prevMonthlyTransferCount) {
		PrevMonthlyTransferCount = prevMonthlyTransferCount;
	}
	/**
	 * @return the prevWeeklyTransferCount
	 */
	public long getPrevWeeklyTransferCount() {
		return PrevWeeklyTransferCount;
	}
	/**
	 * @param prevWeeklyTransferCount the prevWeeklyTransferCount to set
	 */
	public void setPrevWeeklyTransferCount(long prevWeeklyTransferCount) {
		PrevWeeklyTransferCount = prevWeeklyTransferCount;
	}
	/**
	 * @return the dailyTransferAmount
	 */
	public long getDailyTransferAmount() {
		return DailyTransferAmount;
	}
	/**
	 * @param dailyTransferAmount the dailyTransferAmount to set
	 */
	public void setDailyTransferAmount(long dailyTransferAmount) {
		DailyTransferAmount = dailyTransferAmount;
	}
	/**
	 * @return the monthlyTransferAmount
	 */
	public long getMonthlyTransferAmount() {
		return MonthlyTransferAmount;
	}
	/**
	 * @param monthlyTransferAmount the monthlyTransferAmount to set
	 */
	public void setMonthlyTransferAmount(long monthlyTransferAmount) {
		MonthlyTransferAmount = monthlyTransferAmount;
	}
	/**
	 * @return the weeklyTransferAmount
	 */
	public long getWeeklyTransferAmount() {
		return WeeklyTransferAmount;
	}
	/**
	 * @param weeklyTransferAmount the weeklyTransferAmount to set
	 */
	public void setWeeklyTransferAmount(long weeklyTransferAmount) {
		WeeklyTransferAmount = weeklyTransferAmount;
	}
	/**
	 * @return the prevDailyTransferAmount
	 */
	public long getPrevDailyTransferAmount() {
		return PrevDailyTransferAmount;
	}
	/**
	 * @param prevDailyTransferAmount the prevDailyTransferAmount to set
	 */
	public void setPrevDailyTransferAmount(long prevDailyTransferAmount) {
		PrevDailyTransferAmount = prevDailyTransferAmount;
	}
	/**
	 * @return the prevMonthlyTransferAmount
	 */
	public long getPrevMonthlyTransferAmount() {
		return PrevMonthlyTransferAmount;
	}
	/**
	 * @param prevMonthlyTransferAmount the prevMonthlyTransferAmount to set
	 */
	public void setPrevMonthlyTransferAmount(long prevMonthlyTransferAmount) {
		PrevMonthlyTransferAmount = prevMonthlyTransferAmount;
	}
	/**
	 * @return the prevWeeklyTransferAmount
	 */
	public long getPrevWeeklyTransferAmount() {
		return PrevWeeklyTransferAmount;
	}
	/**
	 * @param prevWeeklyTransferAmount the prevWeeklyTransferAmount to set
	 */
	public void setPrevWeeklyTransferAmount(long prevWeeklyTransferAmount) {
		PrevWeeklyTransferAmount = prevWeeklyTransferAmount;
	}
	/**
	 * @return the prevTransferDate
	 */
	public Date getPrevTransferDate() {
		return PrevTransferDate;
	}
	/**
	 * @param prevTransferDate the prevTransferDate to set
	 */
	public void setPrevTransferDate(Date prevTransferDate) {
		PrevTransferDate = prevTransferDate;
	}
	/**
	 * @return the prevTransferWeekDate
	 */
	public Date getPrevTransferWeekDate() {
		return PrevTransferWeekDate;
	}
	/**
	 * @param prevTransferWeekDate the prevTransferWeekDate to set
	 */
	public void setPrevTransferWeekDate(Date prevTransferWeekDate) {
		PrevTransferWeekDate = prevTransferWeekDate;
	}
	/**
	 * @return the prevTransferMonthlyDate
	 */
	public Date getPrevTransferMonthlyDate() {
		return PrevTransferMonthlyDate;
	}
	/**
	 * @param prevTransferMonthlyDate the prevTransferMonthlyDate to set
	 */
	public void setPrevTransferMonthlyDate(Date prevTransferMonthlyDate) {
		PrevTransferMonthlyDate = prevTransferMonthlyDate;
	}
	
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
}

