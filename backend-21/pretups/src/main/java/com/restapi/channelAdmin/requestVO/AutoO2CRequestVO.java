package com.restapi.channelAdmin.requestVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AutoO2CRequestVO {

	private String msisdn ;
	private String loginID ;
	private String userID;
	private String autoO2CAllowed;
	private String o2cTxnAmount;
	private String o2cThresholdLimit ;

}
