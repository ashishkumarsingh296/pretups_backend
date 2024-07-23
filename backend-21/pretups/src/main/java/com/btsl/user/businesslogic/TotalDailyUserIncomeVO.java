package com.btsl.user.businesslogic;

import java.util.Date;

public class TotalDailyUserIncomeVO {
	long totalIncome ;
	long baseCommission ;
	long additionalCommission;
	long cac ;
	long cbc;
	Date date;
	/**
	 * @return the totalIncome
	 */
	public long getTotalIncome() {
		return totalIncome;
	}
	/**
	 * @param totalIncome the totalIncome to set
	 */
	public void setTotalIncome(long totalIncome) {
		this.totalIncome = totalIncome;
	}
	/**
	 * @return the baseCommission
	 */
	public long getBaseCommission() {
		return baseCommission;
	}
	/**
	 * @param baseCommission the baseCommission to set
	 */
	public void setBaseCommission(long baseCommission) {
		this.baseCommission = baseCommission;
	}
	/**
	 * @return the additionalCommission
	 */
	public long getAdditionalCommission() {
		return additionalCommission;
	}
	/**
	 * @param additionalCommission the additionalCommission to set
	 */
	public void setAdditionalCommission(long additionalCommission) {
		this.additionalCommission = additionalCommission;
	}
	/**
	 * @return the cac
	 */
	public long getCac() {
		return cac;
	}
	/**
	 * @param cac the cac to set
	 */
	public void setCac(long cac) {
		this.cac = cac;
	}
	/**
	 * @return the cbc
	 */
	public long getCbc() {
		return cbc;
	}
	/**
	 * @param cbc the cbc to set
	 */
	public void setCbc(long cbc) {
		this.cbc = cbc;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
}
