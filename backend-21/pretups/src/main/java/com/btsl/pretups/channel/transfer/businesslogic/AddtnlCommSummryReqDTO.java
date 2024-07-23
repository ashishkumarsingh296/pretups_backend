
package com.btsl.pretups.channel.transfer.businesslogic;

public class AddtnlCommSummryReqDTO extends CommonDownloadReqDTO {

	private String domain;
	private String categoryCode;
	private String geography;
	private String dailyOrmonthlyOption;
	private String fromMonthYear;
	private String toMonthYear;
	private String service;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getDailyOrmonthlyOption() {
		return dailyOrmonthlyOption;
	}

	public void setDailyOrmonthlyOption(String dailyOrmonthlyOption) {
		this.dailyOrmonthlyOption = dailyOrmonthlyOption;
	}

	
	public String getFromMonthYear() {
		return fromMonthYear;
	}

	public void setFromMonthYear(String fromMonthYear) {
		this.fromMonthYear = fromMonthYear;
	}

	public String getToMonthYear() {
		return toMonthYear;
	}

	public void setToMonthYear(String toMonthYear) {
		this.toMonthYear = toMonthYear;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AddtnlCommSummryReqDTO [ ");
		sb.append("From Date : ");
		sb.append(fromDate).append(" ,");
		sb.append(",To Date : ");
		sb.append(toDate).append(" ,");
		sb.append(",NetworkCode: ");
		sb.append(extnwcode).append(" ,");
		sb.append(",categoryCode: ");
		sb.append(categoryCode).append(" ,");
		sb.append(",domain: ");
		sb.append(domain).append(" ,");
		sb.append(",geography: ");
		sb.append(geography).append(" ,");
		sb.append(",user: ");
		
		
		
		sb.append(" ]");
		return sb.toString();
	}

}
