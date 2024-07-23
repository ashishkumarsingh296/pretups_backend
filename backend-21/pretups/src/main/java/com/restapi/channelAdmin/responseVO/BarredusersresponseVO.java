package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class BarredusersresponseVO extends BaseResponseMultiple{
private List<ChannelUserVO> usersList;
	
	public List<ChannelUserVO> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<ChannelUserVO> usersList) {
		this.usersList = usersList;

}
}