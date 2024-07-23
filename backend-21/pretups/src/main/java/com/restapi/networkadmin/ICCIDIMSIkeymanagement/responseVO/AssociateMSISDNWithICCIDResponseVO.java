package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssociateMSISDNWithICCIDResponseVO extends BaseResponse {
	private String previousSwapedKeyIccID;
	private String iccID;
	private String MSISDN;
	private Boolean isReAssociate;

}
