package com.restapi.networkadmin.geogrpahycellidmapping.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import lombok.Data;

import java.util.ArrayList;

@Data
public class UploadFileToAssociateCellIdResponseVO extends BaseResponse {
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
	private String noOfRecords;

}