package com.restapi.channelenquiry.service;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

public class C2cAndO2cEnquiryResponseVO extends BaseResponseMultiple {
	
	private ArrayList<ChannelTransferVO> transferList;
	private int transferListSize;

	public int getTransferListSize() {
		return transferListSize;
	}

	public void setTransferListSize(int transferListSize) {
		this.transferListSize = transferListSize;
	}

	public ArrayList<ChannelTransferVO> getTransferList() {
		return transferList;
	}

	public void setTransferList(ArrayList<ChannelTransferVO> transferList) {
		this.transferList = transferList;
	}

	@Override
	public String toString() {
		return "C2cAndO2cEnquiryResponseVO [transferList=" + transferList + ", transferListSize=" + transferListSize
				+ "]";
	}
	
	
	

}
