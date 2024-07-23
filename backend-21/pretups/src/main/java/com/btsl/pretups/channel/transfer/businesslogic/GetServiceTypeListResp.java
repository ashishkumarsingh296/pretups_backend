package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.profile.businesslogic.ServiceTypeobjVO;

/*
 * @(#)GetServicekeworkListResp.java
 *  get List of service Types
 * 
 * @List<ServicekeywordobjVO>
 *

 */
public class GetServiceTypeListResp extends BaseResponseMultiple {

	private List<ServiceTypeobjVO> listServiceListObj;

	public List<ServiceTypeobjVO> getListServiceListObj() {
		return listServiceListObj;
	}

	public void setListServiceListObj(List<ServiceTypeobjVO> listServiceListObj) {
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
