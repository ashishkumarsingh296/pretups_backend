package com.restapi.networkadmin.servicePrdMapping.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingVO;

public class SearchServicePrdMappingRespVO extends BaseResponse {
	
	private List<SelectorAmountMappingVO> searchServicePrdlist;

	public List<SelectorAmountMappingVO> getSearchServicePrdlist() {
		return searchServicePrdlist;
	}

	public void setSearchServicePrdlist(List<SelectorAmountMappingVO> searchServicePrdlist) {
		this.searchServicePrdlist = searchServicePrdlist;
	}
	
	
	
	

}
