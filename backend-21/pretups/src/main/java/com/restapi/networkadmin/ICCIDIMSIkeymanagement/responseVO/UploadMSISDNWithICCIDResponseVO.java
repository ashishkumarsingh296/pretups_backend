package com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;

import lombok.Data;

@Data
public class UploadMSISDNWithICCIDResponseVO extends BaseResponse {
	private ArrayList errorList;
	private String fileAttachment;
	private String fileName;
	private String fileType;
	private int totalRecords = 0;
	private int validRecords = 0;
	private ErrorMap errorMap;
	private String messageCode;
	private String message;
	private String errorFlag;
}
