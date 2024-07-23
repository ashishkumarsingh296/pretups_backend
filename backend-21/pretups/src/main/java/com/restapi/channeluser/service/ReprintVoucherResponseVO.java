package com.restapi.channeluser.service;

import java.util.List;

import com.btsl.common.BaseResponse;

public class ReprintVoucherResponseVO extends BaseResponse{
	
	List<VoucherVO> voucherListNew;

	public List<VoucherVO> getVoucherList() {
		return voucherListNew;
	}

	public void setVoucherList(List<VoucherVO> voucherList) {
		this.voucherListNew = voucherList;
	}
	
	
	
}
