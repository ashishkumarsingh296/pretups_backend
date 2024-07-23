package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

public class GetParentListResponseVO extends BaseResponse{

	List<ListValueVO> parentList;

	public List<ListValueVO> getParentList() {
		return parentList;
	}

	public void setParentList(List<ListValueVO> parentList) {
		this.parentList = parentList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("getParentListResponseVO [parentList=").append(parentList).append("]");
		return builder.toString();
	}

}
