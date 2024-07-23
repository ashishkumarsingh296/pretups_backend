package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;

public class ParentCategoryListResponseVO extends BaseResponse{

	List<CategoryVO> parentCategoryList;
	boolean notApplicable=false;

	public List<CategoryVO> getParentCategoryList() {
		return parentCategoryList;
	}

	public void setParentCategoryList(List<CategoryVO> parentCategoryList) {
		this.parentCategoryList = parentCategoryList;
	}

	public boolean isNotApplicable() {
		return notApplicable;
	}

	public void setNotApplicable(boolean notApplicable) {
		this.notApplicable = notApplicable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetParentCategoryListResponseVO [parentCategoryList=").append(parentCategoryList)
				.append(", notApplicable=").append(notApplicable).append("]");
		return builder.toString();
	}
}
