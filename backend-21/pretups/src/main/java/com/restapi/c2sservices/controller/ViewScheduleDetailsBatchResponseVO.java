package com.restapi.c2sservices.controller;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;

public class ViewScheduleDetailsBatchResponseVO extends BaseResponseMultiple {
	ArrayList<ScheduleBatchMasterVO> scheduleDetailsList = null;

	public ArrayList<ScheduleBatchMasterVO> getScheduleDetailList() {
		return scheduleDetailsList;
	}

	public void setScheduleDetailList(ArrayList<ScheduleBatchMasterVO> scheduleDetailsList) {
		this.scheduleDetailsList = scheduleDetailsList;
	}

	@Override
	public String toString() {
		return "ViewScheduleDetailsListResponseVO [scheduleDetailList=" + scheduleDetailsList + "]";
	}
}
