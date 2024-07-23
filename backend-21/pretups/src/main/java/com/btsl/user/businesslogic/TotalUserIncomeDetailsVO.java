package com.btsl.user.businesslogic;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class TotalUserIncomeDetailsVO {
	String msisdn = null;
	String extnwcode = null;
	long totalIncome ;
	long baseCommission ;
	long additionalCommission;
	long cac ;
	long cbc;
	long toatalincomec2co2c;
	long totalincomec2s;
	
	
	
	LinkedList<TotalDailyUserIncomeResponseVO> detailedInfoList ;
	
	/**
	 * @return the detailedInfoList
	 */
	public LinkedList<TotalDailyUserIncomeResponseVO> getDetailedInfoList() {
		return detailedInfoList;
	}
	/**
	 * @param detailedInfoList the detailedInfoList to set
	 */
	public void setDetailedInfoList(LinkedList<TotalDailyUserIncomeResponseVO> detailedInfoList) {
		this.detailedInfoList = detailedInfoList;
	}
	/**
	 * @return the map
	 */
	public LinkedHashMap<Date, TotalUserIncomeDetailsVO> getDetilInfoMap() {
		return detilInfoMap;
	}
	/**
	 * @param map the map to set
	 */
	public void setDetilInfoMap(LinkedHashMap<Date, TotalUserIncomeDetailsVO> map) {
		this.detilInfoMap = map;
	}
	LinkedHashMap<Date, TotalUserIncomeDetailsVO> detilInfoMap;
	
	/**
	 * @return the toatalincomec2co2c
	 */
	public long getToatalincomec2co2c() {
		return toatalincomec2co2c;
	}
	/**
	 * @param toatalincomec2co2c the toatalincomec2co2c to set
	 */
	public void setToatalincomec2co2c(long toatalincomec2co2c) {
		this.toatalincomec2co2c = toatalincomec2co2c;
	}
	/**
	 * @return the totalincomec2s
	 */
	public long getTotalincomec2s() {
		return totalincomec2s;
	}
	/**
	 * @param totalincomec2s the totalincomec2s to set
	 */
	public void setTotalincomec2s(long totalincomec2s) {
		this.totalincomec2s = totalincomec2s;
	}
	/**
	 * @return the extnwcode
	 */
	public String getExtnwcode() {
		return extnwcode;
	}
	/**
	 * @param extnwcode the extnwcode to set
	 */
	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	/**
	 * @return the totalIncome1
	 */
	public long getTotalIncome1() {
		return totalIncome1;
	}
	/**
	 * @param totalIncome1 the totalIncome1 to set
	 */
	public void setTotalIncome1(long totalIncome1) {
		this.totalIncome1 = totalIncome1;
	}
	/**
	 * @return the previousTotalIncome
	 */
	public long getPreviousTotalIncome() {
		return previousTotalIncome;
	}
	/**
	 * @param previousTotalIncome the previousTotalIncome to set
	 */
	public void setPreviousTotalIncome(long previousTotalIncome) {
		this.previousTotalIncome = previousTotalIncome;
	}
	/**
	 * @return the totalBaseCom
	 */
	public long getTotalBaseCom() {
		return totalBaseCom;
	}
	/**
	 * @param totalBaseCom the totalBaseCom to set
	 */
	public void setTotalBaseCom(long totalBaseCom) {
		this.totalBaseCom = totalBaseCom;
	}
	/**
	 * @return the previousTotalBaseComm
	 */
	public long getPreviousTotalBaseComm() {
		return previousTotalBaseComm;
	}
	/**
	 * @param previousTotalBaseComm the previousTotalBaseComm to set
	 */
	public void setPreviousTotalBaseComm(long previousTotalBaseComm) {
		this.previousTotalBaseComm = previousTotalBaseComm;
	}
	/**
	 * @return the totalAdditionalBaseCom
	 */
	public long getTotalAdditionalBaseCom() {
		return totalAdditionalBaseCom;
	}
	/**
	 * @param totalAdditionalBaseCom the totalAdditionalBaseCom to set
	 */
	public void setTotalAdditionalBaseCom(long totalAdditionalBaseCom) {
		this.totalAdditionalBaseCom = totalAdditionalBaseCom;
	}
	/**
	 * @return the previousTotalAdditionalBaseCom
	 */
	public long getPreviousTotalAdditionalBaseCom() {
		return previousTotalAdditionalBaseCom;
	}
	/**
	 * @param previousTotalAdditionalBaseCom the previousTotalAdditionalBaseCom to set
	 */
	public void setPreviousTotalAdditionalBaseCom(long previousTotalAdditionalBaseCom) {
		this.previousTotalAdditionalBaseCom = previousTotalAdditionalBaseCom;
	}
	/**
	 * @return the totalCac
	 */
	public long getTotalCac() {
		return totalCac;
	}
	/**
	 * @param totalCac the totalCac to set
	 */
	public void setTotalCac(long totalCac) {
		this.totalCac = totalCac;
	}
	/**
	 * @return the previousTotalCac
	 */
	public long getPreviousTotalCac() {
		return previousTotalCac;
	}
	/**
	 * @param previousTotalCac the previousTotalCac to set
	 */
	public void setPreviousTotalCac(long previousTotalCac) {
		this.previousTotalCac = previousTotalCac;
	}
	/**
	 * @return the totalCbc
	 */
	public long getTotalCbc() {
		return totalCbc;
	}
	/**
	 * @param totalCbc the totalCbc to set
	 */
	public void setTotalCbc(long totalCbc) {
		this.totalCbc = totalCbc;
	}
	/**
	 * @return the previousTotalCbc
	 */
	public long getPreviousTotalCbc() {
		return previousTotalCbc;
	}
	/**
	 * @param previousTotalCbc the previousTotalCbc to set
	 */
	public void setPreviousTotalCbc(long previousTotalCbc) {
		this.previousTotalCbc = previousTotalCbc;
	}
	/**
	 * @return the previousFromDate
	 */
	public long getPreviousFromDate() {
		return previousFromDate;
	}
	/**
	 * @param previousFromDate the previousFromDate to set
	 */
	public void setPreviousFromDate(long previousFromDate) {
		this.previousFromDate = previousFromDate;
	}
	/**
	 * @return the previousToDate
	 */
	public long getPreviousToDate() {
		return previousToDate;
	}
	/**
	 * @param previousToDate the previousToDate to set
	 */
	public void setPreviousToDate(long previousToDate) {
		this.previousToDate = previousToDate;
	}
	String fromdatestring = null;
	String todatestring = null;
	long totalIncome1;
	long previousTotalIncome;
	long totalBaseCom; 
	long previousTotalBaseComm ;
	long totalAdditionalBaseCom;
	long previousTotalAdditionalBaseCom;
	long totalCac;
	long previousTotalCac;
	long totalCbc;
	long previousTotalCbc;
	long previousFromDate;
	long previousToDate;
	
	
	
	
	
	public long getCac() {
		return cac;
	}
	public void setCac(long cac) {
		this.cac = cac;
	}
	
	public long getCbc() {
		return cbc;
	}
	public void setCbc(long cbc) {
		this.cbc = cbc;
	}
	public long getAdditionalCommission() {
		return additionalCommission;
	}
	public void setAdditionalCommission(long additionalCommission) {
		this.additionalCommission = additionalCommission;
	}
	
	public long getBaseCommission() {
		return baseCommission;
	}
	public void setBaseCommission(long baseCommission) {
		this.baseCommission = baseCommission;
	}
	public long getTotalIncome() {
		return totalIncome;
	}
	public void setTotalIncome(long totalIncome) {
		this.totalIncome = totalIncome;
	}
	public String getFromdatestring() {
		return fromdatestring;
	}
	public void setExtnwCode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	public String getExtnwCode() {
		return extnwcode;
	}
	public void setFromdatestring(String fromdatestring) {
		this.fromdatestring = fromdatestring;
	}
	public String getTodatestring() {
		return todatestring;
	}
	public void setTodatestring(String todatestring) {
		this.todatestring = todatestring;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	 public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public long getCommissionValue() {
		return commissionValue;
	}
	public void setCommissionValue(long commissionValue) {
		this.commissionValue = commissionValue;
	}
	private Date fromDate = null;
	 private Date toDate = null;
	 private long commissionValue;

	 private String userID;
	 private long marginAmount;
	public long getMarginAmount() {
		return marginAmount;
	}
	public void setMarginAmount(long marginAmount) {
		this.marginAmount = marginAmount;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
}
