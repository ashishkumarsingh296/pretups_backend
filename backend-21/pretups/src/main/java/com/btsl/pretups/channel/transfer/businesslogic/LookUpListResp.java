package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ListValueVO;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class LookUpListResp extends BaseResponseMultiple {

	
	private List<ListValueVO> listDetails = new ArrayList<ListValueVO>();
	
	public List<ListValueVO> getListDetails() {
		return listDetails;
	}
	
	public void setListDetails(List<ListValueVO> listDetails) {
		this.listDetails = listDetails;
	}



	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" LookUpListResp : [  :")

				.append("]");
		return sb.toString();
	}

}
