package com.restapi.channelenquiry.service;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;

public class BatchC2cTransferResponseVO extends BaseResponseMultiple{

	private List<C2CBatchMasterVO> transferList;

	public BatchC2cTransferResponseVO() {

	}
	
	public List<C2CBatchMasterVO> getTransferList() {
		return transferList;
	}
	public void setTransferList(List<C2CBatchMasterVO> transferList) {
		this.transferList = transferList;
	}

	@Override
	public String toString() {
		return "BatchC2cTransferResponseVO [transferList=" + transferList + "]";
	}
}
