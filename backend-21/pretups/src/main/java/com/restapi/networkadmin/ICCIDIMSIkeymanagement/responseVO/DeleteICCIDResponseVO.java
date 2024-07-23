package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteICCIDResponseVO extends BaseResponse {
	private String iccID;
	private Boolean isDeleteReconfirm;
}
