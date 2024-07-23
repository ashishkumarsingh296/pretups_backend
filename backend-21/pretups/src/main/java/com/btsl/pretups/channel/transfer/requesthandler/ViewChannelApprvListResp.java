package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.user.businesslogic.ViewOfflineReportStatusVO;
import com.btsl.user.businesslogic.UserApprovalVO;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class ViewChannelApprvListResp extends BaseResponseMultiple {

	List<UserApprovalVO> listApprovalList;


	public List<UserApprovalVO> getListApprovalList() {
		return listApprovalList;
	}


	public void setListApprovalList(List<UserApprovalVO> listApprovalList) {
		this.listApprovalList = listApprovalList;
	}


	@Override
	public String toString() {
		return "[ listApprovalList  ]";
	}

}
