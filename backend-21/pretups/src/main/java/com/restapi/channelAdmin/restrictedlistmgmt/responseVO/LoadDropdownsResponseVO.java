package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class LoadDropdownsResponseVO extends BaseResponse{
	private ArrayList geoDomainList;
	private ArrayList domainList;
	private int domainListSize;
	private ArrayList categoryList;
	
	
}
