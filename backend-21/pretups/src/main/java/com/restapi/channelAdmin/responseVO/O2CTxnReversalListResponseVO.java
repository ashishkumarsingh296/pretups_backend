package com.restapi.channelAdmin.responseVO;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

public class O2CTxnReversalListResponseVO extends BaseResponseMultiple{

	List<ChannelTransferVO> o2CTxnReversalList = new ArrayList<>();
	int o2CTxnReversalListSize;
	
	public List<ChannelTransferVO> getO2CTxnReversalList() {
		return o2CTxnReversalList;
	}

	public void setO2CTxnReversalList(List<ChannelTransferVO> o2cTxnReversalList) {
		o2CTxnReversalList = o2cTxnReversalList;
	}

	public int getO2CTxnReversalListSize() {
		return o2CTxnReversalListSize;
	}

	public void setO2CTxnReversalListSize(int o2cTxnReversalListSize) {
		o2CTxnReversalListSize = o2cTxnReversalListSize;
	}
	
	
}
