package com.restapi.networkadmin.commissionprofile.requestVO;

public class LoadVersionListBasedOnDateRequestVO {
	private String categoryCode;
	private String date;
	private String commProfileSetId;
	
	
	public String getCommProfileSetId() {
		return commProfileSetId;
	}
	public void setCommProfileSetId(String commProfileSetId) {
		this.commProfileSetId = commProfileSetId;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
