package com.restapi.networkadmin.commissionprofile.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchAddCommisionProfileResponseVO extends BaseResponse {
	private String fileAttachment;
	private int totalRecords = 0;
	private int validRecords = 0;
	private ErrorMap errorMap;
	private String fileType;
	private ArrayList errorList;
	private String errorFlag;
	private String sheetName;
	private String fileName;


}
