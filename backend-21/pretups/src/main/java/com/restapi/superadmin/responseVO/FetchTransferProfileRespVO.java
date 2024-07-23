package com.restapi.superadmin.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;

public class FetchTransferProfileRespVO  extends BaseResponse{
	
	private TransferProfileVO transferProfileVO ;

	public TransferProfileVO getTransferProfileVO() {
		return transferProfileVO;
	}

	public void setTransferProfileVO(TransferProfileVO transferProfileVO) {
		this.transferProfileVO = transferProfileVO;
	}



}
