package com.btsl.user.businesslogic;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class GetChannelUsersListResponseVo extends BaseResponseMultiple{
	public ArrayList<GetChannelUsersMsg>channelUsersList;
	public ArrayList<ChannelUserVO>staffUserList;
	public ArrayList<ChannelUserVO> getStaffUserList() {
		return staffUserList;
	}

	public void setStaffUserList(ArrayList<ChannelUserVO> staffUserList) {
		this.staffUserList = staffUserList;
	}

	public ArrayList<GetChannelUsersMsg> getChannelUsersList() {
		return channelUsersList;
	}

	public void setChannelUsersList(ArrayList<GetChannelUsersMsg> channelUsersList) {
		this.channelUsersList = channelUsersList;
	}

	
	
	
	

}
