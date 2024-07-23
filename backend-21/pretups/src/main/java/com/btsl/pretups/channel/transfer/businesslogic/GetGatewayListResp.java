package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;

/*
 * @(#)GetServicekeworkListResp.java
 *  get List of service Types
 * 
 * @List<ServicekeywordobjVO>
 *

 */
public class GetGatewayListResp extends BaseResponseMultiple {

	private ArrayList listServiceListObj;

	public ArrayList getListServiceListObj() {
		return listServiceListObj;
	}

	public void setListServiceListObj(ArrayList listServiceListObj) {
		this.listServiceListObj = listServiceListObj;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" GetServicekeworkListResp : [ listServiceListObj :");
		sb.append(listServiceListObj);
		sb.append("]");
		return sb.toString();
	}

}
