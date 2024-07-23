package com.restapi.channelAdmin.requestVO;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;



public class SuspendResumeUserHierarchyRequestVO {

	@JsonProperty
    @io.swagger.v3.oas.annotations.media.Schema(example = "[ddealer , ddealer2]")
	ArrayList<String> loginIdList;
	
	@JsonProperty
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y or S")
	String requestType;

	public ArrayList<String> getLoginIdList() {
		return loginIdList;
	}

	public void setLoginIdList(ArrayList<String> loginIdList) {
		this.loginIdList = loginIdList;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("suspendResumeUserHierarchyRequestVO [loginIdList=");
		builder.append(loginIdList);
		builder.append(", requestType=");
		builder.append(requestType);
		builder.append("]");
		return builder.toString();
	}

	
}
