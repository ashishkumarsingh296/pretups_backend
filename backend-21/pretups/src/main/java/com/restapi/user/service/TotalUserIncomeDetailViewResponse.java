package com.restapi.user.service;

import java.util.LinkedList;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.user.businesslogic.TotalDailyUserIncomeResponseVO;

public class TotalUserIncomeDetailViewResponse extends BaseResponseMultiple{

	LinkedList<TotalDailyUserIncomeResponseVO> detailedInfoList ;
	double totalIncome;
	double previousTotalIncome;
	double totalBaseCom; 
	double previousTotalBaseComm ;
	double totalAdditionalBaseCom;
	double previousTotalAdditionalBaseCom;
	double totalCac;
	double previousTotalCac;
	double totalCbc;
	double previousTotalCbc;
	String fromDate;
	String toDate;
	String previousFromDate;
	String previousToDate;
	String totalIncomePercentage;
	String totalBaseComPercentage;
	String totalAdditionalBaseComPercentage;
	String totalCacPercentage;
	String totalCbcPercentage;
	public LinkedList<TotalDailyUserIncomeResponseVO> getDetailedInfoList() {
		return detailedInfoList;
	}
	public void setDetailedInfoList(LinkedList<TotalDailyUserIncomeResponseVO> detailedInfoList) {
		this.detailedInfoList = detailedInfoList;
	}
	public double getTotalIncome() {
		return totalIncome;
	}
	public void setTotalIncome(double totalIncome) {
		this.totalIncome = totalIncome;
	}
	public double getPreviousTotalIncome() {
		return previousTotalIncome;
	}
	public void setPreviousTotalIncome(double previousTotalIncome) {
		this.previousTotalIncome = previousTotalIncome;
	}
	public double getTotalBaseCom() {
		return totalBaseCom;
	}
	public void setTotalBaseCom(double totalBaseCom) {
		this.totalBaseCom = totalBaseCom;
	}
	public double getPreviousTotalBaseComm() {
		return previousTotalBaseComm;
	}
	public void setPreviousTotalBaseComm(double previousTotalBaseComm) {
		this.previousTotalBaseComm = previousTotalBaseComm;
	}
	public double getTotalAdditionalBaseCom() {
		return totalAdditionalBaseCom;
	}
	public void setTotalAdditionalBaseCom(double totalAdditionalBaseCom) {
		this.totalAdditionalBaseCom = totalAdditionalBaseCom;
	}
	public double getPreviousTotalAdditionalBaseCom() {
		return previousTotalAdditionalBaseCom;
	}
	public void setPreviousTotalAdditionalBaseCom(double previousTotalAdditionalBaseCom) {
		this.previousTotalAdditionalBaseCom = previousTotalAdditionalBaseCom;
	}
	public double getTotalCac() {
		return totalCac;
	}
	public void setTotalCac(double totalCac) {
		this.totalCac = totalCac;
	}
	public double getPreviousTotalCac() {
		return previousTotalCac;
	}
	public void setPreviousTotalCac(double previousTotalCac) {
		this.previousTotalCac = previousTotalCac;
	}
	public double getTotalCbc() {
		return totalCbc;
	}
	public void setTotalCbc(double totalCbc) {
		this.totalCbc = totalCbc;
	}
	public double getPreviousTotalCbc() {
		return previousTotalCbc;
	}
	public void setPreviousTotalCbc(double previousTotalCbc) {
		this.previousTotalCbc = previousTotalCbc;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	public String getPreviousFromDate() {
		return previousFromDate;
	}
	public void setPreviousFromDate(String previousFromDate) {
		this.previousFromDate = previousFromDate;
	}
	public String getPreviousToDate() {
		return previousToDate;
	}
	public void setPreviousToDate(String previousToDate) {
		this.previousToDate = previousToDate;
	}
	
	
	public String getTotalIncomePercentage() {
		return totalIncomePercentage;
	}
	public void setTotalIncomePercentage(String totalIncomePercentage) {
		this.totalIncomePercentage = totalIncomePercentage;
	}
	public String getTotalBaseComPercentage() {
		return totalBaseComPercentage;
	}
	public void setTotalBaseComPercentage(String totalBaseComPercentage) {
		this.totalBaseComPercentage = totalBaseComPercentage;
	}
	public String getTotalAdditionalBaseComPercentage() {
		return totalAdditionalBaseComPercentage;
	}
	public void setTotalAdditionalBaseComPercentage(String totalAdditionalBaseComPercentage) {
		this.totalAdditionalBaseComPercentage = totalAdditionalBaseComPercentage;
	}
	public String getTotalCacPercentage() {
		return totalCacPercentage;
	}
	public void setTotalCacPercentage(String totalCacPercentage) {
		this.totalCacPercentage = totalCacPercentage;
	}
	public String getTotalCbcPercentage() {
		return totalCbcPercentage;
	}
	public void setTotalCbcPercentage(String totalCbcPercentage) {
		this.totalCbcPercentage = totalCbcPercentage;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TotalUserIncomeDetailViewResponse [detailedInfoList=").append(detailedInfoList)
				.append(", totalIncome=").append(totalIncome).append(", previousTotalIncome=")
				.append(previousTotalIncome).append(", totalBaseCom=").append(totalBaseCom)
				.append(", previousTotalBaseComm=").append(previousTotalBaseComm).append(", totalAdditionalBaseCom=")
				.append(totalAdditionalBaseCom).append(", previousTotalAdditionalBaseCom=")
				.append(previousTotalAdditionalBaseCom).append(", totalCac=").append(totalCac)
				.append(", previousTotalCac=").append(previousTotalCac).append(", totalCbc=").append(totalCbc)
				.append(", previousTotalCbc=").append(previousTotalCbc).append(", fromDate=").append(fromDate)
				.append(", toDate=").append(toDate).append(", previousFromDate=").append(previousFromDate)
				.append(", previousToDate=").append(previousToDate).append(", totalIncomePercentage=")
				.append(totalIncomePercentage).append(", totalBaseComPercentage=").append(totalBaseComPercentage)
				.append(", totalAdditionalBaseComPercentage=").append(totalAdditionalBaseComPercentage)
				.append(", totalCacPercentage=").append(totalCacPercentage).append(", totalCbcPercentage=")
				.append(totalCbcPercentage).append("]");
		return builder.toString();
	}
	
	
	
	
	
	
	
}
