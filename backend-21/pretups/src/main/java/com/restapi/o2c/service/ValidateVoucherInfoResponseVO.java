package com.restapi.o2c.service;

import com.btsl.common.BaseResponse;

public class ValidateVoucherInfoResponseVO extends BaseResponse {
	
	int voucherCount;
	
	public int getVoucherCount() {
		return voucherCount;
	}

	public void setVoucherCount(int voucherCount) {
		this.voucherCount = voucherCount;
	}


}
