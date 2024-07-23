package com.restapi.channeluser.service;

import com.btsl.user.businesslogic.UserVO;

public class OwnerParentInfoVO {
	
	private UserVO ownwerVO;
	private UserVO parentVO;
	public UserVO getOwnwerVO() {
		return ownwerVO;
	}
	public void setOwnwerVO(UserVO ownwerVO) {
		this.ownwerVO = ownwerVO;
	}
	public UserVO getParentVO() {
		return parentVO;
	}
	public void setParentVO(UserVO parentVO) {
		this.parentVO = parentVO;
	}
	

}
