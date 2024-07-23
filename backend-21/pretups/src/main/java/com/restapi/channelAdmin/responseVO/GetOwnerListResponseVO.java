package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;

public class GetOwnerListResponseVO extends BaseResponse{

	List<UserVO> ownerList;

	public List<UserVO> getOwnerList() {
		return ownerList;
	}

	public void setOwnerList(List<UserVO> ownerList) {
		this.ownerList = ownerList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("getOwnerListResponseVO [ownerList=").append(ownerList).append("]");
		return builder.toString();
	}

	
}
