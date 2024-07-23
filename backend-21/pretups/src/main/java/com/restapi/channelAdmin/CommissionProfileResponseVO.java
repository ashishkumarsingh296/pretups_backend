package com.restapi.channelAdmin;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommissionProfileResponseVO extends BaseResponse {
	@JsonProperty("commissionProfileList")
	List<CommissionProfileSetVO> commissionProfileList;

	public List<CommissionProfileSetVO> getCommissionProfileList() {
		return commissionProfileList;
	}

	public void setCommissionProfileList(List<CommissionProfileSetVO> commissionProfileList) {
		this.commissionProfileList = commissionProfileList;
	}

	
}
