package com.restapi.superadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;

public class GradeTypeListResponseVO extends BaseResponse{
	
	public ArrayList<DomainVO> domainList;
	public ArrayList<ListValueVO> categoryList;
	public ArrayList<GradeVO> gradeList;
	
	

	public ArrayList<DomainVO> getDomainList() {
		return domainList;
	}



	public void setDomainList(ArrayList<DomainVO> domainList) {
		this.domainList = domainList;
	}



	public ArrayList<ListValueVO> getCategoryList() {
		return categoryList;
	}



	public void setCategoryList(ArrayList<ListValueVO> categoryList) {
		this.categoryList = categoryList;
	}

	

	public ArrayList<GradeVO> getGradeList() {
		return gradeList;
	}



	public void setGradeList(ArrayList<GradeVO> gradeList) {
		this.gradeList = gradeList;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GradeTypeListResponseVO [domainList=");
		builder.append(domainList);
		builder.append("]");
		builder.append("GradeTypeListResponseVO [categoryList=");
		builder.append(categoryList);
		builder.append("]");
		return builder.toString();
	}
	
}
