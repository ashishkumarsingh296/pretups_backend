package com.restapi.channelAdmin.restrictedlistmgmt.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class UploadFileResponseVO extends BaseResponse{
	
	private ArrayList errorList;
	private String fileAttachment;
	private String fileName;
	private String fileType;
	private int totalRecords = 0;
	private int validRecords = 0;
	private ErrorMap errorMap;
	private String messageCode;
	private String message;
	private String noOfRecords;
	private String errorFlag;
	private String totalFailCount;
	private String processedRecs;
}
