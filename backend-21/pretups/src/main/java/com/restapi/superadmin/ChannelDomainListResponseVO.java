package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class ChannelDomainListResponseVO extends BaseResponse{

	public ArrayList channelDomainList;

	public ArrayList getChannelDomainList() {
		return channelDomainList;
	}

	public void setChannelDomainList(ArrayList channelDomainList) {
		this.channelDomainList = channelDomainList;
	}
	
	
}
