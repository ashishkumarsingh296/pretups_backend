package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class C2SCardGroupSetNameListResponseVO extends BaseResponse{
	ArrayList cardGroupSetNameList;
	String currentDefaultCardGroup;
	
	
	

}
