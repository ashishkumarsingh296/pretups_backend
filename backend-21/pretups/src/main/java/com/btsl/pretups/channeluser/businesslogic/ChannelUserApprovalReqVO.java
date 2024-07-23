package com.btsl.pretups.channeluser.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserApprovalReqVO extends AddChannelUserRequestVO {

	@JsonProperty("userAction")
	private String userAction;
	
	@JsonProperty("approvalLevel")
	private String approvalLevel;   
	
	@JsonProperty("approveUserID")
	private String approveUserID;

	@JsonProperty("approveUserID")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGSB0000001349", required = true/* , defaultValue = "" */, description = "UserID to be approved")
	public String getApproveUserID() {
		return approveUserID;
	}

	public void setApproveUserID(String approveUserID) {
		this.approveUserID = approveUserID;
	}

	@JsonProperty("approvalLevel")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NEW/APPRV1/APPRV2", required = true/* , defaultValue = "" */, description = "Other Email")
	public String getApprovalLevel() {
		return approvalLevel;
	}

	public void setApprovalLevel(String approvalLevel) {
		this.approvalLevel = approvalLevel;
	}

	@JsonProperty("userAction")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ADD/EDIT", required = true/* , defaultValue = "" */, description = "ADD/EDIT")
	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

}
