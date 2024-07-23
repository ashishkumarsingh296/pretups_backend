package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class GeoDomainTypeListResponseVO extends BaseResponse{

	ArrayList<ListValueVO> geoDomTypeList = new ArrayList<>();

	
}
