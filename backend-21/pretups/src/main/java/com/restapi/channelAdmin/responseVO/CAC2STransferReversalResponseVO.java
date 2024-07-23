package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.fasterxml.jackson.annotation.JsonProperty;



public class CAC2STransferReversalResponseVO extends BaseResponse{

	@JsonProperty("transferList")
	private List<ChannelTransferVO> transferList;

	/**
	 * @return the transferList
	 */
	public List<ChannelTransferVO> getTransferList() {
		return transferList;
	}

	/**
	 * @param transferList the transferList to set
	 */
	public void setTransferList(List<ChannelTransferVO> transferList) {
		this.transferList = transferList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CAC2STransferReversalResponseVO [transferList=").append(transferList).append("]");
		return builder.toString();
	}
	
	
	
	
}
