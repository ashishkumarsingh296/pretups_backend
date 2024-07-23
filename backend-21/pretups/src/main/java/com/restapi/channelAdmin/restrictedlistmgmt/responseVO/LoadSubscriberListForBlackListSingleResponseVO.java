package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoadSubscriberListForBlackListSingleResponseVO extends BaseResponse{
	private String isBlackListed;
	
	private String cp2pPayer;
	private String cp2pPayee;
	private String c2sPayee;
	
	private String subscriberCode;
	private String subscriberName;
	private String minTransferAmount;
	private String maxTransferAmount;
	private String totalTxnCount;
	private String totalTxnAmount;
	private String monthlyTransferLimit;
	
	private String subscriberStatus;
	private String registeredOn;
	
}
