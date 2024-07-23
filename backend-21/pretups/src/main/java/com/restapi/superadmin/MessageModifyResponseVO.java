package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageModifyResponseVO extends BaseResponse {

	public ArrayList requestGatewayList;
	public ArrayList responseGatewayList;
}
