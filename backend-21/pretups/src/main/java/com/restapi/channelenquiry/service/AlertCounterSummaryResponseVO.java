package com.restapi.channelenquiry.service;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.transfer.businesslogic.UserZeroBalanceCounterSummaryVO;

public class AlertCounterSummaryResponseVO extends BaseResponseMultiple{
	private ArrayList<UserZeroBalanceCounterSummaryVO> alertList;

	public ArrayList<UserZeroBalanceCounterSummaryVO> getAlertList() {
		return alertList;
	}

	public void setAlertList(ArrayList<UserZeroBalanceCounterSummaryVO> alertList) {
		this.alertList = alertList;
	}
}
