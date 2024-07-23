package com.restapi.networkadmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadServiceAndProductListResponseVO  extends BaseResponse{
	private List serviceList;
	private List productList;
	
	

}
