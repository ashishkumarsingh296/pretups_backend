package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.user.businesslogic.UserMsisdnUserIDVO;
/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class FetchUserNameAutoSearchRespVO extends BaseResponseMultiple {
	
	
	private List<UserMsisdnUserIDVO>   userList;

	public List<UserMsisdnUserIDVO> getUserList() {
		return userList;
	}

	public void setUserList(List<UserMsisdnUserIDVO> userList) {
		this.userList = userList;
	}
	@Override
	public String toString() {
		return "[ FetchUserNameAutoSearchVO  ]";
	}
	

	
	
}
