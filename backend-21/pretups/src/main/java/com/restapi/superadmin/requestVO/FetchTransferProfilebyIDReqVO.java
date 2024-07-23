package com.restapi.superadmin.requestVO;

public class FetchTransferProfilebyIDReqVO {
	
	private String profileID;
	private String networkCode;
	private String categoryCode;
	
	
	public FetchTransferProfilebyIDReqVO() {
		
	}


	public String getProfileID() {
		return profileID;
	}


	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}


	public String getNetworkCode() {
		return networkCode;
	}


	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}


	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

		

}
