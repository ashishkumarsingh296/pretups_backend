package com.restapi.superadmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ApprovalOperatorUsersRequestVO {

	
	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("loginID")
	private String loginID;
	
	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("category")
	private String category;

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
}
