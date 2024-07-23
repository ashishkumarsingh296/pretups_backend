package com.btsl.pretups.domain.businesslogic;


import java.util.List;

import com.btsl.common.BaseResponseMultiple;

public class CategoryDomainCodeVO extends BaseResponseMultiple{
	List<CategoryVO> domainListNew;

	public List<CategoryVO> getDomainList() {
		return domainListNew;
	}

	public void setDomainList(List<CategoryVO> domainList) {
		this.domainListNew = domainList;
	}

	@Override
	public String toString() {
		return "CategoryDomainCodeVO [domainList=" + domainListNew + "]";
	}

	

}
