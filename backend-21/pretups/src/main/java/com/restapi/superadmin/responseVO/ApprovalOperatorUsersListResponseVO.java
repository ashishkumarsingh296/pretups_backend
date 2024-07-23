package com.restapi.superadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;

public class ApprovalOperatorUsersListResponseVO extends BaseResponse{
	
	public ArrayList approvalOperatorUsersList;

	public ArrayList getApprovalOperatorUsersList() {
		return approvalOperatorUsersList;
	}

	public void setApprovalOperatorUsersList(ArrayList approvalOperatorUsersList) {
		this.approvalOperatorUsersList = approvalOperatorUsersList;
	}
	
	
	
	
}
