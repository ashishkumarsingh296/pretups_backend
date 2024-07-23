package com.restapi.channelAdmin.restrictedlistmgmt.requestVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.ApprovalRestrictedDeatils;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ApprovalRestrictedListResponseVO extends BaseResponse{
	private List<ApprovalRestrictedDeatils> approvalRestrictedDetailsList;
	
	
	
}
