package com.restapi.c2s.services;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

public class GetReversalListResponseVO extends BaseResponseMultiple {
	
	List<ChannelTransferVO> responseList;

	public List<ChannelTransferVO> getResponseList() {
		return responseList;
	}

	public void setResponseList(List<ChannelTransferVO> responseList) {
		this.responseList = responseList;
	}

	@Override
	public String toString() {
		return "GetReversalListResponseVO [responseList=" + responseList + ", getResponseList()=" + getResponseList()
				+ ", getSuccessList()=" + getSuccessList() + ", getService()=" + getService() + ", getReferenceId()="
				+ getReferenceId() + ", getStatus()=" + getStatus() + ", getMessageCode()=" + getMessageCode()
				+ ", getMessage()=" + getMessage() + ", getErrorMap()=" + getErrorMap() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}
	
	

}
