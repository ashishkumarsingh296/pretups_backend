package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import java.util.ArrayList;
import com.btsl.common.BaseResponse;

import lombok.Data;
@Data
public class CorrectMSISDNWithICCIDResponseVO extends BaseResponse{

	private ArrayList posKeyList;
	private String firstIccID;
	private String firstMsisdn;
	private String firstRegisteredOn;
	private String secondRegisteredOn;
	private String firstCreatedOn;
	private String secondCreatedOn;
	private String firstModifiedOn;
	private String secondModifiedOn;
	private String firstSimProfileID;
	private String secondSimProfileID;
	private String firstNewICCID;
	private String secondNewICCID;
	private String secondIccID;
	private String secondMsisdn;
	private String message1;
	private String message2;
	private String message3;
	private boolean bothAvailableStatus;
	
	
}
