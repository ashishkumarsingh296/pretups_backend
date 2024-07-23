package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkAutoC2CSOSCreditLimitFileResponseVO extends BaseResponse {

	private String fileAttachment;
	private int totalRecords = 0;
	private int validRecords = 0;
	private ErrorMap errorMap;
	private String messageCode;
	private String message;
	private String fileName;
	private String _noOfRecords;
	private ArrayList _errorList = null;
	private String _errorFlag;
	private String fileType;

}
