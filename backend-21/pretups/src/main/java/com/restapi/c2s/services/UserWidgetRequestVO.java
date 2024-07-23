package com.restapi.c2s.services;

import java.util.List;



public class UserWidgetRequestVO  {


	@io.swagger.v3.oas.annotations.media.Schema( required = true, description ="Widget List")
	private List<String> WigetList = null;

	public List<String> getWigetList() {
		return WigetList;
	}

	public void setWigetList(List<String> wigetList) {
		WigetList = wigetList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserWidgetRequestVO [WigetList=");
		builder.append(WigetList);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
