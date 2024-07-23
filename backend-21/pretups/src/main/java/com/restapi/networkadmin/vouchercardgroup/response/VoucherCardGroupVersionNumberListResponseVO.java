package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoucherCardGroupVersionNumberListResponseVO extends BaseResponse{
	
	    private String cardGroupSetID;
		private List versionList; 
}
