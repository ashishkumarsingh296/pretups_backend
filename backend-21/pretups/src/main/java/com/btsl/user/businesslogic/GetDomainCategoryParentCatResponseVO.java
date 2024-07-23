package com.btsl.user.businesslogic;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class GetDomainCategoryParentCatResponseVO extends BaseResponse {
	String loggedInUserDomainCode;
	String loggedInUserDomainName;
	String loggedInUserCatCode;
	String loggedInUserCatName;
	String ownerName;
	String ownerMsisdn;
	String ownerLoginId;
	String ownerUserId;
	String userStatus;
	
	public ArrayList<GetDomainCatParentCatResp1Msg>data;
	
	public String getLoggedInUserDomainCode() {
		return loggedInUserDomainCode;
	}
	public void setLoggedInUserDomainCode(String loggedInUserDomainCode) {
		this.loggedInUserDomainCode = loggedInUserDomainCode;
	}
	
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getLoggedInUserDomainName() {
		return loggedInUserDomainName;
	}
	public void setLoggedInUserDomainName(String loggedInUserDomainName) {
		this.loggedInUserDomainName = loggedInUserDomainName;
	}
	public String getLoggedInUserCatCode() {
		return loggedInUserCatCode;
	}
	public void setLoggedInUserCatCode(String loggedInUserCatCode) {
		this.loggedInUserCatCode = loggedInUserCatCode;
	}
	public String getLoggedInUserCatName() {
		return loggedInUserCatName;
	}
	public void setLoggedInUserCatName(String loggedInUserCatName) {
		this.loggedInUserCatName = loggedInUserCatName;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public ArrayList<GetDomainCatParentCatResp1Msg> getData() {
		return data;
	}
	public void setData(ArrayList<GetDomainCatParentCatResp1Msg> data) {
		this.data = data;
	}
	public String getOwnerMsisdn() {
		return ownerMsisdn;
	}
	public void setOwnerMsisdn(String ownerMsisdn) {
		this.ownerMsisdn = ownerMsisdn;
	}
	public String getOwnerLoginId() {
		return ownerLoginId;
	}
	public void setOwnerLoginId(String ownerLoginId) {
		this.ownerLoginId = ownerLoginId;
	}
	public String getOwnerUserId() {
		return ownerUserId;
	}
	public void setOwnerUserId(String ownerUserId) {
		this.ownerUserId = ownerUserId;
	}
	
	
	
	
	
	

}
