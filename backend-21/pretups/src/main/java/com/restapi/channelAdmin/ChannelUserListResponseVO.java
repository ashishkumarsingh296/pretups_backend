package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;

public class ChannelUserListResponseVO extends BaseResponse {

	public ArrayList<GetChannelUsersMsg> channelUsersList;

	public ArrayList<GetChannelUsersMsg> getChannelUsersList() {
		return channelUsersList;
	}

	public void setChannelUsersList(ArrayList<GetChannelUsersMsg> channelUsersList) {
		this.channelUsersList = channelUsersList;
	}

}