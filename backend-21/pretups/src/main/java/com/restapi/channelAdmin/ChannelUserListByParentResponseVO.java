package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelUserDTO;

public class ChannelUserListByParentResponseVO extends BaseResponse {

	public ArrayList<ChannelUserDTO> channelUsersList;

	public ArrayList<ChannelUserDTO> getChannelUsersList() {
		return channelUsersList;
	}

	public void setChannelUsersList(ArrayList<ChannelUserDTO> channelUsersList) {
		this.channelUsersList = channelUsersList;
	}

}