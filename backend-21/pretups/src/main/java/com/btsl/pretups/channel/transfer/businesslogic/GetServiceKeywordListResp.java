package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordVO;

/*
 * @(#)GetServiceKeywordListResp.java
 *  get List of service keywords
 * 
 * @List<ServiceKeywordobjVO>
 *

 */
public class GetServiceKeywordListResp extends BaseResponseMultiple {

	private List<ServiceKeywordVO> listServiceListObj;

	public List<ServiceKeywordVO> getListServiceListObj() {
		return listServiceListObj;
	}

	public void setListServiceListObj(List<ServiceKeywordVO> listServiceListObj) {
		this.listServiceListObj = listServiceListObj;
	}

	@Override	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" GetServiceKeywordListResp : [ listServiceListObj :");
		sb.append(listServiceListObj);
		sb.append("]");
		return sb.toString();
	}

}
