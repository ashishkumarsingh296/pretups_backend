package com.restapi.superadmin.responseVO;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;

public class TransferProfileSearchRespVO extends BaseResponse {

	public TransferProfileSearchRespVO() {

	}
	
	private boolean noCategoryLevel;

	List<TransferProfileVO> transferProfileList = new ArrayList<TransferProfileVO>();

	public List<TransferProfileVO> getTransferProfileList() {
		return transferProfileList;
	}

	public void setTransferProfileList(List<TransferProfileVO> transferProfileList) {
		this.transferProfileList = transferProfileList;
	}

	public boolean isNoCategoryLevel() {
		return noCategoryLevel;
	}

	public void setNoCategoryLevel(boolean noCategoryLevel) {
		this.noCategoryLevel = noCategoryLevel;
	}

}
