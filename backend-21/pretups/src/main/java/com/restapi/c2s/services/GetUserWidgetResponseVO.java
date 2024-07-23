package com.restapi.c2s.services;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;


public class GetUserWidgetResponseVO  extends BaseResponse{

	public ArrayList<String> getWidgetList() {
		return widgetList;
	}

	public void setWidgetList(ArrayList<String> widgetList) {
		this.widgetList = widgetList;
	}

	ArrayList<String> widgetList;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetUserWidgetResponseVO [widgetList=");
		builder.append(widgetList);
		builder.append("]");
		return builder.toString();
	}
	
	
}
