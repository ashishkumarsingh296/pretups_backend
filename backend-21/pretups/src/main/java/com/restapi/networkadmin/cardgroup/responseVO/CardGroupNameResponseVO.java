package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;

public class CardGroupNameResponseVO extends BaseResponse {
	
	
	private List<CardGroupSetVO> cardGroupNameList;

	public List<CardGroupSetVO> getCardGroupNameList() {
		return cardGroupNameList;
	}

	public void setCardGroupNameList(List<CardGroupSetVO> cardGroupNameList) {
		this.cardGroupNameList = cardGroupNameList;
	}
	
	
	
	

}
