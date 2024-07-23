package com.restapi.networkadmin.vouchercardgroup.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoucherCardGroupTransferValueRequestVO {
	private String moduleType;
	private String gateway;
	private String serviceType;
	private String serviceDesc;
	private String subserviceType;
	private String receiverType;	
	private String receiverServiceClass;
	private String voucherType;
	private String voucherSegment;
	private String denomination;
	private String profile;
	private String applicableFromHour;
	private String applicableFromDate;
	private String oldValidityDate;
}
