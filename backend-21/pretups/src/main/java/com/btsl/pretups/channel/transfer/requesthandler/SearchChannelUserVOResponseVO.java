package com.btsl.pretups.channel.transfer.requesthandler;


import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class SearchChannelUserVOResponseVO extends BaseResponseMultiple {

	private ChannelUserVO channelUserVO;
	private ArrayList ownerUsersList;
	
	
	public ArrayList getOwnerUsersList() {
		return ownerUsersList;
	}



	public void setOwnerUsersList(ArrayList ownerUsersList) {
		this.ownerUsersList = ownerUsersList;
	}
private ArrayList channelUsersList;
	
	
	public ArrayList getChannelUsersList() {
		return channelUsersList;
	}

    private ArrayList prodList;
	
	
	public ArrayList getProdList() {
		return prodList;
	}



	public void setProdList(ArrayList prodList) {
		this.prodList = prodList;
	}


	public void setChannelUsersList(ArrayList channelUsersList) {
		this.channelUsersList = channelUsersList;
	}



	public ChannelUserVO getChannelUserVO() {
		return channelUserVO;
	}



	public void setChannelUserVO(ChannelUserVO channelUserVO) {
		this.channelUserVO = channelUserVO;
	}



	@Override
	public String toString() {
		return "ChannelUserVO [ChannelUserVO]" + channelUserVO+"OwnerUsersList [OwnerUsersList]"+ownerUsersList+"ChannelUsersList [ChannelUsersList]"+channelUsersList+"ProductList [ProductList]"+prodList;
	}
}
