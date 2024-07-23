package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ApprovalRestrictedListResponseVO extends BaseResponse{
	private List<ApprovalRestrictedDeatils> approvalRestrictedDetailsList;
	private String statusDes;
	
	
	
	
}
