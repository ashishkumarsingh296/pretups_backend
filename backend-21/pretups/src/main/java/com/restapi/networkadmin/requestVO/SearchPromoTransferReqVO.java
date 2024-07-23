package com.restapi.networkadmin.requestVO;

public class SearchPromoTransferReqVO {

	
	
	private String optionTab; // USR/GRD/GRP/CAT/CELL/SRV
	private String domainCode;
	private String categoryCode;
	private String geoGraphyDomainType;
	private String geography;
	private String userID;
	private String grade;
	private String cellGroupID;
	
	public SearchPromoTransferReqVO(){
		
	}
	public String getOptionTab() {
		return optionTab;
	}
	public void setOptionTab(String optionTab) {
		this.optionTab = optionTab;
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
	public String getGeoGraphyDomainType() {
		return geoGraphyDomainType;
	}
	public void setGeoGraphyDomainType(String geoGraphyDomainType) {
		this.geoGraphyDomainType = geoGraphyDomainType;
	}
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getCellGroupID() {
		return cellGroupID;
	}
	public void setCellGroupID(String cellGroupID) {
		this.cellGroupID = cellGroupID;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	
	
	
	

}
