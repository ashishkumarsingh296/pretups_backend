package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

public class AllDomainsResponseVO extends BaseResponse{

	List<ListValueVO> domains;

	public List<ListValueVO> getDomains() {
		return domains;
	}

	public void setDomains(List<ListValueVO> domains) {
		this.domains = domains;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllDomainsResponseVO [domains=").append(domains).append("]");
		return builder.toString();
	}
	
	
}
