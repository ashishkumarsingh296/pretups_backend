package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ViewRestrictedResponseVO {
	private String subscriberName;
	private String subscriberCode;
	private String subscriberMobileNumber;
	private String blockListStatus;
	private String rechargeAllow;
	private String minTransactionAmount;
	private String maxTransactionAmount;
	private String monthlyTransactionLimit;
	private String monthlyTransactionAmount;
	private String totalTransactionAmount;
	private String totalTransactionCount;
	private String status;
	private String p2pSender;
	private String p2pReciver;
	
	
	
	
	
}
