package com.restapi.o2c.service;

import java.util.List;

public class C2CBulkApprovalRequestVO 
{
	List<C2CBulkApprovalData> approvalList;

	public List<C2CBulkApprovalData> getApprovalList() {
		return approvalList;
	}

	public void setApprovalList(List<C2CBulkApprovalData> approvalList) {
		this.approvalList = approvalList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2CBulkApprovalRequestVO [approvalList=");
		builder.append(approvalList);
		builder.append("]");
		return builder.toString();
	}
	
	
}
