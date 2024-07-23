package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.user.businesslogic.ViewOfflineReportStatusVO;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class ViewAllOfflineRptStatusRespVO extends BaseResponseMultiple {

	private List<ViewOfflineReportStatusVO> offlineReportStatusList;

	public List<ViewOfflineReportStatusVO> getOfflineReportStatusList() {
		return offlineReportStatusList;
	}

	public void setOfflineReportStatusList(List<ViewOfflineReportStatusVO> offlineReportStatusList) {
		this.offlineReportStatusList = offlineReportStatusList;
	}

	@Override
	public String toString() {
		return "[ ViewAllOfflineRptStatusRespVO  ]";
	}

}
