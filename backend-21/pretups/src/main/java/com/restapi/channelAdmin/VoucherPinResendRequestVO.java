package com.restapi.channelAdmin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherPinResendRequestVO {

	private String transactionid;
	private String date;
	private String subscriberMsisdn;
	private String serialNo;
	private String customerMsisdn;
	private String requestGatewayCode;
	private String remarks;
}
