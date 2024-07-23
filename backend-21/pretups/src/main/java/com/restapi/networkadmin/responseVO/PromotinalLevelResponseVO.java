package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
public class PromotinalLevelResponseVO extends BaseResponse {
	

	public ArrayList getPromotionalLevelList() {
		return promotionalLevelList;
	}

	public void setPromotionalLevelList(ArrayList promotionalLevelList) {
		this.promotionalLevelList = promotionalLevelList;
	}

	ArrayList promotionalLevelList;
	
	@Override
	public String toString() {
		return "PromotinalLevelResponseVO [promotionalLevelList=" + promotionalLevelList + "]";
	}

}
