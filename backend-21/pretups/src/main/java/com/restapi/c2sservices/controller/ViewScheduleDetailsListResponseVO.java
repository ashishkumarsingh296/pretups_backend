package com.restapi.c2sservices.controller;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;

public class ViewScheduleDetailsListResponseVO extends BaseResponseMultiple{
	
	ArrayList<ScheduleBatchDetailVO> scheduleDetailsList;

	public ArrayList<ScheduleBatchDetailVO> getScheduleDetailList() {
		return scheduleDetailsList;
	}

	public void setScheduleDetailList(ArrayList<ScheduleBatchDetailVO> scheduleDetailsList) {
		this.scheduleDetailsList = scheduleDetailsList;
	}

	@Override
	public String toString() {
		return "ViewScheduleDetailsListResponseVO [scheduleDetailList=" + scheduleDetailsList + "]";
	}

}
