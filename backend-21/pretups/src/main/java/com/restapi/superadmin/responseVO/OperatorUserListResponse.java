package com.restapi.superadmin.responseVO;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.domain.businesslogic.CategoryVO;

public class OperatorUserListResponse extends BaseResponse{
	
	public ArrayList viewOperatorUser;
	List <CategoryVO>categoryList = new ArrayList<CategoryVO>();
	public CategoryVO categoryVO;
	

	
	public CategoryVO getCategoryVO() {
		return categoryVO;
	}

	public void setCategoryVO(CategoryVO categoryVO) {
		this.categoryVO = categoryVO;
	}

	public List<CategoryVO> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategoryVO> categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList getViewOperatorUser() {
		return viewOperatorUser;
	}

	public void setViewOperatorUser(ArrayList viewOperatorUser) {
		this.viewOperatorUser = viewOperatorUser;
	}
	
	

	
	
	

}
