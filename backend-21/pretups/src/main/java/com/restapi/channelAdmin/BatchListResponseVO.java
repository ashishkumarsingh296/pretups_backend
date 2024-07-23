package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class BatchListResponseVO extends BaseResponse {

	public ArrayList<VomsVoucherVO> batchIdList;

	public ArrayList<VomsVoucherVO> getBatchIdList() {
		return batchIdList;
	}

	public void setBatchIdList(ArrayList<VomsVoucherVO> batchIdList) {
		this.batchIdList = batchIdList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BatchListResponseVO [batchIdList=");
		builder.append(batchIdList);
		builder.append("]");
		return builder.toString();
	}

}
