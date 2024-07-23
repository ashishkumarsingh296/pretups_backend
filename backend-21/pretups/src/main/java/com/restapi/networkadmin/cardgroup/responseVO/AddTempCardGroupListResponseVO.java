package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class AddTempCardGroupListResponseVO extends BaseResponse{

	private ArrayList<CardGroupDetailsVO> tempCardGroupList;

	
	@Override
	public String toString() {
		final StringBuilder sbd = new StringBuilder("AddTempCardGroupListResponseVO ");
		 sbd.append("AddTempCardGroupListResponseVO [");
		 sbd.append("tempCardGroupList=").append(tempCardGroupList);
		 sbd.append("]");
		    
		return sbd.toString();
	}
			
}
