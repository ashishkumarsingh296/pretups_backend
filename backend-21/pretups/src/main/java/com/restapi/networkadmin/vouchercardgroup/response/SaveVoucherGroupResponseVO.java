package com.restapi.networkadmin.vouchercardgroup.response;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveVoucherGroupResponseVO extends BaseResponse {
	private String isVersionCreated;
	private String cardGroupSetId;
}
