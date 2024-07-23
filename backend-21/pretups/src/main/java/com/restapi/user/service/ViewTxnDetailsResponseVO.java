package com.restapi.user.service;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ViewTxnDetailsResponseVO  extends BaseResponse{

	@JsonProperty("Txn Details")
    private ChannelTransferVO dataObj = new ChannelTransferVO();

	public ChannelTransferVO getDataObj() {
		return dataObj;
	}

	public void setDataObj(ChannelTransferVO dataObj) {
		this.dataObj = dataObj;
	}


}
