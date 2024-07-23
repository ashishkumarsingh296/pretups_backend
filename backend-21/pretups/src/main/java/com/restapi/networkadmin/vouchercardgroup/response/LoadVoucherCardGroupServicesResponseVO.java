package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class LoadVoucherCardGroupServicesResponseVO extends BaseResponse {
	private List cardGroupSubServiceList;
	private List serviceTypeList;
}
