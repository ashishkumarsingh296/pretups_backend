package com.restapi.superadmin.responseVO;

import com.btsl.common.BaseResponse;

public class TransferProfileLoadRespVO  extends BaseResponse{
	
	private TransferProfileFormVO transferProfileFormVO ;

	public TransferProfileFormVO getTransferProfileFormVO() {
		return transferProfileFormVO;
	}

	public void setTransferProfileFormVO(TransferProfileFormVO transferProfileFormVO) {
		this.transferProfileFormVO = transferProfileFormVO;
	}
	

}
