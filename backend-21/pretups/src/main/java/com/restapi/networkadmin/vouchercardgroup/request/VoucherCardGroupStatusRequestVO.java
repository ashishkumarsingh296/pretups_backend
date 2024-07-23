package com.restapi.networkadmin.vouchercardgroup.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoucherCardGroupStatusRequestVO {
	private String cardGroupSetName;
	private String serviceType;
	private String subServiceType;
	private String setType;
	private String version;
}
