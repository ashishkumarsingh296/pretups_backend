package com.restapi.networkadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2STransferRuleResponseVO1 extends BaseResponse {

	public ArrayList serviceClassList;
	public ArrayList subServiceTypeList;
	public ArrayList<ListValueVO> cardGroupList;
	public ArrayList serviceTypeList;
	public ArrayList<ListValueVO> gatewayList;
	

}
