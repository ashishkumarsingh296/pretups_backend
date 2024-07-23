package com.restapi.networkadmin.vouchercardgroup.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteVoucherCardGroupRequestVO {

	private String cardGroupSetId;
	private String serviceTypeDesc;
	private String subServiceDesc;
	private String cardGroupSetName;
	
}
