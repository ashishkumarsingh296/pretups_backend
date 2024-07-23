package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;


public class DomainListResponseVO extends BaseResponse {
	
	public ArrayList<ListValueVO> domainTypeList;

	public ArrayList<ListValueVO> getDomainTypeList() {
		return domainTypeList;
	}


	public void setDomainTypeList(ArrayList<ListValueVO> domainTypeList) {
		this.domainTypeList = domainTypeList;
	}


	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DomainListResponseVO [domainTypeList=");
		builder.append(domainTypeList);
		builder.append("]");
		return builder.toString();
	}

}
