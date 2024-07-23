package com.btsl.user.businesslogic;

public class TotalDailyUserIncomeResponseVO {
	String totalIncome ;
	String baseCommission ;
	String additionalCommission;
	String cac ;
	String cbc;
	String date;
	/**
	 * @return the totalIncome
	 */
	public String getTotalIncome() {
		return totalIncome;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TotalDailyUserIncomeResponseVO [totalIncome=" + totalIncome + ", baseCommission=" + baseCommission
				+ ", additionalCommission=" + additionalCommission + ", cac=" + cac + ", cbc=" + cbc + ", date=" + date
				+ "]";
	}
	/**
	 * @param totalIncome the totalIncome to set
	 */
	public void setTotalIncome(String totalIncome) {
		this.totalIncome = totalIncome;
	}
	/**
	 * @return the baseCommission
	 */
	public String getBaseCommission() {
		return baseCommission;
	}
	/**
	 * @param baseCommission the baseCommission to set
	 */
	public void setBaseCommission(String baseCommission) {
		this.baseCommission = baseCommission;
	}
	/**
	 * @return the additionalCommission
	 */
	public String getAdditionalCommission() {
		return additionalCommission;
	}
	/**
	 * @param additionalCommission the additionalCommission to set
	 */
	public void setAdditionalCommission(String additionalCommission) {
		this.additionalCommission = additionalCommission;
	}
	/**
	 * @return the cac
	 */
	public String getCac() {
		return cac;
	}
	/**
	 * @param cac the cac to set
	 */
	public void setCac(String cac) {
		this.cac = cac;
	}
	/**
	 * @return the cbc
	 */
	public String getCbc() {
		return cbc;
	}
	/**
	 * @param cbc the cbc to set
	 */
	public void setCbc(String cbc) {
		this.cbc = cbc;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
}
