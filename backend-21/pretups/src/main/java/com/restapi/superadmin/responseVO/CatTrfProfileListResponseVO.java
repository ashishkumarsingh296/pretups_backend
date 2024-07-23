package com.restapi.superadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class CatTrfProfileListResponseVO extends BaseResponse{

	private ArrayList catProfileTrfList;

	

	/**
	 * @return the catProfileTrfList
	 */
	public ArrayList getCatProfileTrfList() {
		return catProfileTrfList;
	}



	/**
	 * @param catProfileTrfList the catProfileTrfList to set
	 */
	public void setCatProfileTrfList(ArrayList catProfileTrfList) {
		this.catProfileTrfList = catProfileTrfList;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CatTrfProfileListResponseVO [catProfileTrfList=");
		builder.append(catProfileTrfList);
		builder.append("]");
		return builder.toString();
	}

	
	
}
