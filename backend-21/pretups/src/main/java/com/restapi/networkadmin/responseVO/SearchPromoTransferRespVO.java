package com.restapi.networkadmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

public class SearchPromoTransferRespVO extends BaseResponse{
	
	
	
	private List<PromoTransferSearchVO> listSearchPromoData;
	
	public SearchPromoTransferRespVO() {
		
	}

	public List<PromoTransferSearchVO> getListSearchPromoData() {
		return listSearchPromoData;
	}

	public void setListSearchPromoData(List<PromoTransferSearchVO> listSearchPromoData) {
		this.listSearchPromoData = listSearchPromoData;
	}

	
}