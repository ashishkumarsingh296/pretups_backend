package com.restapi.channelAdmin.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.web.pretups.channel.transfer.web.ChannelTransferEnquiryModel;

public class O2CTransferDetailsResponseVO extends BaseResponse{
	
	private ChannelTransferEnquiryModel transferDetails;
	private ChannelTransferVO channelTransferVO;

	public ChannelTransferEnquiryModel getTransferDetails() {
		return transferDetails;
	}

	public void setTransferDetails(ChannelTransferEnquiryModel transferDetails) {
		this.transferDetails = transferDetails;
	}
	

	public ChannelTransferVO getChannelTransferVO() {
		return channelTransferVO;
	}

	public void setChannelTransferVO(ChannelTransferVO channelTransferVO) {
		this.channelTransferVO = channelTransferVO;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CTransferDetailsResponseVO [transferDetails=");
		builder.append(transferDetails);
		builder.append(", channelTransferVO=");
		builder.append(channelTransferVO);
		builder.append("]");
		return builder.toString();
	}

	
	
	
	
	
	
	

}
