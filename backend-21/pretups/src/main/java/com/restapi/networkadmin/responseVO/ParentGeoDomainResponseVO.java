package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ParentGeoDomainResponseVO extends BaseResponse {
	ArrayList parentDomainList = new ArrayList<>();
	private String parentDomainType;
	
	
	
}
