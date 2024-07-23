package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;

public class GeoDomainCatResponse extends BaseResponseMultiple {
	
	private ArrayList geoList;
	private ArrayList channelDomainList;
	private ArrayList categoryList;
	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList getChannelDomainList() {
		return channelDomainList;
	}

	public void setChannelDomainList(ArrayList channelDomainList) {
		this.channelDomainList = channelDomainList;
	}

	public ArrayList getGeoList() {
		return geoList;
	}

	public void setGeoList(ArrayList geoList) {
		this.geoList = geoList;
	}
	
	@Override
	public String toString() {
		return "ChannelUserVO [ChannelUserVO]" + geoList+"OwnerUsersList [OwnerUsersList]"+channelDomainList+"ChannelUsersList [ChannelUsersList]"+categoryList;
	}
}
