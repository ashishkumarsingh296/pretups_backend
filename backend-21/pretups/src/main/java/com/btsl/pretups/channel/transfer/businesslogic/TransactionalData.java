package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionalData {
	@JsonProperty("fromDate")
	private String fromDate;
	@JsonProperty("toDate")
	private String toDate;
	@JsonProperty("totalCount")
	private String totalCount;
	@JsonProperty("totalValue")
	private String totalValue;
	@JsonProperty("lastMonthCount")
	private String lastMonthCount;
	@JsonProperty("lastMonthValue")
	private String lastMonthValue;
	@JsonProperty("previousFromDate")
	private String previousFromDate;
	@JsonProperty("previousToDate")
	private String previousToDate;
	@JsonProperty("totalPercentage")
	private String totalPercentage;
	@JsonProperty("countPercentage")
	private String countPercentage;
	
	@JsonProperty("fromDate")
	public String getFromDate() {
	return fromDate;
	}

	@JsonProperty("fromDate")
	public void setFromDate(String fromDate) {
	this.fromDate = fromDate;
	}

	@JsonProperty("toDate")
	public String getToDate() {
	return toDate;
	}

	@JsonProperty("toDate")
	public void setToDate(String toDate) {
	this.toDate = toDate;
	}

	@JsonProperty("totalCount")
	public String getTotalCount() {
	return totalCount;
	}

	@JsonProperty("totalCount")
	public void setTotalCount(String totalCount) {
	this.totalCount = totalCount;
	}

	@JsonProperty("totalValue")
	public String getTotalValue() {
	return totalValue;
	}

	@JsonProperty("totalValue")
	public void setTotalValue(String totalValue) {
	this.totalValue = totalValue;
	}

	@JsonProperty("lastMonthCount")
	public String getLastMonthCount() {
	return lastMonthCount;
	}

	@JsonProperty("lastMonthCount")
	public void setLastMonthCount(String lastMonthCount) {
	this.lastMonthCount = lastMonthCount;
	}

	@JsonProperty("lastMonthValue")
	public String getLastMonthValue() {
	return lastMonthValue;
	}

	@JsonProperty("lastMonthValue")
	public void setLastMonthValue(String lastMonthValue) {
	this.lastMonthValue = lastMonthValue;
	}

	@JsonProperty("previousFromDate")
	public String getPreviousFromDate() {
	return previousFromDate;
	}

	@JsonProperty("previousFromDate")
	public void setPreviousFromDate(String previousFromDate) {
	this.previousFromDate = previousFromDate;
	}

	@JsonProperty("previousToDate")
	public String getPreviousToDate() {
	return previousToDate;
	}

	@JsonProperty("previousToDate")
	public void setPreviousToDate(String previousToDate) {
	this.previousToDate = previousToDate;
	}

	public String getTotalPercentage() {
		return totalPercentage;
	}

	public void setTotalPercentage(String totalPercentage) {
		this.totalPercentage = totalPercentage;
	}

	public String getCountPercentage() {
		return countPercentage;
	}

	public void setCountPercentage(String countPercentage) {
		this.countPercentage = countPercentage;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionalData [fromDate=").append(fromDate).append(", toDate=").append(toDate)
				.append(", totalCount=").append(totalCount).append(", totalValue=").append(totalValue)
				.append(", lastMonthCount=").append(lastMonthCount).append(", lastMonthValue=").append(lastMonthValue)
				.append(", previousFromDate=").append(previousFromDate).append(", previousToDate=").append(previousToDate)
				.append(", totalPercentage=").append(totalPercentage).append(", countPercentage=").append(countPercentage);
		return builder.toString();
	}
}